package com.example.routing.controller;

import com.example.routing.model.RouteResponse;
import com.example.routing.service.RoutingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/{origin}/{destination}")
    public RouteResponse route(@PathVariable String origin, @PathVariable String destination) {
        return new RouteResponse(routingService.findRoute(origin, destination));
    }
}
