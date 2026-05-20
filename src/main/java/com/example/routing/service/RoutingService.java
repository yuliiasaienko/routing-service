package com.example.routing.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoutingService {

    private final CountryGraphProvider countryGraphProvider;
    private final CountryCodeValidator countryCodeValidator;
    private final RoutePathFinder routePathFinder;

    public RoutingService(
            CountryGraphProvider countryGraphProvider,
            CountryCodeValidator countryCodeValidator,
            RoutePathFinder routePathFinder
    ) {
        this.countryGraphProvider = countryGraphProvider;
        this.countryCodeValidator = countryCodeValidator;
        this.routePathFinder = routePathFinder;
    }

    public List<String> findRoute(String origin, String destination) {
        Map<String, List<String>> borderGraph = countryGraphProvider.getBorderGraph();
        CountryCodeValidator.RouteRequest routeRequest =
                countryCodeValidator.validate(origin, destination, borderGraph);

        return routePathFinder.findShortestPath(
                borderGraph,
                routeRequest.originCode(),
                routeRequest.destinationCode()
        );
    }
}
