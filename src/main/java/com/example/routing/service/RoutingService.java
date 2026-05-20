package com.example.routing.service;

import com.example.routing.exception.RouteNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class RoutingService {

    static final String DEFAULT_COUNTRIES_SOURCE_URL =
            "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final String countriesSourceUrl;
    private final Map<String, List<String>> borderGraph = new HashMap<>();

    public RoutingService(
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader,
            @Value("${routing.countries.source-url:" + DEFAULT_COUNTRIES_SOURCE_URL + "}") String countriesSourceUrl
    ) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.countriesSourceUrl = countriesSourceUrl;
    }

    @PostConstruct
    void loadCountries() throws IOException {
        borderGraph.clear();
        Resource resource = resourceLoader.getResource(countriesSourceUrl);
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode countries = objectMapper.readTree(inputStream);
            for (JsonNode country : countries) {
                String cca3 = country.path("cca3").asText("").toUpperCase(Locale.ROOT);
                if (cca3.isBlank()) {
                    continue;
                }
                List<String> borders = new ArrayList<>();
                JsonNode bordersNode = country.path("borders");
                if (bordersNode.isArray()) {
                    for (JsonNode border : bordersNode) {
                        String code = border.asText("").toUpperCase(Locale.ROOT);
                        if (!code.isBlank()) {
                            borders.add(code);
                        }
                    }
                }
                borderGraph.put(cca3, List.copyOf(borders));
            }
        }
    }

    public List<String> findRoute(String origin, String destination) {
        String originCode = normalize(origin);
        String destinationCode = normalize(destination);

        if (!borderGraph.containsKey(originCode) || !borderGraph.containsKey(destinationCode)) {
            throw new RouteNotFoundException("Unknown country code");
        }

        if (originCode.equals(destinationCode)) {
            return List.of(originCode);
        }

        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previous = new HashMap<>();

        queue.add(originCode);
        visited.add(originCode);

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            for (String neighbor : borderGraph.getOrDefault(current, List.of())) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                previous.put(neighbor, current);

                if (neighbor.equals(destinationCode)) {
                    return buildPath(previous, originCode, destinationCode);
                }
                queue.addLast(neighbor);
            }
        }

        throw new RouteNotFoundException("No land route found");
    }

    private static String normalize(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private static List<String> buildPath(Map<String, String> previous, String origin, String destination) {
        List<String> route = new ArrayList<>();
        String cursor = destination;

        route.add(cursor);
        while (!cursor.equals(origin)) {
            cursor = previous.get(cursor);
            if (cursor == null) {
                throw new RouteNotFoundException("No land route found");
            }
            route.add(cursor);
        }

        Collections.reverse(route);
        return route;
    }
}
