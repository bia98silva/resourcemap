// src/test/java/com/resourcemap/controller/NeedControllerTest.java
package com.resourcemap.controller;

import com.resourcemap.model.*;
import com.resourcemap.service.NeedService;
import com.resourcemap.service.MatchingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NeedController.class)
class NeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NeedService needService;

    @MockBean
    private MatchingService matchingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void listNeeds_ShouldReturnNeedsPage() throws Exception {
        // Arrange
        Need need1 = createTestNeed("Food Aid", Category.FOOD, Priority.HIGH);
        Need need2 = createTestNeed("Medical Supplies", Category.MEDICAL, Priority.CRITICAL);

        when(needService.findActiveNeeds()).thenReturn(Arrays.asList(need1, need2));

        // Act & Assert
        mockMvc.perform(get("/needs"))
                .andExpect(status().isOk())
                .andExpect(view().name("needs/list"))
                .andExpect(model().attributeExists("needs"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @WithMockUser
    void createNeedForm_ShouldReturnCreateForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/needs/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("needs/create"))
                .andExpect(model().attributeExists("need"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @WithMockUser
    void createNeed_ShouldRedirectToNeeds_WhenValid() throws Exception {
        // Arrange
        Need savedNeed = createTestNeed("New Need", Category.FOOD, Priority.HIGH);
        savedNeed.setId(1L);

        when(needService.createNeed(any(Need.class))).thenReturn(savedNeed);

        // Act & Assert
        mockMvc.perform(post("/needs/create")
                        .with(csrf())
                        .param("title", "New Need")
                        .param("description", "Test description")
                        .param("location", "Test Location")
                        .param("category", "FOOD")
                        .param("priority", "HIGH")
                        .param("quantity", "100")
                        .param("unit", "units"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/needs"));
    }

    @Test
    @WithMockUser
    void viewNeed_ShouldReturnNeedView() throws Exception {
        // Arrange
        Need need = createTestNeed("Test Need", Category.FOOD, Priority.HIGH);
        need.setId(1L);

        when(needService.findById(1L)).thenReturn(Optional.of(need));
        when(matchingService.findMatchesByNeed(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/needs/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("needs/view"))
                .andExpect(model().attributeExists("need"))
                .andExpect(model().attributeExists("matches"));
    }

    @Test
    @WithMockUser
    void deleteNeed_ShouldRedirectToNeeds() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/needs/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/needs"));
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
}