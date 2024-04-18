package com.example.user.controller;


import com.example.commonawsutil.s3.UrlUtils;
import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("/api/seller")
public class SellerController {
    private String bucketName = "b-";
    private String keyNamePrefix = "k-";

    private final S3Client s3Client;

    private final RestaurantRepository restaurantRepository;

    SellerController(S3Client s3Client, RestaurantRepository restaurantRepository) {
        this.s3Client = s3Client;
        this.restaurantRepository = restaurantRepository;
    }

    //TODO what if load balancer brings a user into wrong instance?
    // -> store this into redis master, and only read from master for this task.
    private final Map<String, String> sessionIdToRestaurantIdMap = new HashMap<>(); // 세션 ID와 레스토랑 ID를 매핑하기 위한 맵

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession() {
        String sessionId = UUID.randomUUID().toString(); // 세션 ID 생성
        String restaurantId = UUID.randomUUID().toString();
        sessionIdToRestaurantIdMap.put(sessionId, restaurantId); // 세션 ID와 레스토랑 ID를 매핑하는 맵에 추가
        return ResponseEntity.ok(sessionId);
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
    public void uploadProfilePictureResized(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId) {
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
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    @PostMapping("/register/restaurant")
    public void registerRestaurant(@RequestParam("sessionId") String sessionId, @org.springframework.web.bind.annotation.RequestBody RestaurantDto restaurantDto) {
        // uploaded restaurant picture
        //TODO if user cancelled register restaurant, we should mark or delete an image.
        var id = sessionIdToRestaurantIdMap.get(sessionId);
        var restaurantEntity = Restaurant.builder()
                .id(id)
                .userId("this_user")
                .name(restaurantDto.getName())
                .type(restaurantDto.getType())
                .openTime(restaurantDto.getOpenTime())
                .closeTime(restaurantDto.getCloseTime())
                .build();
        restaurantRepository.save(restaurantEntity);
    }

    @PostMapping("/register/{restaurantId}/menu")
    public void registerMenu(@PathVariable("restaurantId") String restaurantId) {

    }


}
