package com.example.driver;

import com.example.driver.dto.ContactInfoDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableDiscoveryClient // no need for recent spring boot version
//@EnableConfigurationProperties(value={ContactInfoDto.class}) // ComponentScan replaces this
//@EnableFeignClients
@ComponentScan(basePackages = {"com.example"})

public class DriverApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverApplication.class, args);
    }

}
