package com.resourcemap.service;

import com.resourcemap.model.Match;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final RabbitMQDemoService rabbitMQDemoService;

    public NotificationService(RabbitMQDemoService rabbitMQDemoService) {
        this.rabbitMQDemoService = rabbitMQDemoService;
    }

    @RabbitListener(queues = "notification.queue")
    public void handleMatchNotification(Match match) {
        try {
         
            sendEmailToNGO(match);
            sendEmailToDonor(match);

           
            String content = String.format("Match processado: %s ↔ %s (Score: %.2f)",
                    match.getNeed().getTitle(),
                    match.getDonation().getTitle(),
                    match.getCompatibilityScore());

            rabbitMQDemoService.recordReceivedMessage("Match Notification", content);

     
            System.out.println("✅ CONSUMIDOR: Match notification processada para Match ID: " + match.getId());

        } catch (Exception e) {
            rabbitMQDemoService.recordReceivedMessage("Match Notification Error",
                    "Erro ao processar match: " + e.getMessage());
            System.err.println("❌ ERRO: Falha ao processar notificação de match: " + e.getMessage());
        }
    }

    @RabbitListener(queues = {"notification.queue"})
    public void handleTestMessage(Map<String, Object> message) {
        try {
            String content = message.get("content").toString();
            String type = message.get("type").toString();

         
            rabbitMQDemoService.recordReceivedMessage(type, content);

            
            System.out.println("✅ CONSUMIDOR: Mensagem de teste recebida - " + content);

        } catch (Exception e) {
            rabbitMQDemoService.recordReceivedMessage("Test Message Error",
                    "Erro ao processar mensagem: " + e.getMessage());
            System.err.println("❌ ERRO: Falha ao processar mensagem de teste: " + e.getMessage());
        }
    }

    private void sendEmailToNGO(Match match) {
       
        String ngoName = match.getNeed().getOrganization() != null ?
                match.getNeed().getOrganization().getName() : "ONG Demo";
        System.out.println("📧 Email enviado para ONG: " + ngoName);
    }

    private void sendEmailToDonor(Match match) {
       
        String donorName = match.getDonation().getDonor() != null ?
                match.getDonation().getDonor().getName() : "Doador Demo";
        System.out.println("📧 Email enviado para doador: " + donorName);
    }

    public void sendUrgentNeedAlert(Long needId) {
       
        System.out.println("🚨 Alerta de necessidade urgente enviado para Need ID: " + needId);
        rabbitMQDemoService.recordReceivedMessage("Urgent Alert",
                "Alerta processado para necessidade ID: " + needId);
    }
}
