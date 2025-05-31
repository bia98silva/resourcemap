package com.resourcemap.controller;

import com.resourcemap.service.AIService;
import com.resourcemap.service.ReportService;
import com.resourcemap.service.NeedService;
import com.resourcemap.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/ai-assistant")
public class AIAssistantController {

    private final AIService aiService;
    private final ReportService reportService;
    private final NeedService needService;
    private final DonationService donationService;

    public AIAssistantController(AIService aiService,
                                 ReportService reportService,
                                 NeedService needService,
                                 DonationService donationService) {
        this.aiService = aiService;
        this.reportService = reportService;
        this.needService = needService;
        this.donationService = donationService;
    }

    @GetMapping
    public String aiAssistantPage(Model model) {
        model.addAttribute("stats", reportService.getDashboardStatistics());
        return "ai/assistant";
    }

    @PostMapping("/generate-description")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateDescription(@RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String category = request.get("category");
            String location = request.get("location");

            String description = aiService.generateNeedDescription(title, category, location);

            Map<String, String> response = new HashMap<>();
            response.put("description", description);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao gerar descrição: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/suggest-matching")
    @ResponseBody
    public ResponseEntity<Map<String, String>> suggestMatching(@RequestBody Map<String, String> request) {
        try {
            String needDescription = request.get("needDescription");
            String donationDescription = request.get("donationDescription");

            String suggestion = aiService.suggestOptimalMatching(needDescription, donationDescription);

            Map<String, String> response = new HashMap<>();
            response.put("suggestion", suggestion);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao gerar sugestão: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/generate-report")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateImpactReport() {
        try {
            Map<String, Object> statistics = reportService.getDashboardStatistics();
            String report = aiService.generateImpactReport(statistics);

            Map<String, String> response = new HashMap<>();
            response.put("report", report);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao gerar relatório: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");

            // Contextualizar a conversa com dados do sistema
            Map<String, Object> systemContext = reportService.getDashboardStatistics();

            String response = aiService.chatWithAssistant(message, systemContext);

            Map<String, String> result = new HashMap<>();
            result.put("response", response);
            result.put("status", "success");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro na conversa: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/optimize")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateOptimizations() {
        try {
            Map<String, Object> statistics = reportService.getDashboardStatistics();
            String suggestions = aiService.generateOptimizationSuggestions(statistics);

            Map<String, String> response = new HashMap<>();
            response.put("suggestions", suggestions);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erro ao gerar otimizações: " + e.getMessage());
            response.put("status", "error");

            return ResponseEntity.status(500).body(response);
        }
    }
}