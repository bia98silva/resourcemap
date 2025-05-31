// src/test/java/com/resourcemap/repository/NeedRepositoryTest.java
package com.resourcemap.repository;

import com.resourcemap.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NeedRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NeedRepository needRepository;

    @Test
    void findByStatus_ShouldReturnNeedsWithSpecificStatus() {
        // Arrange
        Need activeNeed = createTestNeed("Active Need", NeedStatus.ACTIVE);
        Need fulfilledNeed = createTestNeed("Fulfilled Need", NeedStatus.FULFILLED);

        entityManager.persistAndFlush(activeNeed);
        entityManager.persistAndFlush(fulfilledNeed);

        // Act
        List<Need> activeNeeds = needRepository.findByStatus(NeedStatus.ACTIVE);

        // Assert
        assertEquals(1, activeNeeds.size());
        assertEquals("Active Need", activeNeeds.get(0).getTitle());
    }

    @Test
    void findByCategory_ShouldReturnNeedsWithSpecificCategory() {
        // Arrange
        Need foodNeed = createTestNeed("Food Need", NeedStatus.ACTIVE);
        foodNeed.setCategory(Category.FOOD);

        Need medicalNeed = createTestNeed("Medical Need", NeedStatus.ACTIVE);
        medicalNeed.setCategory(Category.MEDICAL);

        entityManager.persistAndFlush(foodNeed);
        entityManager.persistAndFlush(medicalNeed);

        // Act
        List<Need> foodNeeds = needRepository.findByCategory(Category.FOOD);

        // Assert
        assertEquals(1, foodNeeds.size());
        assertEquals("Food Need", foodNeeds.get(0).getTitle());
        assertEquals(Category.FOOD, foodNeeds.get(0).getCategory());
    }

    @Test
    void findActiveNeedsByPriority_ShouldReturnSortedByPriorityAndDate() {
        // Arrange
        Need lowPriorityNeed = createTestNeed("Low Priority", NeedStatus.ACTIVE);
        lowPriorityNeed.setPriority(Priority.LOW);

        Need criticalNeed = createTestNeed("Critical Need", NeedStatus.ACTIVE);
        criticalNeed.setPriority(Priority.CRITICAL);

        Need highPriorityNeed = createTestNeed("High Priority", NeedStatus.ACTIVE);
        highPriorityNeed.setPriority(Priority.HIGH);

        entityManager.persistAndFlush(lowPriorityNeed);
        entityManager.persistAndFlush(criticalNeed);
        entityManager.persistAndFlush(highPriorityNeed);

        // Act
        List<Need> sortedNeeds = needRepository.findActiveNeedsByPriority();

        // Assert
        assertEquals(3, sortedNeeds.size());
        assertEquals(Priority.CRITICAL, sortedNeeds.get(0).getPriority());
        assertEquals(Priority.HIGH, sortedNeeds.get(1).getPriority());
        assertEquals(Priority.LOW, sortedNeeds.get(2).getPriority());
    }

    @Test
    void findByLocationContainingIgnoreCase_ShouldReturnMatchingLocations() {
        // Arrange
        Need nyNeed = createTestNeed("NY Need", NeedStatus.ACTIVE);
        nyNeed.setLocation("New York");

        Need laWeed = createTestNeed("LA Need", NeedStatus.ACTIVE);
        laWeed.setLocation("Los Angeles");

        entityManager.persistAndFlush(nyNeed);
        entityManager.persistAndFlush(laWeed);

        // Act
        List<Need> nyNeeds = needRepository.findByLocationContainingIgnoreCase("york");

        // Assert
        assertEquals(1, nyNeeds.size());
        assertEquals("New York", nyNeeds.get(0).getLocation());
    }

    private Need createTestNeed(String title, NeedStatus status) {
        Need need = new Need();
        need.setTitle(title);
        need.setDescription("Test description");
        need.setLocation("Test Location");
        need.setCategory(Category.OTHER);
        need.setPriority(Priority.MEDIUM);
        need.setStatus(status);
        need.setQuantity(100);
        need.setUnit("units");
        return need;
    }
}