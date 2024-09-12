package com.example.eatsorderapplication.application.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TestController {

    @GetMapping("/ok")
    public String ok() {

        return "ok";
    }

    @PostMapping("/ok")
    public ResponseEntity<String> okPost() {

        return ResponseEntity.ok("ok");
    }
}