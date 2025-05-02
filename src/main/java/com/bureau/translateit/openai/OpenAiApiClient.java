package com.bureau.translateit.openai;

import com.bureau.translateit.exceptions.OpenAiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class OpenAiApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    public OpenAiApiClient(@Value("${openai.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    public String getLocale(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        String prompt = "Analyze the following text and return its language locale (en-US, es-ES, fr-FR). Return only the locale code.\nText: " + content;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", Collections.singletonList(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "max_tokens", 10,
                "temperature", 0.3
        );

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();
            JsonNode root = objectMapper.readTree(responseBody);
            String locale = root.path("choices").get(0).path("message").path("content").asText();
            return locale.isEmpty() ? "" : locale;
        } catch (Exception e) {
            throw new OpenAiException(e.getMessage());
        }
    }
}
