package com.example.routing.service;

import com.example.routing.exception.RouteNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CountryCodeValidator {

    public RouteRequest validate(String origin, String destination, Map<String, List<String>> borderGraph) {
        String originCode = normalize(origin);
        String destinationCode = normalize(destination);

        if (!borderGraph.containsKey(originCode) || !borderGraph.containsKey(destinationCode)) {
            throw new RouteNotFoundException("Unknown country code");
        }

        return new RouteRequest(originCode, destinationCode);
    }

    private String normalize(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    public record RouteRequest(String originCode, String destinationCode) {
    }
}
