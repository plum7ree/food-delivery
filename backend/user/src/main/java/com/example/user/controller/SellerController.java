package com.example.user.controller;


import com.example.commonawsutil.s3.GeneratePresignedGetUrlAndRetrieve;
import com.example.user.data.dto.*;
import com.example.user.data.entity.Menu;
import com.example.user.data.entity.Option;
import com.example.user.data.entity.OptionGroup;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.data.repository.AccountRepository;
import com.example.user.service.ImageService;
import com.example.user.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/seller", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class SellerController {
//    private String bucketName = "b-";
//    private String keyNamePrefix = "k-";
    private String bucketName = "b-ubermsa-ap-northeast-2-1";
    private String keyNamePrefix = "k-restaurant-picture";

    private final S3Client s3Client;

    private final AccountRepository accountRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final ImageService imageService;

    public SellerController(S3Client s3Client, AccountRepository accountRepository, RestaurantRepository restaurantRepository, RestaurantService restaurantService, ImageService imageService) {
        this.s3Client = s3Client;
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
        this.imageService = imageService;
    }

    //TODO what if load balancer brings a user into wrong instance?
    // -> store this into redis master, and only read from master for this task.
    private final Map<String, RestaurantDto> sessionIdToRestaurantDtoMap = new HashMap<>(); // 세션 ID와 레스토랑 ID를 매핑하기 위한 맵

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession() {
        log.info("create session");
        String sessionId = UUID.randomUUID().toString(); // 세션 ID 생성
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setId( UUID.randomUUID().toString());
        sessionIdToRestaurantDtoMap.put(sessionId, restaurantDto); // 세션 ID와 레스토랑 ID를 매핑하는 맵에 추가
        return ResponseEntity.ok(sessionId);
    }

    @PostMapping("/register/picture")
    @Transactional
    public ResponseEntity<String> uploadRestaurantPicture(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId, @RequestParam("type") String type) {
        try {
            var _restaurantDto = sessionIdToRestaurantDtoMap.get(sessionId);
            imageService.uploadPictureResized(_restaurantDto.getId(), type, file, 0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("");
    }


    @PostMapping("/register/restaurant")
    @Transactional
    public ResponseEntity<String> registerRestaurant(@org.springframework.web.bind.annotation.RequestBody RestaurantDto restaurantDto) {
        // uploaded restaurant picture
        //TODO if user cancelled register restaurant, we should mark or delete an image.
        var sessionId = restaurantDto.getSessionId();
        var _restaurantDto = sessionIdToRestaurantDtoMap.get(sessionId);
        // enumType validation already done by RestaurantTypeEnum

//        var user = accountRepository.findById(UUID.fromString("13f0c8f0-eec0-4169-ad9f-e8bb408a5325"));
//        if (user.isEmpty()) {
//            return ResponseEntity.badRequest().body("user not found");
//        }

        var menuDtoList = restaurantDto.getMenuDtoList();
        if(menuDtoList.isEmpty()) {
            return ResponseEntity.badRequest().body("no menu exists!");
        }

        var restaurantId = restaurantService.save(restaurantDto);

        sessionIdToRestaurantDtoMap.remove(sessionId);



        return ResponseEntity.ok(restaurantId);
    }

    @PostMapping("/register/{restaurantId}/menu")
    public void registerMenu(@PathVariable("restaurantId") String restaurantId) {
        // 1. validate user authority for this restaurant. admin can also change this.
        // is restaurant user id is same?

    }



//    {
//      "content": [
//        {
//          "name": "Restaurant 1",
//          "type": "BURGER",
//          "openTime": "10:00:00",
//          "closeTime": "22:00:00",
//          "pictureUrl1": "https://presigned-url-for-restaurant-1-picture.example.com"
//        },
//        {
//          "name": "Restaurant 2",
//          "type": "PIZZA",
//          "openTime": "11:00:00",
//          "closeTime": "23:00:00",
//          "pictureUrl1": "https://presigned-url-for-restaurant-2-picture.example.com"
//        },
//        // 더 많은 Restaurant 정보
//      ],
//      "pageable": {
//        "sort": {
//          "sorted": false,
//          "unsorted": true,
//          "empty": true
//        },
//        "offset": 0,
//        "pageNumber": 0,
//        "pageSize": 10,
//        "paged": true,
//        "unpaged": false
//      },
//      "last": true,
//      "totalPages": 1,
//      "totalElements": 10,
//      "size": 10,
//      "number": 0,
//      "sort": {
//        "sorted": false,
//        "unsorted": true,
//        "empty": true
//      },
//      "first": true,
//      "numberOfElements": 10,
//      "empty": false
//    }
    @GetMapping("/restaurants")
    public Page<RestaurantDto> getRestaurantsByType(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        var restaurantDtos =  restaurantRepository.findByType(RestaurantTypeEnum.valueOf(type), pageable);

        // picture 에 대해 presigned URl 생성
        restaurantDtos.stream().forEach(restaurant -> {
            var keyName = restaurant.getPictureUrl1();
            GeneratePresignedGetUrlAndRetrieve presign = new GeneratePresignedGetUrlAndRetrieve();
            String presignedUrlString = "";
            try {
                presignedUrlString = presign.createPresignedGetUrl(bucketName, keyName);
                presign.useHttpUrlConnectionToGet(presignedUrlString);
            } catch (Exception e) {

            }
            restaurant.setPictureUrl1(presignedUrlString);
        });

        return restaurantDtos;
    }

    @GetMapping("/user-registered-restaurant")
    public ResponseEntity<List<RestaurantDto>> getUserRegisteredRestaurantsBty() {
//        User user = getUser();
//        user.getAll
        var restaurants = restaurantRepository.findAll();
//        restaurants.stream().forEach(res -> res.findAllMenu);
        var ret = restaurants.stream().map(restaurant -> RestaurantDto.builder()
                .type(String.valueOf(restaurant.getType()))
                .name(restaurant.getName())
                .build()).toList();

        return ResponseEntity.ok(ret);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Transactional
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurantId") String restaurantId) {
        var res = restaurantRepository.findById(UUID.fromString(restaurantId));
        if(res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        log.info("getRestaurant restairamtEntity: {} " ,res.toString());
        List<Menu> menuList = res.get().getMenuList();
        log.info("getRestaurant: menuList : {}" , menuList.toString());
        //        menuList.get(0)
        menuList.stream().forEach(menu -> menu.getOptionGroupList().stream().forEach(optionGroup -> optionGroup.getOptions()));

        List<MenuDto> menuDtoList = menuList.stream()
            .map(menu -> {
                List<OptionGroup> optionGroupList = menu.getOptionGroupList();
                List<OptionGroupDto> optionGroupDtoList = optionGroupList.stream()
                        .map(optionGroup -> {
                            List<Option> options = optionGroup.getOptions(); // LazyInitializationException 발생
                            List<OptionDto> optionDtoList = options.stream()
                                    .map(option -> OptionDto.builder()
                                            .name(option.getName())
                                            .cost(option.getCost().toString())
                                            .build())
                                    .collect(Collectors.toList());
                            return OptionGroupDto.builder()
                                    .description(optionGroup.getDescription())
                                    .optionDtoList(optionDtoList)
                                    .maxSelectNumber(optionGroup.getMaxSelectNumber())
                                    .isNecessary(optionGroup.isNecessary())
                                    .build();
                        })
                        .collect(Collectors.toList());
                return MenuDto.builder()
                        .name(menu.getName())
                        .price(menu.getPrice().toString())
                        .description(menu.getDescription())
                        .pictureUrl(menu.getPictureUrl())
                        .optionGroupDtoList(optionGroupDtoList)
                        .build();
            })
            .collect(Collectors.toList());


        return ResponseEntity.ok(RestaurantDto.builder().menuDtoList(menuDtoList).build());
    }




}
