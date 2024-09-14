//package com.example.eatsorderapplication.config.tomcat;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.resource.PathResourceResolver;
//import org.springframework.web.servlet.resource.WebJarsResourceResolver;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }
//
//    // 이게 필요한가?
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/")
//            .resourceChain(false)
//            .addResolver(new WebJarsResourceResolver())
//            .addResolver(new PathResourceResolver());
//    }
//}