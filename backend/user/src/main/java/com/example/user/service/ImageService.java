package com.example.user.service;

import com.example.commonawsutil.s3.UrlUtils;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.data.repository.AccountRepository;
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
    private String bucketName = "b-ubermsa-ap-northeast-2-1";
    private String keyNamePrefix = "k-restaurant-picture";
        private final S3Client s3Client;

            private final AccountRepository accountRepository;
    private final RestaurantRepository restaurantRepository;

    public ImageService(S3Client s3Client, AccountRepository accountRepository, RestaurantRepository restaurantRepository) {
        this.s3Client = s3Client;
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
    }


    public void uploadPictureResized(String restaurantId, String folder, MultipartFile file, Integer fileIdx) {
                log.info("uploadPictureResized");


        var keyName = keyNamePrefix + "/" + restaurantId + "/" + folder + "/" + file.getName();

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
}
