package com.looprex.shopping.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class StatusClient {

    private final WebClient webClient;

    public StatusClient(@Value("${products-service.url}") String productsServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(productsServiceUrl)
                .build();
    }

    /**
     * Verifica si un estado existe llamando al microservicio products
     * @param statusId ID del estado a verificar
     * @return true si existe, false si no existe
     */
    public boolean statusExists(Long statusId) {
        try {
            this.webClient.get()
                    .uri("/api/statuses/{id}", statusId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de productos: " + e.getMessage());
        }
    }

    /**
     * Obtiene los datos completos de un estado
     * @param statusId ID del estado
     * @return JSON del estado como String
     */
    public String getStatusById(Long statusId) {
        try {
            return this.webClient.get()
                    .uri("/api/statuses/{id}", statusId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Estado con ID " + statusId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de productos: " + e.getMessage());
        }
    }
}