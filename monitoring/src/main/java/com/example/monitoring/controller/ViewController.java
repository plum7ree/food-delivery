package com.example.monitoring.controller;

import com.example.driver.data.dto.LocationDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;


@Controller
public class ViewController {

    HashMap<String, LocationDto> locationMap = new HashMap<>();
//    @MessageMapping("/topic/location")
//    public driverLocationListenerCb(LocationDto location) {
//        locationMap [location.getDriverId()]
//    }

	@GetMapping("/")
	public String listDrivers(Model model) {

        model.addAttribute("locationList", locationMap.values());
		return "locations";
	}




}
