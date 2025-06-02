package com.resourcemap.controller;

import com.resourcemap.service.ReportService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class HomeController {

    private final ReportService reportService;
    private final MessageSource messageSource;

    public HomeController(ReportService reportService, MessageSource messageSource) {
        this.reportService = reportService;
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public String home(Model model, Locale locale) {
        model.addAttribute("stats", reportService.getDashboardStatistics());
        model.addAttribute("currentLocale", locale.toString());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Locale locale) {
        model.addAttribute("currentLocale", locale.toString());
        return "auth/login";
    }
}