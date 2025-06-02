package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final MatchingService matchingService;

    public DonationService(DonationRepository donationRepository, MatchingService matchingService) {
        this.donationRepository = donationRepository;
        this.matchingService = matchingService;
    }

    public Donation createDonation(Donation donation) {
        Donation savedDonation = donationRepository.save(donation);
        // Trigger automatic matching
        matchingService.findMatchesForDonation(savedDonation.getId());
        return savedDonation;
    }

    public List<Donation> findAll() {
        return donationRepository.findAll();
    }

    public List<Donation> findAvailableDonations() {
        return donationRepository.findByStatus(DonationStatus.AVAILABLE);
    }

    public List<Donation> findByCategory(Category category) {
        return donationRepository.findByCategory(category);
    }

    public List<Donation> findByLocation(String location) {
        return donationRepository.findByLocationContainingIgnoreCase(location);
    }

    public Optional<Donation> findById(Long id) {
        return donationRepository.findById(id);
    }

    public Donation updateDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    public void markAsExpired() {
        List<Donation> availableDonations = donationRepository.findByStatus(DonationStatus.AVAILABLE);
        LocalDateTime now = LocalDateTime.now();

        for (Donation donation : availableDonations) {
            if (donation.getExpiryDate() != null && donation.getExpiryDate().isBefore(now)) {
                donation.setStatus(DonationStatus.EXPIRED);
                donationRepository.save(donation);
            }
        }
    }
}