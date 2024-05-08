package com.example.user.controller;


import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.repository.AccountRepository;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.service.ImageService;
import com.example.user.service.ImageType;
import com.example.user.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/seller", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class SellerController {
    private final S3Client s3Client;
    private final AccountRepository accountRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final ImageService imageService;
    //TODO what if load balancer brings a user into wrong instance?
    // -> store this into redis master, and only read from master for this task.
    private final Map<String, RestaurantDto> sessionIdToRestaurantDtoMap = new HashMap<>(); // 세션 ID와 레스토랑 ID를 매핑하기 위한 맵


    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    private String keyNamePrefix = "k-restaurant-picture";

    public SellerController(S3Client s3Client, AccountRepository accountRepository, RestaurantRepository restaurantRepository, RestaurantService restaurantService, ImageService imageService) {
        this.s3Client = s3Client;
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
        this.imageService = imageService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession() {
        log.info("create session");
        String sessionId = UUID.randomUUID().toString(); // 세션 ID 생성
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setId(UUID.randomUUID());
        sessionIdToRestaurantDtoMap.put(sessionId, restaurantDto); // 세션 ID와 레스토랑 ID를 매핑하는 맵에 추가
        return ResponseEntity.ok(sessionId);
    }

    @PostMapping("/register/picture")
    @Transactional
    public ResponseEntity<String> uploadRestaurantPicture(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId, @RequestParam("type") ImageType type) {
        try {
            var _restaurantDto = sessionIdToRestaurantDtoMap.get(sessionId);
            imageService.uploadPictureResized(_restaurantDto.getId().toString(), type.name(), file, 0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("");
    }


    @PostMapping("/register/restaurant")
    @Transactional
    public ResponseEntity<String> registerRestaurant(@org.springframework.web.bind.annotation.RequestBody RestaurantDto restaurantDto) {

        //TODO if user cancelled register restaurant, we should mark or delete an image.
        var sessionId = restaurantDto.getSessionId();
        restaurantDto.setUserId("13f0c8f0-eec0-4169-ad9f-e8bb408a5325");


        var menuDtoList = restaurantDto.getMenuDtoList();
        if (menuDtoList.isEmpty()) {
            return ResponseEntity.badRequest().body("no menu exists!");
        }

        var restaurantId = restaurantService.saveRestaurantAndAllChildren(restaurantDto);

        sessionIdToRestaurantDtoMap.remove(sessionId);


        return ResponseEntity.ok(restaurantId);
    }

    @PostMapping("/register/{restaurantId}/menu")
    public void registerMenu(@PathVariable("restaurantId") String restaurantId) {
        // 1. validate user authority for this restaurant. admin can also change this.
        // is restaurant user id is same?

    }

    @GetMapping("/restaurants")
    public Page<RestaurantDto> getRestaurantsByType(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        var restaurantDtos = restaurantRepository.findByType(RestaurantTypeEnum.valueOf(type), pageable);

        // picture 에 대해 presigned URl 생성
        restaurantDtos.stream().map(restaurant -> imageService.createPresignedUrlForRestaurant(restaurant)).collect(Collectors.toList());

        return restaurantDtos;
    }

    @GetMapping("/user-registered-restaurant")
    public ResponseEntity<List<RestaurantDto>> getUserRegisteredRestaurantsBty() {
        var restaurants = restaurantRepository.findAll();
        var ret = restaurants.stream().map(restaurant -> RestaurantDto.builder()
                .type(restaurant.getType())
                .name(restaurant.getName())
                .build()).toList();

        return ResponseEntity.ok(ret);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Transactional
    public ResponseEntity<RestaurantDto> getRestaurantAndAllChildren(@PathVariable("restaurantId") String restaurantId) {
        RestaurantDto restaurantDto = null;
        try {
            restaurantDto = restaurantService.findRestaurantById(restaurantId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        log.info("getRestaurant restairamtEntity: {} ", restaurantDto.toString());
        restaurantDto = imageService.createPresignedUrlForRestaurant(restaurantDto);

        var menuDtoList = restaurantService.findMenuAndAllChildrenByRestaurantId(restaurantId).orElse(null);
        menuDtoList = menuDtoList.stream().map(imageService::createPresignedUrlForMenuAndAllChildren).collect(Collectors.toList());
        restaurantDto.setMenuDtoList(menuDtoList);

        log.info("getRestaurant: menuList : {}", menuDtoList.toString());


        return ResponseEntity.ok(restaurantDto);
    }


}
