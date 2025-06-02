package com.resourcemap.controller;

import com.resourcemap.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ReportService reportService;
    private final NeedService needService;
    private final DonationService donationService;
    private final MatchingService matchingService;

    public DashboardController(ReportService reportService,
                               NeedService needService,
                               DonationService donationService,
                               MatchingService matchingService) {
        this.reportService = reportService;
        this.needService = needService;
        this.donationService = donationService;
        this.matchingService = matchingService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", reportService.getDashboardStatistics());
        model.addAttribute("urgentNeeds", needService.findUrgentNeeds());
        model.addAttribute("recentMatches", reportService.getRecentMatches(10));
        model.addAttribute("pendingMatches", matchingService.findPendingMatches());

        return "dashboard/index";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("stats", reportService.getDashboardStatistics());
        model.addAttribute("efficiency", reportService.getMatchingEfficiency());

        return "dashboard/analytics";
    }
}