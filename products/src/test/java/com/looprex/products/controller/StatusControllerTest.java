package com.looprex.products.controller;

import com.looprex.products.model.Status;
import com.looprex.products.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatusService statusService;

    private Status testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new Status();
        testStatus.setStatusId(1L);
        testStatus.setName("Activo");
    }

    @Test
    void getAllStatuses_DeberiaRetornar200ConListaDeEstados() throws Exception {
        // Arrange
        Status status2 = new Status();
        status2.setStatusId(2L);
        status2.setName("Inactivo");
        
        List<Status> statuses = Arrays.asList(testStatus, status2);
        when(statusService.getAllStatuses()).thenReturn(statuses);

        // Act & Assert
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Activo"))
                .andExpect(jsonPath("$.data[1].name").value("Inactivo"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void getAllStatuses_DeberiaRetornar204CuandoNoHayEstados() throws Exception {
        // Arrange
        when(statusService.getAllStatuses()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(204));
    }

    @Test
    void getStatusById_DeberiaRetornar200CuandoEstadoExiste() throws Exception {
        // Arrange
        when(statusService.getStatusById(1L)).thenReturn(Optional.of(testStatus));

        // Act & Assert
        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.statusId").value(1))
                .andExpect(jsonPath("$.data.name").value("Activo"));
    }

    @Test
    void getStatusById_DeberiaRetornar404CuandoEstadoNoExiste() throws Exception {
        // Arrange
        when(statusService.getStatusById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/statuses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}