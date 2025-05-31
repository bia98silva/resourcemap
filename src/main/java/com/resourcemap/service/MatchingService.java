package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class MatchingService {

    private final NeedRepository needRepository;
    private final DonationRepository donationRepository;
    private final MatchRepository matchRepository;
    private final RabbitTemplate rabbitTemplate;

    public MatchingService(NeedRepository needRepository,
                           DonationRepository donationRepository,
                           MatchRepository matchRepository,
                           RabbitTemplate rabbitTemplate) {
        this.needRepository = needRepository;
        this.donationRepository = donationRepository;
        this.matchRepository = matchRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Match> findAllMatches() {
        List<Need> activeNeeds = needRepository.findActiveNeedsByPriority();
        List<Donation> availableDonations = donationRepository.findAvailableDonations();
        List<Match> newMatches = new ArrayList<>();

        for (Need need : activeNeeds) {
            for (Donation donation : availableDonations) {
                if (isCompatible(need, donation)) {
                    Double score = calculateCompatibilityScore(need, donation);
                    if (score >= 0.7) { // 70% compatibility threshold
                        Match match = new Match(need, donation,
                                Math.min(need.getQuantity(), donation.getQuantity()), score);
                        Match savedMatch = matchRepository.save(match);
                        newMatches.add(savedMatch);

                        // Send notification via RabbitMQ
                        sendMatchNotification(savedMatch);
                    }
                }
            }
        }

        return newMatches;
    }

    public void findMatchesForNeed(Long needId) {
        Optional<Need> needOpt = needRepository.findById(needId);
        if (needOpt.isEmpty()) return;

        Need need = needOpt.get();
        List<Donation> availableDonations = donationRepository.findAvailableDonationsByCategory(need.getCategory());

        for (Donation donation : availableDonations) {
            if (isCompatible(need, donation)) {
                Double score = calculateCompatibilityScore(need, donation);
                if (score >= 0.7) {
                    Match match = new Match(need, donation,
                            Math.min(need.getQuantity(), donation.getQuantity()), score);
                    Match savedMatch = matchRepository.save(match);
                    sendMatchNotification(savedMatch);
                }
            }
        }
    }

    public void findMatchesForDonation(Long donationId) {
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (donationOpt.isEmpty()) return;

        Donation donation = donationOpt.get();
        List<Need> activeNeeds = needRepository.findActiveNeedsByCategory(donation.getCategory());

        for (Need need : activeNeeds) {
            if (isCompatible(need, donation)) {
                Double score = calculateCompatibilityScore(need, donation);
                if (score >= 0.7) {
                    Match match = new Match(need, donation,
                            Math.min(need.getQuantity(), donation.getQuantity()), score);
                    Match savedMatch = matchRepository.save(match);
                    sendMatchNotification(savedMatch);
                }
            }
        }
    }

    private boolean isCompatible(Need need, Donation donation) {
        return need.getCategory().equals(donation.getCategory()) &&
                need.getLocation().equalsIgnoreCase(donation.getLocation());
    }

    private Double calculateCompatibilityScore(Need need, Donation donation) {
        double score = 0.0;

        // Category match (40%)
        if (need.getCategory().equals(donation.getCategory())) {
            score += 0.4;
        }

        // Location proximity (30%)
        if (need.getLocation().equalsIgnoreCase(donation.getLocation())) {
            score += 0.3;
        }

        // Quantity compatibility (20%)
        int neededQty = need.getQuantity();
        int availableQty = donation.getQuantity();
        double qtyRatio = Math.min(neededQty, availableQty) / (double) Math.max(neededQty, availableQty);
        score += 0.2 * qtyRatio;

        // Priority urgency bonus (10%)
        if (need.getPriority() == Priority.CRITICAL) {
            score += 0.1;
        } else if (need.getPriority() == Priority.HIGH) {
            score += 0.05;
        }

        return Math.min(score, 1.0);
    }

    public Match confirmMatch(Long matchId, String notes) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setStatus(MatchStatus.CONFIRMED);
        match.setConfirmedAt(LocalDateTime.now());
        match.setNotes(notes);

        // Update donation status
        Donation donation = match.getDonation();
        donation.setStatus(DonationStatus.RESERVED);
        donationRepository.save(donation);

        return matchRepository.save(match);
    }

    public Match completeMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setStatus(MatchStatus.COMPLETED);

        // Update need and donation status
        Need need = match.getNeed();
        Donation donation = match.getDonation();

        // Update quantities
        int matchedQty = match.getMatchedQuantity();
        need.setQuantity(need.getQuantity() - matchedQty);
        donation.setQuantity(donation.getQuantity() - matchedQty);

        // Check if fully fulfilled
        if (need.getQuantity() <= 0) {
            need.setStatus(NeedStatus.FULFILLED);
        }

        if (donation.getQuantity() <= 0) {
            donation.setStatus(DonationStatus.DONATED);
        }

        needRepository.save(need);
        donationRepository.save(donation);

        return matchRepository.save(match);
    }

    private void sendMatchNotification(Match match) {
        try {
            rabbitTemplate.convertAndSend("resourcemap.exchange", "notification.match", match);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send match notification: " + e.getMessage());
        }
    }

    public List<Match> findPendingMatches() {
        return matchRepository.findByStatus(MatchStatus.PENDING);
    }

    public List<Match> findMatchesByNeed(Long needId) {
        return matchRepository.findByNeedId(needId);
    }

    public List<Match> findMatchesByDonation(Long donationId) {
        return matchRepository.findByDonationId(donationId);
    }
}