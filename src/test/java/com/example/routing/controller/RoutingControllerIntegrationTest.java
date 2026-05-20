package com.example.routing.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoutingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsRouteForValidLandPath() throws Exception {
        mockMvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").isArray())
                .andExpect(jsonPath("$.route[0]").value("CZE"))
                .andExpect(jsonPath("$.route").value(hasItem("ITA")));
    }

    @Test
    void returnsBadRequestForNoLandPath() throws Exception {
        mockMvc.perform(get("/routing/AUS/USA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void returnsBadRequestForUnknownCountryCode() throws Exception {
        mockMvc.perform(get("/routing/XXX/ITA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown country code"));
    }
}
