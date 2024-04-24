package com.example.user.controller;


import com.example.commonawsutil.s3.UrlUtils;
import com.example.user.data.dto.*;
import com.example.user.data.entity.Menu;
import com.example.user.data.entity.Option;
import com.example.user.data.entity.OptionGroup;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public SellerController(S3Client s3Client, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    //TODO what if load balancer brings a user into wrong instance?
    // -> store this into redis master, and only read from master for this task.
    private final Map<UUID, UUID> sessionIdToRestaurantIdMap = new HashMap<>(); // 세션 ID와 레스토랑 ID를 매핑하기 위한 맵

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession() {
        UUID sessionId = UUID.randomUUID(); // 세션 ID 생성
        UUID restaurantId = UUID.randomUUID();
        sessionIdToRestaurantIdMap.put(sessionId, restaurantId); // 세션 ID와 레스토랑 ID를 매핑하는 맵에 추가
        return ResponseEntity.ok(sessionId.toString());
    }
    @PostMapping("/restaurant-picture")
    public void uploadRestaurantPicture(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId) {
        var keyName = keyNamePrefix + "/" + sessionIdToRestaurantIdMap.get(sessionId);
        if(!UrlUtils.checkBucketExists(bucketName, s3Client)) {
            UrlUtils.createBucket(bucketName, s3Client);
        }
        try (InputStream inputStream = file.getInputStream()) {
            if (!file.getContentType().equals("image/png")) {
                // Handle the case where the file is not a PNG image
                throw new IllegalArgumentException("Profile picture must be a PNG image.");
            }
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리 로직 추가
        }
    }

    @PostMapping("/restaurant-picture-resized")
    public ResponseEntity<String> uploadProfilePictureResized(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId) {
        if(!sessionIdToRestaurantIdMap.containsKey(sessionId)) {
            return ResponseEntity.notFound().build();
        }

        var keyName = keyNamePrefix + "/" + sessionIdToRestaurantIdMap.get(sessionId);

        // Check if the bucket exists, create it if not
        if (!UrlUtils.checkBucketExists(bucketName, s3Client)) {
            UrlUtils.createBucket(bucketName, s3Client);
        }
        try (InputStream inputStream = file.getInputStream()) {
            //TODO move this resize part into serverless application (aws lambda or kubernetes fargate)
            BufferedImage originalImage = ImageIO.read(inputStream);

            // Resize the image to a small icon
            BufferedImage resizedImage = resizeImage(originalImage, 200, 200);

            // Convert the resized image to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload the resized image to S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(imageBytes));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return ResponseEntity.ok().build();
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    @PostMapping("/register/restaurant")
    @Transactional
    public ResponseEntity<String> registerRestaurant(@org.springframework.web.bind.annotation.RequestBody RestaurantDto restaurantDto) {
        // uploaded restaurant picture
        //TODO if user cancelled register restaurant, we should mark or delete an image.
        var sessionId = restaurantDto.getSessionId();
        var restaurantId = sessionIdToRestaurantIdMap.get(sessionId);
        // enumType validation already done by RestaurantTypeEnum

        var user = userRepository.findById(UUID.fromString("13f0c8f0-eec0-4169-ad9f-e8bb408a5325"));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("user not found");
        }

        var menuDtoList = restaurantDto.getMenuDtoList();

        var restaurantEntity = Restaurant.builder()
                .id(restaurantId)
                .user(user.get())
                .name(restaurantDto.getName())
                .type(RestaurantTypeEnum.valueOf(restaurantDto.getType()))
                .openTime(restaurantDto.getOpenTime())
                .closeTime(restaurantDto.getCloseTime())
                .pictureUrl1(restaurantDto.getPictureUrl1())
                .pictureUrl2(restaurantDto.getPictureUrl2())
                .build();

        // Menu 엔티티 생성 및 Restaurant 엔티티에 추가
        List<Menu> menuList = menuDtoList.stream()
                .map(menuDto -> Menu.builder()
                        .name(menuDto.getName())
                        .description(menuDto.getDescription())
                        .pictureUrl(menuDto.getPictureUrl())
                        .price(BigInteger.valueOf(Long.parseLong(menuDto.getPrice())))
                        .restaurant(restaurantEntity)
                        .optionGroupList(menuDto.getOptionGroupDtoList().stream()
                                .map(optionGroupDto -> OptionGroup.builder()
                                        .isDuplicatedAllowed(optionGroupDto.isDuplicatedAllowed())
                                        .isNecessary(optionGroupDto.isNecessary())
                                        .options(optionGroupDto.getOptionDtoList().stream()
                                                .map(optionDto -> Option.builder()
                                                        .name(optionDto.getName())
                                                        .cost(BigInteger.valueOf(Long.parseLong(optionDto.getCost())))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        restaurantEntity.setMenuList(menuList);

        var res = restaurantRepository.save(restaurantEntity);
        sessionIdToRestaurantIdMap.remove(sessionId);



        return ResponseEntity.ok(res.getId().toString());
    }

    @PostMapping("/register/{restaurantId}/menu")
    public void registerMenu(@PathVariable("restaurantId") String restaurantId) {
        // 1. validate user authority for this restaurant. admin can also change this.
        // is restaurant user id is same?

    }

    @GetMapping("/registered-restaurant")
    public ResponseEntity<List<RestaurantDto>> getRegisteredRestaurants() {
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
        List<Menu> menuList = res.get().getMenuList();
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
                                            .build())
                                    .collect(Collectors.toList());
                            return OptionGroupDto.builder()
                                    .build();
                        })
                        .collect(Collectors.toList());
                return MenuDto.builder()
                        .name(menu.getName())
                        .build();
            })
            .collect(Collectors.toList());


        return ResponseEntity.ok(RestaurantDto.builder().menuDtoList(menuDtoList).build());
    }




}


//
//@RestController("/api/seller")
//public class SellerController {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private final JPAQueryFactory queryFactory;
//
//    SellerController(S3Client s3Client, RestaurantRepository restaurantRepository, EntityManager entityManager) {
//        this.s3Client = s3Client;
//        this.restaurantRepository = restaurantRepository;
//        this.entityManager = entityManager;
//        this.queryFactory = new JPAQueryFactory(entityManager);
//    }
//
//
//    @GetMapping("/registered-restaurant")
//    public ResponseEntity<List<RestaurantDto>> getRegisteredRestaurants() {
//        TypedQuery<Restaurant> query = entityManager.createQuery("SELECT r FROM Restaurant r", Restaurant.class);
//        List<Restaurant> restaurants = query.getResultList();
//
//        List<RestaurantDto> ret = restaurants.stream()
//                .map(restaurant -> RestaurantDto.builder()
//                        .type(restaurant.getType())
//                        .userId(restaurant.getUserId())
//                        .name(restaurant.getName())
//                        .build())
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(ret);
//    }
//
//    @GetMapping("/restaurant/{restaurantId}")
//    @Transactional
//    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurantId") String restaurantId) {
//        Restaurant restaurant = queryFactory.selectFrom(QRestaurant.restaurant)
//                .leftJoin(QRestaurant.restaurant.menuList, menu).fetchJoin()
//                .leftJoin(menu.optionGroupList, optionGroup).fetchJoin()
//                .leftJoin(optionGroup.options, option).fetchJoin()
//                .where(QRestaurant.restaurant.id.eq(restaurantId))
//                .fetchOne();
//
//        if (restaurant == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<MenuDto> menuDtoList = restaurant.getMenuList().stream()
//                .map(menu -> {
//                    List<OptionGroupDto> optionGroupDtoList = menu.getOptionGroupList().stream()
//                            .map(optionGroup -> {
//                                List<OptionDto> optionDtoList = optionGroup.getOptions().stream()
//                                        .map(option -> OptionDto.builder()
//                                                .name(option.getName())
//                                                .build())
//                                        .collect(Collectors.toList());
//                                return OptionGroupDto.builder()
//                                        .optionDtoList(optionDtoList)
//                                        .build();
//                            })
//                            .collect(Collectors.toList());
//                    return MenuDto.builder()
//                            .name(menu.getName())
//                            .optionGroupDtoList(optionGroupDtoList)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(RestaurantDto.builder()
//                .menuDtoList(menuDtoList)
//                .build());
//    }
