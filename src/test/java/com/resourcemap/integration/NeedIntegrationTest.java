package com.resourcemap.integration;

import com.resourcemap.model.*;
import com.resourcemap.repository.NeedRepository;
import com.resourcemap.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:integrationtestdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class NeedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NeedRepository needRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(UserRole.NGO_MEMBER);
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"NGO_MEMBER"})
    void createNeed_ShouldPersistInDatabase() throws Exception {
        // Arrange
        long initialCount = needRepository.count();

        // Act
        mockMvc.perform(post("/needs/create")
                        .with(csrf())
                        .param("title", "Integration Test Need")
                        .param("description", "Test description for integration")
                        .param("location", "Test City")
                        .param("category", "FOOD")
                        .param("priority", "HIGH")
                        .param("quantity", "200")
                        .param("unit", "kg"))
                .andExpect(status().is3xxRedirection());

        // Assert
        long finalCount = needRepository.count();
        assertEquals(initialCount + 1, finalCount);

        Need savedNeed = needRepository.findAll().get(0);
        assertEquals("Integration Test Need", savedNeed.getTitle());
        assertEquals("Test City", savedNeed.getLocation());
        assertEquals(Category.FOOD, savedNeed.getCategory());
        assertEquals(Priority.HIGH, savedNeed.getPriority());
        assertEquals(200, savedNeed.getQuantity());
    }
}