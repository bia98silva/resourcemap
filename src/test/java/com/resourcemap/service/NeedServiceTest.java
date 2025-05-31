package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.NeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NeedServiceTest {

    @Mock
    private NeedRepository needRepository;

    @Mock
    private MatchingService matchingService;

    private NeedService needService;

    @BeforeEach
    void setUp() {
        needService = new NeedService(needRepository, matchingService);
    }

    @Test
    void createNeed_ShouldSaveAndTriggerMatching() {
        // Arrange
        Need need = new Need();
        need.setTitle("Emergency Food");
        need.setCategory(Category.FOOD);
        need.setQuantity(100);

        Need savedNeed = new Need();
        savedNeed.setId(1L);
        savedNeed.setTitle("Emergency Food");

        when(needRepository.save(any(Need.class))).thenReturn(savedNeed);

        // Act
        Need result = needService.createNeed(need);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(needRepository).save(need);
        verify(matchingService).findMatchesForNeed(1L);
    }

    @Test
    void findActiveNeeds_ShouldReturnActiveNeeds() {
        // Arrange
        List<Need> activeNeeds = Arrays.asList(
                createTestNeed("Food Aid", NeedStatus.ACTIVE),
                createTestNeed("Medical Supplies", NeedStatus.ACTIVE)
        );

        when(needRepository.findByStatus(NeedStatus.ACTIVE)).thenReturn(activeNeeds);

        // Act
        List<Need> result = needService.findActiveNeeds();

        // Assert
        assertEquals(2, result.size());
        verify(needRepository).findByStatus(NeedStatus.ACTIVE);
    }

    @Test
    void findById_ShouldReturnNeed_WhenExists() {
        // Arrange
        Need need = createTestNeed("Test Need", NeedStatus.ACTIVE);
        when(needRepository.findById(1L)).thenReturn(Optional.of(need));

        // Act
        Optional<Need> result = needService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Need", result.get().getTitle());
    }

    private Need createTestNeed(String title, NeedStatus status) {
        Need need = new Need();
        need.setTitle(title);
        need.setStatus(status);
        need.setCategory(Category.FOOD);
        need.setQuantity(50);
        return need;
    }
}