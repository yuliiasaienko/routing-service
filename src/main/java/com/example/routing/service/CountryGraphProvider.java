package com.example.routing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CountryGraphProvider {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final String countriesSourceUrl;
    private Map<String, List<String>> borderGraph = Map.of();

    public CountryGraphProvider(
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader,
            @Value("${routing.countries.source-url}") String countriesSourceUrl
    ) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.countriesSourceUrl = countriesSourceUrl;
    }

    @PostConstruct
    void loadCountries() throws IOException {
        borderGraph = Map.copyOf(parseCountries(readCountriesJson()));
    }

    public Map<String, List<String>> getBorderGraph() {
        return borderGraph;
    }

    private JsonNode readCountriesJson() throws IOException {
        Resource resource = resourceLoader.getResource(countriesSourceUrl);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }

    private Map<String, List<String>> parseCountries(JsonNode countries) {
        Map<String, List<String>> parsedGraph = new HashMap<>();
        for (JsonNode country : countries) {
            String cca3 = extractCode(country.path("cca3"));
            if (cca3.isBlank()) {
                continue;
            }
            parsedGraph.put(cca3, extractBorders(country.path("borders")));
        }
        return parsedGraph;
    }

    private String extractCode(JsonNode node) {
        return node.asText("").toUpperCase(Locale.ROOT);
    }

    private List<String> extractBorders(JsonNode bordersNode) {
        if (!bordersNode.isArray()) {
            return List.of();
        }

        List<String> borders = new ArrayList<>();
        for (JsonNode border : bordersNode) {
            String code = extractCode(border);
            if (!code.isBlank()) {
                borders.add(code);
            }
        }
        return List.copyOf(borders);
    }
}
