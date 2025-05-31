// src/test/java/com/resourcemap/controller/ApiControllerTest.java
package com.resourcemap.controller;

import com.resourcemap.model.*;
import com.resourcemap.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NeedService needService;

    @MockBean
    private DonationService donationService;

    @MockBean
    private MatchingService matchingService;

    @MockBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getNeeds_ShouldReturnNeedsList() throws Exception {
        // Arrange
        Need need = createTestNeed("API Test Need", Category.FOOD, Priority.HIGH);
        when(needService.findActiveNeeds()).thenReturn(Arrays.asList(need));

        // Act & Assert
        mockMvc.perform(get("/api/needs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("API Test Need"))
                .andExpect(jsonPath("$[0].category").value("FOOD"));
    }

    @Test
    @WithMockUser
    void getDonations_ShouldReturnDonationsList() throws Exception {
        // Arrange
        Donation donation = createTestDonation("API Test Donation", Category.MEDICAL);
        when(donationService.findAvailableDonations()).thenReturn(Arrays.asList(donation));

        // Act & Assert
        mockMvc.perform(get("/api/donations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("API Test Donation"))
                .andExpect(jsonPath("$[0].category").value("MEDICAL"));
    }

    @Test
    @WithMockUser
    void findMatches_ShouldReturnNewMatches() throws Exception {
        // Arrange
        Need need = createTestNeed("Test Need", Category.FOOD, Priority.HIGH);
        Donation donation = createTestDonation("Test Donation", Category.FOOD);
        Match match = new Match(need, donation, 50, 0.8);

        when(matchingService.findAllMatches()).thenReturn(Arrays.asList(match));

        // Act & Assert
        mockMvc.perform(post("/api/matches/find")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].matchedQuantity").value(50))
                .andExpect(jsonPath("$[0].compatibilityScore").value(0.8));
    }

    @Test
    @WithMockUser
    void confirmMatch_ShouldReturnConfirmedMatch() throws Exception {
        // Arrange
        Need need = createTestNeed("Test Need", Category.FOOD, Priority.HIGH);
        Donation donation = createTestDonation("Test Donation", Category.FOOD);
        Match match = new Match(need, donation, 50, 0.8);
        match.setId(1L);
        match.setStatus(MatchStatus.CONFIRMED);

        when(matchingService.confirmMatch(eq(1L), any(String.class))).thenReturn(match);

        // Act & Assert
        mockMvc.perform(post("/api/matches/1/confirm")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Test confirmation notes\"")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    void getStatistics_ShouldReturnStatistics() throws Exception {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNeeds", 15);
        stats.put("totalDonations", 12);
        stats.put("totalMatches", 8);

        when(reportService.getDashboardStatistics()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalNeeds").value(15))
                .andExpect(jsonPath("$.totalDonations").value(12))
                .andExpect(jsonPath("$.totalMatches").value(8));
    }

    @Test
    @WithMockUser
    void getUrgentNeeds_ShouldReturnUrgentNeeds() throws Exception {
        // Arrange
        Need urgentNeed = createTestNeed("Urgent Need", Category.MEDICAL, Priority.CRITICAL);
        when(needService.findUrgentNeeds()).thenReturn(Arrays.asList(urgentNeed));

        // Act & Assert
        mockMvc.perform(get("/api/needs/urgent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Urgent Need"))
                .andExpect(jsonPath("$[0].priority").value("CRITICAL"));
    }

    private Need createTestNeed(String title, Category category, Priority priority) {
        Need need = new Need();
        need.setTitle(title);
        need.setDescription("Test description");
        need.setLocation("Test Location");
        need.setCategory(category);
        need.setPriority(priority);
        need.setStatus(NeedStatus.ACTIVE);
        need.setQuantity(100);
        need.setUnit("units");
        return need;
    }

    private Donation createTestDonation(String title, Category category) {
        Donation donation = new Donation();
        donation.setTitle(title);
        donation.setDescription("Test description");
        donation.setLocation("Test Location");
        donation.setCategory(category);
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setQuantity(50);
        donation.setUnit("units");
        return donation;
    }
}