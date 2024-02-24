package com.example.route.controller;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {

    @Autowired
    private GraphHopper graphHopper;

@GetMapping("/")
public void Query() {
//    GHRequest req = new GHRequest()
    graphHopper.route()
}


}
