package com.looprex.geography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.service.ComunaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComunaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComunaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComunaService comunaService;

    private Comuna testComuna;
    private Region testRegion;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setRegionId(1L);
        testRegion.setName("Región Metropolitana");

        testComuna = new Comuna();
        testComuna.setComunaId(1L);
        testComuna.setName("Santiago");
        testComuna.setRegion(testRegion);
    }

    @Test
    void getAllComunas_DeberiaRetornar200ConListaDeComunas() throws Exception {
        // Arrange
        Comuna comuna2 = new Comuna();
        comuna2.setComunaId(2L);
        comuna2.setName("Providencia");
        comuna2.setRegion(testRegion);
        
        List<Comuna> comunas = Arrays.asList(testComuna, comuna2);
        when(comunaService.getAllComunas()).thenReturn(comunas);

        // Act & Assert
        mockMvc.perform(get("/api/comunas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Santiago"))
                .andExpect(jsonPath("$.data[1].name").value("Providencia"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void getAllComunas_DeberiaRetornar204CuandoNoHayComunas() throws Exception {
        // Arrange
        when(comunaService.getAllComunas()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/comunas"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(204));
    }

    @Test
    void getComunaById_DeberiaRetornar200CuandoComunaExiste() throws Exception {
        // Arrange
        when(comunaService.getComunaById(1L)).thenReturn(Optional.of(testComuna));

        // Act & Assert
        mockMvc.perform(get("/api/comunas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.comunaId").value(1))
                .andExpect(jsonPath("$.data.name").value("Santiago"));
    }

    @Test
    void getComunaById_DeberiaRetornar404CuandoComunaNoExiste() throws Exception {
        // Arrange
        when(comunaService.getComunaById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/comunas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getComunasByRegionId_DeberiaRetornar200ConComunasDeLaRegion() throws Exception {
        // Arrange
        List<Comuna> comunas = Arrays.asList(testComuna);
        when(comunaService.getComunasByRegionId(1L)).thenReturn(comunas);

        // Act & Assert
        mockMvc.perform(get("/api/comunas/region/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void createComuna_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(comunaService.createComuna(any(Comuna.class))).thenReturn(testComuna);

        // Act & Assert
        mockMvc.perform(post("/api/comunas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComuna)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createComuna_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(comunaService.createComuna(any(Comuna.class)))
                .thenThrow(new IllegalArgumentException("Ya existe una comuna con ese nombre"));

        // Act & Assert
        mockMvc.perform(post("/api/comunas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComuna)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateComuna_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(comunaService.updateComuna(any(Long.class), any(Comuna.class)))
                .thenReturn(Optional.of(testComuna));

        // Act & Assert
        mockMvc.perform(put("/api/comunas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComuna)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateComuna_DeberiaRetornar404CuandoComunaNoExiste() throws Exception {
        // Arrange
        when(comunaService.updateComuna(any(Long.class), any(Comuna.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/comunas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComuna)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteComuna_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        when(comunaService.deleteComuna(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/comunas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteComuna_DeberiaRetornar404CuandoComunaNoExiste() throws Exception {
        // Arrange
        when(comunaService.deleteComuna(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/comunas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}