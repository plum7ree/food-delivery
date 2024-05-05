package com.example.user.service;

import com.example.commonawsutil.s3.GeneratePresignedGetUrlAndRetrieve;
import com.example.commonawsutil.s3.UrlUtils;
import com.example.user.data.dto.MenuDto;
import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.repository.AccountRepository;
import com.example.user.data.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ImageService {
    private final S3Client s3Client;
    private final AccountRepository accountRepository;
    private final RestaurantRepository restaurantRepository;
    private String bucketName = "b-ubermsa-ap-northeast-2-1";
    private static String keyNamePrefix = "k-restaurant-picture";

    @FunctionalInterface
    public interface KeyNameGenerator {
        String apply(String restaurantId, String folder, String fileName, Integer fileIdx);
    }
    KeyNameGenerator KeyNameGen = (restaurantId, folder, fileName, fileIdx) -> {
        return keyNamePrefix + "/" + restaurantId + "/" + folder + "/" + fileName;
    };
    public ImageService(S3Client s3Client, AccountRepository accountRepository, RestaurantRepository restaurantRepository) {
        this.s3Client = s3Client;
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
    }


    public void uploadPictureResized(String restaurantId, String folder, MultipartFile file, Integer fileIdx) {
        log.info("uploadPictureResized");

        var keyName = KeyNameGen.apply(restaurantId, folder, file.getName(), fileIdx);

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


    public RestaurantDto createPresignedUrlForRestaurant(RestaurantDto restaurantDto) {

        var keyName = KeyNameGen.apply(restaurantDto.getId().toString(), ImageType.RESTAURANT.name(), restaurantDto.getPictureUrl1(), 0);
            GeneratePresignedGetUrlAndRetrieve presign = new GeneratePresignedGetUrlAndRetrieve();
            String presignedUrlString = "";
            try {
                presignedUrlString = presign.createPresignedGetUrl(bucketName, keyName);
                presign.useHttpUrlConnectionToGet(presignedUrlString);
            } catch (Exception e) {

            }
            restaurantDto.setPictureUrl1(presignedUrlString);
            return restaurantDto;
    }

    public MenuDto createPresignedUrlForMenuAndAllChildren(MenuDto menuDto) {
        var keyName = KeyNameGen.apply(menuDto.getRestaurantId().toString(), ImageType.MENU.name(), menuDto.getPictureUrl(), 0);
            GeneratePresignedGetUrlAndRetrieve presign = new GeneratePresignedGetUrlAndRetrieve();
            String presignedUrlString = "";
            try {
                presignedUrlString = presign.createPresignedGetUrl(bucketName, keyName);
                presign.useHttpUrlConnectionToGet(presignedUrlString);
            } catch (Exception e) {

            }
            menuDto.setPictureUrl(presignedUrlString);
            return menuDto;
    }
}
