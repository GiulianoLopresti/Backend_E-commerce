package com.looprex.shopping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.service.DetailService;
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

@WebMvcTest(DetailController.class)
@AutoConfigureMockMvc(addFilters = false)
class DetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DetailService detailService;

    private Detail testDetail;

    @BeforeEach
    void setUp() {
        testDetail = new Detail();
        testDetail.setDetailId(1L);
        testDetail.setBuyId(1L);
        testDetail.setProductId(1L);
        testDetail.setQuantity(2);
        testDetail.setUnitPrice(1899990);
        testDetail.setSubtotal(3799980);
    }

    @Test
    void getAllDetails_DeberiaRetornar200ConListaDeDetalles() throws Exception {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(detailService.getAllDetails()).thenReturn(details);

        // Act & Assert
        mockMvc.perform(get("/api/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getAllDetails_DeberiaRetornar204CuandoNoHayDetalles() throws Exception {
        // Arrange
        when(detailService.getAllDetails()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/details"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getDetailById_DeberiaRetornar200CuandoDetalleExiste() throws Exception {
        // Arrange
        when(detailService.getDetailById(1L)).thenReturn(Optional.of(testDetail));

        // Act & Assert
        mockMvc.perform(get("/api/details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.detailId").value(1))
                .andExpect(jsonPath("$.data.quantity").value(2));
    }

    @Test
    void getDetailById_DeberiaRetornar404CuandoDetalleNoExiste() throws Exception {
        // Arrange
        when(detailService.getDetailById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/details/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getDetailsByBuy_DeberiaRetornar200ConDetallesDeLaCompra() throws Exception {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(detailService.getDetailsByBuy(1L)).thenReturn(details);

        // Act & Assert
        mockMvc.perform(get("/api/details/buy/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDetailsByBuy_DeberiaRetornar204CuandoCompraNoTieneDetalles() throws Exception {
        // Arrange
        when(detailService.getDetailsByBuy(1L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/details/buy/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getDetailsByBuy_DeberiaRetornar404CuandoCompraNoExiste() throws Exception {
        // Arrange
        when(detailService.getDetailsByBuy(999L))
                .thenThrow(new IllegalArgumentException("La compra con ID 999 no existe"));

        // Act & Assert
        mockMvc.perform(get("/api/details/buy/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getDetailsByProduct_DeberiaRetornar200ConDetallesDelProducto() throws Exception {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(detailService.getDetailsByProduct(1L)).thenReturn(details);

        // Act & Assert
        mockMvc.perform(get("/api/details/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDetailsByProduct_DeberiaRetornar204CuandoProductoNoTieneVentas() throws Exception {
        // Arrange
        when(detailService.getDetailsByProduct(1L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/details/product/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getDetailsByProduct_DeberiaRetornar404CuandoProductoNoExiste() throws Exception {
        // Arrange
        when(detailService.getDetailsByProduct(999L))
                .thenThrow(new IllegalArgumentException("El producto con ID 999 no existe"));

        // Act & Assert
        mockMvc.perform(get("/api/details/product/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createDetail_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(detailService.createDetail(any(Detail.class))).thenReturn(testDetail);

        // Act & Assert
        mockMvc.perform(post("/api/details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDetail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createDetail_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(detailService.createDetail(any(Detail.class)))
                .thenThrow(new IllegalArgumentException("La cantidad debe ser al menos 1"));

        // Act & Assert
        mockMvc.perform(post("/api/details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDetail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateDetail_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(detailService.updateDetail(any(Long.class), any(Detail.class)))
                .thenReturn(Optional.of(testDetail));

        // Act & Assert
        mockMvc.perform(put("/api/details/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDetail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateDetail_DeberiaRetornar404CuandoDetalleNoExiste() throws Exception {
        // Arrange
        when(detailService.updateDetail(any(Long.class), any(Detail.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/details/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDetail)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateDetail_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(detailService.updateDetail(any(Long.class), any(Detail.class)))
                .thenThrow(new IllegalArgumentException("La cantidad debe ser al menos 1"));

        // Act & Assert
        mockMvc.perform(put("/api/details/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDetail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteDetail_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        when(detailService.deleteDetail(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteDetail_DeberiaRetornar404CuandoDetalleNoExiste() throws Exception {
        // Arrange
        when(detailService.deleteDetail(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/details/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}