package com.looprex.geography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.geography.model.Region;
import com.looprex.geography.service.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.hamcrest.Matchers.containsString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegionService regionService;

    private Region testRegion;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setRegionId(1L);
        testRegion.setName("Región Metropolitana");
    }

    @Test
    void getAllRegions_DeberiaRetornar200ConListaDeRegiones() throws Exception {
        // Arrange
        Region region2 = new Region();
        region2.setRegionId(2L);
        region2.setName("Región de Valparaíso");
        
        List<Region> regions = Arrays.asList(testRegion, region2);
        when(regionService.getAllRegions()).thenReturn(regions);

        // Act & Assert
        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Región Metropolitana"))
                .andExpect(jsonPath("$.data[1].name").value("Región de Valparaíso"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void getAllRegions_DeberiaRetornar204CuandoNoHayRegiones() throws Exception {
        // Arrange
        when(regionService.getAllRegions()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(204))
                .andExpect(jsonPath("$.message").value("No se encontraron regiones en el sistema"));
    }

    @Test
    void getRegionById_DeberiaRetornar200CuandoRegionExiste() throws Exception {
        // Arrange
        when(regionService.getRegionById(1L)).thenReturn(Optional.of(testRegion));

        // Act & Assert
        mockMvc.perform(get("/api/regions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.regionId").value(1))
                .andExpect(jsonPath("$.data.name").value("Región Metropolitana"));
    }

    @Test
    void getRegionById_DeberiaRetornar404CuandoRegionNoExiste() throws Exception {
        // Arrange
        when(regionService.getRegionById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/regions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void createRegion_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(regionService.createRegion(any(Region.class))).thenReturn(testRegion);

        // Act & Assert
        mockMvc.perform(post("/api/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Región creada exitosamente"));
    }

    @Test
    void createRegion_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(regionService.createRegion(any(Region.class)))
                .thenThrow(new IllegalArgumentException("Ya existe una región con ese nombre"));

        // Act & Assert
        mockMvc.perform(post("/api/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400));
        }

    @Test
    void updateRegion_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(regionService.updateRegion(any(Long.class), any(Region.class)))
                .thenReturn(Optional.of(testRegion));

        // Act & Assert
        mockMvc.perform(put("/api/regions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Región actualizada exitosamente"));
        }

    @Test
    void updateRegion_DeberiaRetornar404CuandoRegionNoExiste() throws Exception {
        // Arrange
        when(regionService.updateRegion(any(Long.class), any(Region.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/regions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRegion)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }
    
    @Test
    void deleteRegion_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        doNothing().when(regionService).deleteRegion(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/regions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Región eliminada exitosamente"));
    }
    
    @Test
    void deleteRegion_DeberiaRetornar404CuandoRegionNoExiste() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Región no encontrada"))
            .when(regionService).deleteRegion(999L);
        // Act & Assert
        mockMvc.perform(delete("/api/regions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Región no encontrada"));
    }
    
    @Test
    void deleteRegion_DeberiaRetornar400CuandoTieneComunasAsociadas() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("No se puede eliminar la región porque tiene 3 comuna(s) asociada(s). Elimina las comunas primero."))
            .when(regionService).deleteRegion(1L);
    // Act & Assert
    mockMvc.perform(delete("/api/regions/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.statusCode").value(400))
            .andExpect(jsonPath("$.message").value(containsString("No se puede eliminar la región")))
            .andExpect(jsonPath("$.message").value(containsString("comuna(s) asociada(s)")));
        }               
}