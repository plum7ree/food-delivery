package com.example.user.controller;

import com.example.commonawsutil.s3.GeneratePresignedGetUrlAndRetrieve;
import com.example.commonawsutil.s3.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class ProfileController {

    private final S3Client s3Client;
    String keyName = "k-userprofile/userid1.png";

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    ProfileController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostMapping("/profile-picture")
    public void uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        //TODO check png or jpeg
        if (!UrlUtils.checkBucketExists(bucketName, s3Client)) {
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

    @PostMapping("/profile-picture-resized")
    public void uploadProfilePictureResized(@RequestParam("file") MultipartFile file) {
        //TODO check png or jpeg
        // Check if the bucket exists, create it if not
        if (!UrlUtils.checkBucketExists(bucketName, s3Client)) {
            UrlUtils.createBucket(bucketName, s3Client);
        }
        try (InputStream inputStream = file.getInputStream()) {
            //TODO move this resize part into serverless application (aws lambda or kubernetes fargate)
            BufferedImage originalImage = ImageIO.read(inputStream);

            // Resize the image to a small icon
            BufferedImage resizedImage = resizeImage(originalImage, 32, 32);

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

    // Method to resize the image
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @GetMapping("/profile-picture")
    public String getProfilePicturePresignedURL() {


        //TODO set expiration
        GeneratePresignedGetUrlAndRetrieve presign = new GeneratePresignedGetUrlAndRetrieve();
        String presignedUrlString = "";
        try {
            presignedUrlString = presign.createPresignedGetUrl(bucketName, keyName);
//            presign.useHttpUrlConnectionToGet(presignedUrlString);
        } finally {
//            UrlUtils.deleteObject(bucketName, keyName, s3Client);
//            UrlUtils.deleteBucket(bucketName, s3Client);
        }


        return presignedUrlString;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }
}