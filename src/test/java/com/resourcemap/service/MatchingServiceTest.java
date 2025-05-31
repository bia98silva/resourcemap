// src/test/java/com/resourcemap/service/MatchingServiceTest.java
package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private NeedRepository needRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingService(needRepository, donationRepository, matchRepository, rabbitTemplate);
    }

    @Test
    void findAllMatches_ShouldCreateMatches_WhenCompatible() {
        // Arrange
        Need need = createTestNeed("Food Aid", Category.FOOD, "New York", Priority.HIGH, 100);
        Donation donation = createTestDonation("Food Donation", Category.FOOD, "New York", 50);

        when(needRepository.findActiveNeedsByPriority()).thenReturn(Arrays.asList(need));
        when(donationRepository.findAvailableDonations()).thenReturn(Arrays.asList(donation));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match match = invocation.getArgument(0);
            match.setId(1L);
            return match;
        });

        // Act
        List<Match> matches = matchingService.findAllMatches();

        // Assert
        assertEquals(1, matches.size());
        Match match = matches.get(0);
        assertEquals(need, match.getNeed());
        assertEquals(donation, match.getDonation());
        assertEquals(50, match.getMatchedQuantity());
        assertTrue(match.getCompatibilityScore() >= 0.7);

        verify(matchRepository).save(any(Match.class));
        verify(rabbitTemplate).convertAndSend(eq("resourcemap.exchange"), eq("notification.match"), any(Match.class));
    }

    @Test
    void findAllMatches_ShouldNotCreateMatches_WhenIncompatible() {
        // Arrange
        Need need = createTestNeed("Food Aid", Category.FOOD, "New York", Priority.HIGH, 100);
        Donation donation = createTestDonation("Medical Supplies", Category.MEDICAL, "Los Angeles", 50);

        when(needRepository.findActiveNeedsByPriority()).thenReturn(Arrays.asList(need));
        when(donationRepository.findAvailableDonations()).thenReturn(Arrays.asList(donation));

        // Act
        List<Match> matches = matchingService.findAllMatches();

        // Assert
        assertEquals(0, matches.size());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void confirmMatch_ShouldUpdateMatchAndDonationStatus() {
        // Arrange
        Long matchId = 1L;
        String notes = "Test confirmation notes";

        Need need = createTestNeed("Food Aid", Category.FOOD, "New York", Priority.HIGH, 100);
        Donation donation = createTestDonation("Food Donation", Category.FOOD, "New York", 50);
        Match match = new Match(need, donation, 50, 0.8);
        match.setId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        Match result = matchingService.confirmMatch(matchId, notes);

        // Assert
        assertEquals(MatchStatus.CONFIRMED, result.getStatus());
        assertEquals(notes, result.getNotes());
        assertNotNull(result.getConfirmedAt());
        assertEquals(DonationStatus.RESERVED, donation.getStatus());

        verify(donationRepository).save(donation);
        verify(matchRepository).save(match);
    }

    @Test
    void confirmMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        Long matchId = 999L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> matchingService.confirmMatch(matchId, "notes"));
    }

    @Test
    void completeMatch_ShouldUpdateQuantitiesAndStatuses() {
        // Arrange
        Long matchId = 1L;

        Need need = createTestNeed("Food Aid", Category.FOOD, "New York", Priority.HIGH, 100);
        Donation donation = createTestDonation("Food Donation", Category.FOOD, "New York", 50);
        Match match = new Match(need, donation, 50, 0.8);
        match.setId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        Match result = matchingService.completeMatch(matchId);

        // Assert
        assertEquals(MatchStatus.COMPLETED, result.getStatus());
        assertEquals(50, need.getQuantity()); // 100 - 50
        assertEquals(0, donation.getQuantity()); // 50 - 50
        assertEquals(DonationStatus.DONATED, donation.getStatus());

        verify(needRepository).save(need);
        verify(donationRepository).save(donation);
        verify(matchRepository).save(match);
    }

    @Test
    void findMatchesForNeed_ShouldCreateMatchesForSpecificNeed() {
        // Arrange
        Long needId = 1L;
        Need need = createTestNeed("Food Aid", Category.FOOD, "New York", Priority.HIGH, 100);
        need.setId(needId);

        Donation donation = createTestDonation("Food Donation", Category.FOOD, "New York", 50);

        when(needRepository.findById(needId)).thenReturn(Optional.of(need));
        when(donationRepository.findAvailableDonationsByCategory(Category.FOOD))
                .thenReturn(Arrays.asList(donation));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        matchingService.findMatchesForNeed(needId);

        // Assert
        verify(matchRepository).save(any(Match.class));
        verify(rabbitTemplate).convertAndSend(eq("resourcemap.exchange"), eq("notification.match"), any(Match.class));
    }

    private Need createTestNeed(String title, Category category, String location, Priority priority, int quantity) {
        Need need = new Need();
        need.setTitle(title);
        need.setCategory(category);
        need.setLocation(location);
        need.setPriority(priority);
        need.setQuantity(quantity);
        need.setStatus(NeedStatus.ACTIVE);
        need.setUnit("units");
        need.setDescription("Test description");
        return need;
    }

    private Donation createTestDonation(String title, Category category, String location, int quantity) {
        Donation donation = new Donation();
        donation.setTitle(title);
        donation.setCategory(category);
        donation.setLocation(location);
        donation.setQuantity(quantity);
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setUnit("units");
        donation.setDescription("Test description");
        return donation;
    }
}