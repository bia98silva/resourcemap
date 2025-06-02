package com.resourcemap.service;

import com.resourcemap.config.RabbitConfig;
import com.resourcemap.model.Match;
import com.resourcemap.model.Need;
import com.resourcemap.model.Donation;
import com.resourcemap.model.Category;
import com.resourcemap.model.Priority;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RabbitMQDemoService {

    private final RabbitTemplate rabbitTemplate;

    // Armazenar mensagens em memória para demonstração
    private final Queue<Map<String, Object>> messageHistory = new ConcurrentLinkedQueue<>();
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final AtomicLong messagesReceived = new AtomicLong(0);
    private final AtomicLong totalMessages = new AtomicLong(0);

    public RabbitMQDemoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTestMessage(String message, String type) {
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", message);
            messageData.put("type", type);
            messageData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            messageData.put("sender", "Demo Producer");

            // Enviar para a fila de notificações
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "notification.demo", messageData);

            // Registrar na história
            addToHistory("SENT", "Test Message", message, type);
            messagesSent.incrementAndGet();
            totalMessages.incrementAndGet();

        } catch (Exception e) {
            addToHistory("ERROR", "Send Failed", e.getMessage(), "ERROR");
            throw e;
        }
    }

    public void sendMatchNotificationDemo() {
        try {
            // Criar objetos demo para simular um match real
            Need demoNeed = createDemoNeed();
            Donation demoDonation = createDemoDonation();
            Match demoMatch = createDemoMatch(demoNeed, demoDonation);

            // Enviar notificação de match
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, "notification.match", demoMatch);

            // Registrar na história
            addToHistory("SENT", "Match Notification",
                    "Match encontrado: " + demoNeed.getTitle() + " ↔ " + demoDonation.getTitle(),
                    "MATCH");

            messagesSent.incrementAndGet();
            totalMessages.incrementAndGet();

        } catch (Exception e) {
            addToHistory("ERROR", "Match Notification Failed", e.getMessage(), "ERROR");
            throw e;
        }
    }

    public void recordReceivedMessage(String type, String content) {
        addToHistory("RECEIVED", type, content, "CONSUMED");
        messagesReceived.incrementAndGet();
    }

    private void addToHistory(String action, String type, String content, String category) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("action", action);
        entry.put("type", type);
        entry.put("content", content);
        entry.put("category", category);
        entry.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        entry.put("id", System.currentTimeMillis());

        messageHistory.offer(entry);

        // Manter apenas os últimos 50 registros
        while (messageHistory.size() > 50) {
            messageHistory.poll();
        }
    }

    public List<Map<String, Object>> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("messagesSent", messagesSent.get());
        stats.put("messagesReceived", messagesReceived.get());
        stats.put("totalMessages", totalMessages.get());
        stats.put("historySize", messageHistory.size());
        stats.put("rabbitMQStatus", "Connected");
        stats.put("lastUpdate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return stats;
    }

    public void clearMessageHistory() {
        messageHistory.clear();
    }

    // Métodos para criar objetos demo
    private Need createDemoNeed() {
        Need need = new Need();
        need.setId(999L);
        need.setTitle("Alimentos para Abrigo Demo");
        need.setDescription("Necessidade de demonstração para RabbitMQ");
        need.setCategory(Category.FOOD);
        need.setPriority(Priority.HIGH);
        need.setLocation("São Paulo, SP");
        need.setQuantity(100);
        need.setUnit("kg");
        return need;
    }

    private Donation createDemoDonation() {
        Donation donation = new Donation();
        donation.setId(999L);
        donation.setTitle("Doação de Alimentos Demo");
        donation.setDescription("Doação de demonstração para RabbitMQ");
        donation.setCategory(Category.FOOD);
        donation.setLocation("São Paulo, SP");
        donation.setQuantity(50);
        donation.setUnit("kg");
        return donation;
    }

    private Match createDemoMatch(Need need, Donation donation) {
        Match match = new Match();
        match.setId(999L);
        match.setNeed(need);
        match.setDonation(donation);
        match.setMatchedQuantity(50);
        match.setCompatibilityScore(0.95);
        return match;
    }
}