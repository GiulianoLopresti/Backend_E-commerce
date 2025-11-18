package com.looprex.shopping.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class AddressClient {

    private final WebClient webClient;

    public AddressClient(@Value("${geography-service.url}") String geographyServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(geographyServiceUrl)
                .build();
    }

    /**
     * Verifica si una dirección existe llamando al microservicio geography
     * @param addressId ID de la dirección a verificar
     * @return true si existe, false si no existe
     */
    public boolean addressExists(Long addressId) {
        try {
            this.webClient.get()
                    .uri("/api/addresses/{id}", addressId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de geografía: " + e.getMessage());
        }
    }

    /**
     * Obtiene los datos completos de una dirección
     * @param addressId ID de la dirección
     * @return JSON de la dirección como String
     */
    public String getAddressById(Long addressId) {
        try {
            return this.webClient.get()
                    .uri("/api/addresses/{id}", addressId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Dirección con ID " + addressId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de geografía: " + e.getMessage());
        }
    }
}