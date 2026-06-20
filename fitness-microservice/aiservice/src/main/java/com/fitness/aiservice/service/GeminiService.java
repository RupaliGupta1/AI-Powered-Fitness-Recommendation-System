package com.fitness.aiservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", question)
                                }
                        )
                }
        );

        try {
            return webClient.post()
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()

                    // 🔴 HANDLE ERRORS
                    .onStatus(status -> status.is4xxClientError(), response ->
                            response.bodyToMono(String.class)
                                    .map(error -> new RuntimeException("4xx Error: " + error))
                    )
                    .onStatus(status -> status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .map(error -> new RuntimeException("5xx Error: " + error))
                    )

                    .bodyToMono(String.class)

                    // 🟡 RETRY (important for 429)
                    .retryWhen(
                            reactor.util.retry.Retry.backoff(3, java.time.Duration.ofSeconds(2))
                                    .filter(ex -> ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException)
                    )

                    .block();

        } catch (Exception e) {
            System.err.println("Gemini API failed: " + e.getMessage());
            return null; // IMPORTANT: prevent crash
        }
    }
}
