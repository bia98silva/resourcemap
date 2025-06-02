package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {

    private final NeedRepository needRepository;
    private final DonationRepository donationRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public ReportService(NeedRepository needRepository,
                         DonationRepository donationRepository,
                         MatchRepository matchRepository,
                         UserRepository userRepository) {
        this.needRepository = needRepository;
        this.donationRepository = donationRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

     
        stats.put("totalNeeds", needRepository.count());
        stats.put("totalDonations", donationRepository.count());
        stats.put("totalMatches", matchRepository.count());
        stats.put("totalUsers", userRepository.count());

     
        stats.put("activeNeeds", needRepository.findByStatus(NeedStatus.ACTIVE).size());
        stats.put("availableDonations", donationRepository.findByStatus(DonationStatus.AVAILABLE).size());
        stats.put("pendingMatches", matchRepository.findByStatus(MatchStatus.PENDING).size());

      
        Map<Category, Long> needsByCategory = new HashMap<>();
        Map<Category, Long> donationsByCategory = new HashMap<>();

        for (Category category : Category.values()) {
            needsByCategory.put(category, (long) needRepository.findByCategory(category).size());
            donationsByCategory.put(category, (long) donationRepository.findByCategory(category).size());
        }

        stats.put("needsByCategory", needsByCategory);
        stats.put("donationsByCategory", donationsByCategory);

        return stats;
    }

    public List<Need> getCriticalNeeds() {
        return needRepository.findByPriority(Priority.CRITICAL);
    }

    public List<Match> getRecentMatches(int limit) {
        List<Match> allMatches = matchRepository.findAllOrderByCompatibilityScore();
        return allMatches.stream()
                .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                .limit(limit)
                .toList();
    }

    public double getMatchingEfficiency() {
        long totalNeeds = needRepository.count();
        long fulfilledNeeds = needRepository.findByStatus(NeedStatus.FULFILLED).size();

        if (totalNeeds == 0) return 0.0;
        return (double) fulfilledNeeds / totalNeeds * 100.0;
    }
}
