package com.resourcemap.controller;

import com.resourcemap.service.RabbitMQDemoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/rabbitmq")
public class RabbitMQMonitoringController {

    private final RabbitMQDemoService rabbitMQDemoService;

    public RabbitMQMonitoringController(RabbitMQDemoService rabbitMQDemoService) {
        this.rabbitMQDemoService = rabbitMQDemoService;
    }

    @GetMapping
    public String monitoringPage(Model model) {
        model.addAttribute("messageHistory", rabbitMQDemoService.getMessageHistory());
        model.addAttribute("statistics", rabbitMQDemoService.getStatistics());
        return "rabbitmq/monitoring";
    }

    @PostMapping("/send-test-message")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendTestMessage(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String type = request.getOrDefault("type", "TEST");

            rabbitMQDemoService.sendTestMessage(message, type);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Mensagem enviada para RabbitMQ com sucesso!");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erro ao enviar mensagem: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/trigger-match-notification")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> triggerMatchNotification() {
        try {
            rabbitMQDemoService.sendMatchNotificationDemo();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notificação de match enviada!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erro ao enviar notificação: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(rabbitMQDemoService.getStatistics());
    }

    @GetMapping("/message-history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMessageHistory() {
        Map<String, Object> response = new HashMap<>();
        response.put("messages", rabbitMQDemoService.getMessageHistory());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear-history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearHistory() {
        rabbitMQDemoService.clearMessageHistory();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Histórico limpo com sucesso!");

        return ResponseEntity.ok(response);
    }
}