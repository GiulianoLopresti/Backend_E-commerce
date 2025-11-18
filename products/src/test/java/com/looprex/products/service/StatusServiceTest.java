package com.looprex.products.service;

import com.looprex.products.model.Status;
import com.looprex.products.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    private Status testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new Status();
        testStatus.setStatusId(1L);
        testStatus.setName("Activo");
    }

    @Test
    void getAllStatuses_DeberiaRetornarListaDeEstados() {
        // Arrange
        Status status2 = new Status();
        status2.setStatusId(2L);
        status2.setName("Inactivo");
        
        List<Status> statuses = Arrays.asList(testStatus, status2);
        when(statusRepository.findAll()).thenReturn(statuses);

        // Act
        List<Status> result = statusService.getAllStatuses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Activo", result.get(0).getName());
        verify(statusRepository, times(1)).findAll();
    }

    @Test
    void getAllStatuses_DeberiaRetornarListaVaciaCuandoNoHayEstados() {
        // Arrange
        when(statusRepository.findAll()).thenReturn(List.of());

        // Act
        List<Status> result = statusService.getAllStatuses();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(statusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_DeberiaRetornarEstadoCuandoExiste() {
        // Arrange
        when(statusRepository.findById(1L)).thenReturn(Optional.of(testStatus));

        // Act
        Optional<Status> result = statusService.getStatusById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Activo", result.get().getName());
        verify(statusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_DeberiaRetornarVacioCuandoNoExiste() {
        // Arrange
        when(statusRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Status> result = statusService.getStatusById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(statusRepository, times(1)).findById(999L);
    }

    @Test
    void existsById_DeberiaRetornarTrueCuandoExiste() {
        // Arrange
        when(statusRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = statusService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(statusRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_DeberiaRetornarFalseCuandoNoExiste() {
        // Arrange
        when(statusRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = statusService.existsById(999L);

        // Assert
        assertFalse(result);
        verify(statusRepository, times(1)).existsById(999L);
    }
}