package com.looprex.shopping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.shopping.model.Buy;
import com.looprex.shopping.service.BuyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuyController.class)
@AutoConfigureMockMvc(addFilters = false)
class BuyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BuyService buyService;

    private Buy testBuy;

    @BeforeEach
    void setUp() {
        testBuy = new Buy();
        testBuy.setBuyId(1L);
        testBuy.setOrderNumber("ORD-2025-001");
        testBuy.setBuyDate(System.currentTimeMillis());
        testBuy.setSubtotal(1899990);
        testBuy.setIva(361098);
        testBuy.setShipping(5990);
        testBuy.setTotal(2267078);
        testBuy.setPaymentMethod("Tarjeta de Débito");
        testBuy.setStatusId(1L);
        testBuy.setAddressId(1L);
        testBuy.setUserId(1L);
    }

    @Test
    void getAllBuys_DeberiaRetornar200ConListaDeCompras() throws Exception {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(buyService.getAllBuys()).thenReturn(buys);

        // Act & Assert
        mockMvc.perform(get("/api/buys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getAllBuys_DeberiaRetornar204CuandoNoHayCompras() throws Exception {
        // Arrange
        when(buyService.getAllBuys()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/buys"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getBuyById_DeberiaRetornar200CuandoCompraExiste() throws Exception {
        // Arrange
        when(buyService.getBuyById(1L)).thenReturn(Optional.of(testBuy));

        // Act & Assert
        mockMvc.perform(get("/api/buys/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.buyId").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-2025-001"));
    }

    @Test
    void getBuyById_DeberiaRetornar404CuandoCompraNoExiste() throws Exception {
        // Arrange
        when(buyService.getBuyById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/buys/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getBuyByOrderNumber_DeberiaRetornar200CuandoCompraExiste() throws Exception {
        // Arrange
        when(buyService.getBuyByOrderNumber("ORD-2025-001")).thenReturn(Optional.of(testBuy));

        // Act & Assert
        mockMvc.perform(get("/api/buys/order/ORD-2025-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-2025-001"));
    }

    @Test
    void getBuyByOrderNumber_DeberiaRetornar404CuandoCompraNoExiste() throws Exception {
        // Arrange
        when(buyService.getBuyByOrderNumber("ORD-9999")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/buys/order/ORD-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getBuysByUser_DeberiaRetornar200ConComprasDelUsuario() throws Exception {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(buyService.getBuysByUser(1L)).thenReturn(buys);

        // Act & Assert
        mockMvc.perform(get("/api/buys/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getBuysByUser_DeberiaRetornar204CuandoUsuarioNoTieneCompras() throws Exception {
        // Arrange
        when(buyService.getBuysByUser(1L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/buys/user/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getBuysByUser_DeberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        // Arrange
        when(buyService.getBuysByUser(999L))
                .thenThrow(new IllegalArgumentException("El usuario con ID 999 no existe"));

        // Act & Assert
        mockMvc.perform(get("/api/buys/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getBuysByStatus_DeberiaRetornar200ConComprasDelEstado() throws Exception {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(buyService.getBuysByStatus(1L)).thenReturn(buys);

        // Act & Assert
        mockMvc.perform(get("/api/buys/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getBuysByStatus_DeberiaRetornar204CuandoNoHayComprasConEseEstado() throws Exception {
        // Arrange
        when(buyService.getBuysByStatus(1L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/buys/status/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createBuy_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(buyService.createBuy(any(Buy.class))).thenReturn(testBuy);

        // Act & Assert
        mockMvc.perform(post("/api/buys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBuy)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createBuy_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(buyService.createBuy(any(Buy.class)))
                .thenThrow(new IllegalArgumentException("El número de orden no puede estar vacío"));

        // Act & Assert
        mockMvc.perform(post("/api/buys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBuy)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateBuy_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(buyService.updateBuy(any(Long.class), any(Buy.class)))
                .thenReturn(Optional.of(testBuy));

        // Act & Assert
        mockMvc.perform(put("/api/buys/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBuy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateBuy_DeberiaRetornar404CuandoCompraNoExiste() throws Exception {
        // Arrange
        when(buyService.updateBuy(any(Long.class), any(Buy.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/buys/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBuy)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateBuy_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(buyService.updateBuy(any(Long.class), any(Buy.class)))
                .thenThrow(new IllegalArgumentException("El estado no existe"));

        // Act & Assert
        mockMvc.perform(put("/api/buys/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBuy)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteBuy_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        when(buyService.deleteBuy(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/buys/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteBuy_DeberiaRetornar404CuandoCompraNoExiste() throws Exception {
        // Arrange
        when(buyService.deleteBuy(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/buys/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}