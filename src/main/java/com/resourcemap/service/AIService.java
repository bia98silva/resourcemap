package com.resourcemap.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;


    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateNeedDescription(String title, String category, String location) {
        String prompt = String.format(
                "Como especialista em gestão humanitária, gere uma descrição detalhada e profissional para uma necessidade humanitária com os seguintes dados:\n\n" +
                        "Título: %s\n" +
                        "Categoria: %s\n" +
                        "Localização: %s\n\n" +
                        "A descrição deve:\n" +
                        "- Ser clara e específica sobre o que é necessário\n" +
                        "- Incluir o contexto da situação\n" +
                        "- Explicar a urgência e importância\n" +
                        "- Ser adequada para doadores potenciais\n" +
                        "- Ter entre 100-200 palavras\n" +
                        "- Ser escrita em português brasileiro",
                title, category, location
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String suggestOptimalMatching(String needDescription, String donationDescription) {
        String prompt = String.format(
                "Como especialista em logística humanitária, analise a seguinte necessidade e doação para sugerir a melhor estratégia de correspondência:\n\n" +
                        "NECESSIDADE:\n%s\n\n" +
                        "DOAÇÃO DISPONÍVEL:\n%s\n\n" +
                        "Forneça uma análise detalhada incluindo:\n" +
                        "1. Compatibilidade entre necessidade e doação (0-100%%)\n" +
                        "2. Estratégia de distribuição recomendada\n" +
                        "3. Considerações logísticas importantes\n" +
                        "4. Possíveis desafios e soluções\n" +
                        "5. Cronograma sugerido\n" +
                        "6. Recursos adicionais necessários\n\n" +
                        "Responda em português brasileiro de forma clara e objetiva.",
                needDescription, donationDescription
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String generateImpactReport(Map<String, Object> statistics) {
        String prompt = String.format(
                "Como analista de dados humanitários, crie um relatório de impacto baseado nas seguintes estatísticas do ResourceMap:\n\n" +
                        "DADOS ATUAIS:\n%s\n\n" +
                        "O relatório deve incluir:\n" +
                        "1. RESUMO EXECUTIVO (principais conquistas)\n" +
                        "2. ANÁLISE DE DESEMPENHO (tendências e métricas)\n" +
                        "3. EFICIÊNCIA DE CORRESPONDÊNCIAS (taxa de sucesso)\n" +
                        "4. IMPACTO SOCIAL (beneficiários atendidos)\n" +
                        "5. ÁREAS DE MELHORIA (recomendações)\n" +
                        "6. PRÓXIMOS PASSOS (ações estratégicas)\n\n" +
                        "Use dados quantitativos quando possível e forneça insights acionáveis. " +
                        "Escreva em português brasileiro, de forma profissional e otimista.",
                statistics.toString()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String chatWithAssistant(String userMessage, Map<String, Object> systemContext) {
        String prompt = String.format(
                "Você é um assistente especializado em gestão humanitária e coordenação de doações. " +
                        "Responda às perguntas dos usuários de forma útil, precisa e empática.\n\n" +
                        "CONTEXTO DO SISTEMA ATUAL:\n" +
                        "- Necessidades ativas: %s\n" +
                        "- Doações disponíveis: %s\n" +
                        "- Correspondências pendentes: %s\n" +
                        "- Total de usuários: %s\n\n" +
                        "PERGUNTA DO USUÁRIO:\n%s\n\n" +
                        "INSTRUÇÕES:\n" +
                        "- Seja útil e informativo\n" +
                        "- Use os dados do contexto quando relevante\n" +
                        "- Forneça sugestões práticas\n" +
                        "- Mantenha tom profissional mas acessível\n" +
                        "- Responda em português brasileiro\n" +
                        "- Se não souber algo, seja honesto sobre limitações",
                systemContext.get("activeNeeds"),
                systemContext.get("availableDonations"),
                systemContext.get("pendingMatches"),
                systemContext.get("totalUsers"),
                userMessage
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String generateOptimizationSuggestions(Map<String, Object> statistics) {
        String prompt = String.format(
                "Como consultor em eficiência de operações humanitárias, analise os dados e sugira otimizações:\n\n" +
                        "DADOS DO SISTEMA:\n%s\n\n" +
                        "Forneça recomendações específicas para:\n" +
                        "1. Melhorar taxa de correspondências\n" +
                        "2. Reduzir tempo de resposta\n" +
                        "3. Otimizar distribuição geográfica\n" +
                        "4. Aumentar engajamento de doadores\n" +
                        "5. Implementar automações\n\n" +
                        "Priorize sugestões por impacto e facilidade de implementação.",
                statistics.toString()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
