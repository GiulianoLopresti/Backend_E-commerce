package com.looprex.shopping.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(@Value("${products-service.url}") String productsServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(productsServiceUrl)
                .build();
    }

    /**
     * Verifica si un producto existe llamando al microservicio products
     * @param productId ID del producto a verificar
     * @return true si existe, false si no existe
     */
    public boolean productExists(Long productId) {
        try {
            this.webClient.get()
                    .uri("/api/products/{id}", productId)
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
     * Obtiene los datos completos de un producto
     * @param productId ID del producto
     * @return JSON del producto como String
     */
    public String getProductById(Long productId) {
        try {
            return this.webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Producto con ID " + productId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de productos: " + e.getMessage());
        }
    }
}