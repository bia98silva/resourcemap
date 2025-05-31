// src/test/java/com/resourcemap/service/DonationServiceTest.java
package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private MatchingService matchingService;

    private DonationService donationService;

    @BeforeEach
    void setUp() {
        donationService = new DonationService(donationRepository, matchingService);
    }

    @Test
    void createDonation_ShouldSaveAndTriggerMatching() {
        // Arrange
        Donation donation = createTestDonation("Food Donation", Category.FOOD);
        Donation savedDonation = createTestDonation("Food Donation", Category.FOOD);
        savedDonation.setId(1L);

        when(donationRepository.save(any(Donation.class))).thenReturn(savedDonation);

        // Act
        Donation result = donationService.createDonation(donation);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(donationRepository).save(donation);
        verify(matchingService).findMatchesForDonation(1L);
    }

    @Test
    void findAvailableDonations_ShouldReturnAvailableDonations() {
        // Arrange
        List<Donation> availableDonations = Arrays.asList(
                createTestDonation("Food Donation", Category.FOOD),
                createTestDonation("Medical Donation", Category.MEDICAL)
        );

        when(donationRepository.findByStatus(DonationStatus.AVAILABLE)).thenReturn(availableDonations);

        // Act
        List<Donation> result = donationService.findAvailableDonations();

        // Assert
        assertEquals(2, result.size());
        verify(donationRepository).findByStatus(DonationStatus.AVAILABLE);
    }

    @Test
    void findById_ShouldReturnDonation_WhenExists() {
        // Arrange
        Donation donation = createTestDonation("Test Donation", Category.CLOTHING);
        donation.setId(1L);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // Act
        Optional<Donation> result = donationService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Donation", result.get().getTitle());
    }

    @Test
    void markAsExpired_ShouldUpdateExpiredDonations() {
        // Arrange
        Donation expiredDonation = createTestDonation("Expired Donation", Category.FOOD);
        expiredDonation.setExpiryDate(LocalDateTime.now().minusDays(1));
        expiredDonation.setStatus(DonationStatus.AVAILABLE);

        Donation activeDonation = createTestDonation("Active Donation", Category.WATER);
        activeDonation.setExpiryDate(LocalDateTime.now().plusDays(1));
        activeDonation.setStatus(DonationStatus.AVAILABLE);

        when(donationRepository.findByStatus(DonationStatus.AVAILABLE))
                .thenReturn(Arrays.asList(expiredDonation, activeDonation));

        // Act
        donationService.markAsExpired();

        // Assert
        verify(donationRepository).save(expiredDonation);
        assertEquals(DonationStatus.EXPIRED, expiredDonation.getStatus());
    }

    private Donation createTestDonation(String title, Category category) {
        Donation donation = new Donation();
        donation.setTitle(title);
        donation.setCategory(category);
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setQuantity(50);
        donation.setUnit("units");
        donation.setLocation("Test Location");
        donation.setDescription("Test description");
        return donation;
    }
}