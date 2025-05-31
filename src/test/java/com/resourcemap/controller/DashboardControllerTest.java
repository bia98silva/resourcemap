// src/test/java/com/resourcemap/controller/DashboardControllerTest.java
package com.resourcemap.controller;

import com.resourcemap.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private NeedService needService;

    @MockBean
    private DonationService donationService;

    @MockBean
    private MatchingService matchingService;

    @Test
    @WithMockUser
    void dashboard_ShouldReturnDashboardView() throws Exception {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNeeds", 10);
        stats.put("totalDonations", 8);
        stats.put("totalMatches", 5);

        when(reportService.getDashboardStatistics()).thenReturn(stats);
        when(needService.findUrgentNeeds()).thenReturn(Arrays.asList());
        when(reportService.getRecentMatches(10)).thenReturn(Arrays.asList());
        when(matchingService.findPendingMatches()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("urgentNeeds"))
                .andExpect(model().attributeExists("recentMatches"))
                .andExpect(model().attributeExists("pendingMatches"));
    }

    @Test
    @WithMockUser
    void analytics_ShouldReturnAnalyticsView() throws Exception {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNeeds", 10);

        when(reportService.getDashboardStatistics()).thenReturn(stats);
        when(reportService.getMatchingEfficiency()).thenReturn(85.5);

        // Act & Assert
        mockMvc.perform(get("/dashboard/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/analytics"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("efficiency"));
    }
}