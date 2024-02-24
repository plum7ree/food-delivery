package com.example.route.config;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Configuration
public class GraphHopperConfig {

    private static GraphHopper graphHopper;
    private static final String BUCKET_NAME = "<Your S3 Bucket Name>";
    private static final String KEY = "<Your OSM File Key in S3>";

    @Bean
    public GraphHopper graphHopper() {
        // 1. Download OSM file from S3
//        String osmFilePath = downloadFileFromS3(BUCKET_NAME, KEY);
        URL res  = getClass().getClassLoader().getResource("static/seoul-non-military.osm.pbf");

        // 2. Create GraphHopper instance
        return createGraphHopperInstance(res.getPath());
    }
    // 1. read filestream from s3. and store it from file.
//    private static String downloadFileFromS3(String bucketName, String key) {
//        S3Client s3Client = S3ClientBuilder().
//        S3Object s3Object = s3Client.getObject(bucketName, key);
//        InputStream inputStream = s3Object.getObjectContent();
//        File file = new File("local.osm.pbf"); // Temporary local file
//        try (FileOutputStream outputStream = new FileOutputStream(file)) {
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, length);
//            }
//            return file.getAbsolutePath();
//        } catch (IOException e) {
//            throw new RuntimeException("Error while downloading and saving the OSM file", e);
//        }
//    }

    // 2. create graphhopper
    static GraphHopper createGraphHopperInstance(String ghLoc) {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation("target/routing-graph-cache");

        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("car").setVehicle("car").setTurnCosts(false));

        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));

        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();

        return hopper;
    }




}
