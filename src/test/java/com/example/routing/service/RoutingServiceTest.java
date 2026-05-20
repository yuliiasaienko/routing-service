package com.example.routing.service;

import com.example.routing.exception.RouteNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoutingServiceTest {

    @Test
    void findsRouteBetweenCzechiaAndItaly() throws IOException {
        RoutingService service = createService();

        List<String> route = service.findRoute("CZE", "ITA");

        assertTrue(route.size() >= 2);
        assertEquals("CZE", route.get(0));
        assertEquals("ITA", route.get(route.size() - 1));
    }

    @Test
    void throwsWhenNoLandRouteExists() throws IOException {
        RoutingService service = createService();

        assertThrows(RouteNotFoundException.class, () -> service.findRoute("AUS", "USA"));
    }

    private RoutingService createService() throws IOException {
        CountryGraphProvider graphProvider = new CountryGraphProvider(
                new ObjectMapper(),
                new DefaultResourceLoader(),
                "classpath:countries.json"
        );
        graphProvider.loadCountries();

        return new RoutingService(
                graphProvider,
                new CountryCodeValidator(),
                new RoutePathFinder()
        );
    }
}
