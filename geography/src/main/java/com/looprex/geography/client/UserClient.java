package com.looprex.geography.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user-service.url}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public boolean userExists(Long userId) {
        try {
            this.webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object getUserById(Long userId) {
        return this.webClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}