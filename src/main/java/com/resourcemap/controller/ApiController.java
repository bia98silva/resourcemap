package com.resourcemap.controller;

import com.resourcemap.model.*;
import com.resourcemap.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final NeedService needService;
    private final DonationService donationService;
    private final MatchingService matchingService;
    private final ReportService reportService;

    public ApiController(NeedService needService,
                         DonationService donationService,
                         MatchingService matchingService,
                         ReportService reportService) {
        this.needService = needService;
        this.donationService = donationService;
        this.matchingService = matchingService;
        this.reportService = reportService;
    }

    @GetMapping("/needs")
    public ResponseEntity<List<Need>> getNeeds() {
        return ResponseEntity.ok(needService.findActiveNeeds());
    }

    @GetMapping("/donations")
    public ResponseEntity<List<Donation>> getDonations() {
        return ResponseEntity.ok(donationService.findAvailableDonations());
    }

    @GetMapping("/matches")
    public ResponseEntity<List<Match>> getMatches() {
        return ResponseEntity.ok(matchingService.findPendingMatches());
    }

    @PostMapping("/matches/find")
    public ResponseEntity<List<Match>> findMatches() {
        List<Match> matches = matchingService.findAllMatches();
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/matches/{id}/confirm")
    public ResponseEntity<Match> confirmMatch(@PathVariable Long id, @RequestBody String notes) {
        Match match = matchingService.confirmMatch(id, notes);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(reportService.getDashboardStatistics());
    }

    @GetMapping("/needs/urgent")
    public ResponseEntity<List<Need>> getUrgentNeeds() {
        return ResponseEntity.ok(needService.findUrgentNeeds());
    }
}
