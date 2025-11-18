package com.looprex.shopping.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user-service.url}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    /**
     * Verifica si un usuario existe llamando al microservicio users
     * @param userId ID del usuario a verificar
     * @return true si existe, false si no existe
     */

     
    public boolean userExists(Long userId) {
        try {
            this.webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de usuarios: " + e.getMessage());
        }
    }

    /**
     * Obtiene los datos completos de un usuario
     * @param userId ID del usuario
     * @return JSON del usuario como String
     */
    public String getUserById(Long userId) {
        try {
            return this.webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Usuario con ID " + userId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de usuarios: " + e.getMessage());
        }
    }
}