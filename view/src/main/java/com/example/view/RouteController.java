package com.example.view;

import org.springframework.web.bind.annotation.GetMapping;

@GetMapping("/route")
public String showRoute(Model model) {
    // graphhopper에서 좌표 리스트를 가져옵니다.
    List<GHPoint> ghPoints = graphhopperService.getRoute(...);
    List<double[]> coordinates = ghPoints.stream()
                                    .map(p -> new double[]{p.getLat(), p.getLon()})
                                    .collect(Collectors.toList());

    model.addAttribute("coordinates", coordinates);
    return "route";
}