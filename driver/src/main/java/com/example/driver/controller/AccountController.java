//package com.example.driver.controller;
//
//import com.example.driver.dto.ContactInfoDto;
//import com.example.driver.dto.ResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE}) // /accounts 로 돌리는건 gateway 에서?
//@Validated
//@RequiredArgsConstructor
//public class AccountController {
//    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
//
//    @Autowired
//    private final ContactInfoDto contactInfoDto;
//
//    @GetMapping("/contact-info")
//    public ResponseEntity<ResponseDto> getContactInfo() {
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("200", contactInfoDto.getMessage()));
//    }
//}
