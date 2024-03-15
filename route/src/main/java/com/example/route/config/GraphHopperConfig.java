package com.example.route.config;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(GraphHopperConfig.class);

    // 파일 바꿀때마다 mvn clean 해서 cache 지워줘야함 !
    @Bean
    public GraphHopper graphHopper() {
        // 1. Download OSM file from S3
        // String osmFilePath = downloadFileFromS3(BUCKET_NAME, KEY);

        // Seoul
         URL res  = getClass().getClassLoader().getResource("static/seoul-non-military.osm.pbf");

        // NewYork
        // URL res  = getClass().getClassLoader().getResource("static/new-york-latest.osm.pbf");

        // andorra
        // https://github.com/graphhopper/graphhopper/blob/master/example/src/main/java/com/graphhopper/example/RoutingExample.java
        // URL res  = getClass().getClassLoader().getResource("static/andorra-latest.osm.pbf");

        log.info("osm URL: " + res);

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


    // https://github.com/graphhopper/graphhopper/blob/master/example/src/main/java/com/graphhopper/example/RoutingExample.java
    static GraphHopper createGraphHopperInstance(String ghLoc) {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        hopper.setGraphHopperLocation("target/routing-graph-cache");
        hopper.setProfiles(new Profile("car").setVehicle("car").setTurnCosts(false));
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        hopper.importOrLoad();

        return hopper;
    }




}
