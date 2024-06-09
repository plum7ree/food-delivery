package com.example.eatssearch.controller;


import com.example.eatssearch.data.dto.RestaurantDto;
import com.example.eatssearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDto>> search(@RequestParam String text) throws IOException {

        var list = searchService.search(text);
        return ResponseEntity.ok(list);

    }


}
