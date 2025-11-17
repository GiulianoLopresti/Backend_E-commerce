package com.looprex.geography.service;

import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.ComunaRepository;
import com.looprex.geography.repository.RegionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunaServiceTest {

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
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
    void getAllComunas_DeberiaRetornarListaDeComunas() {
        // Arrange
        Comuna comuna2 = new Comuna();
        comuna2.setComunaId(2L);
        comuna2.setName("Providencia");
        comuna2.setRegion(testRegion);
        
        List<Comuna> comunas = Arrays.asList(testComuna, comuna2);
        when(comunaRepository.findAll()).thenReturn(comunas);

        // Act
        List<Comuna> result = comunaService.getAllComunas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Santiago", result.get(0).getName());
        verify(comunaRepository, times(1)).findAll();
    }

    @Test
    void getComunaById_DeberiaRetornarComunaCuandoExiste() {
        // Arrange
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(testComuna));

        // Act
        Optional<Comuna> result = comunaService.getComunaById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Santiago", result.get().getName());
        verify(comunaRepository, times(1)).findById(1L);
    }

    @Test
    void getComunasByRegionId_DeberiaRetornarComunasDeLaRegion() {
        // Arrange
        List<Comuna> comunas = Arrays.asList(testComuna);
        when(comunaRepository.findByRegionId(1L)).thenReturn(comunas);

        // Act
        List<Comuna> result = comunaService.getComunasByRegionId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Santiago", result.get(0).getName());
        verify(comunaRepository, times(1)).findByRegionId(1L);
    }

    @Test
    void createComuna_DeberiaCrearComunaExitosamente() {
        // Arrange
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
        when(comunaRepository.findByNameAndRegionId("Santiago", 1L)).thenReturn(Optional.empty());
        when(comunaRepository.save(any(Comuna.class))).thenReturn(testComuna);
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(testComuna));

        // Act
        Comuna result = comunaService.createComuna(testComuna);

        // Assert
        assertNotNull(result);
        assertEquals("Santiago", result.getName());
        verify(regionRepository, times(1)).findById(1L);
        verify(comunaRepository, times(1)).findByNameAndRegionId("Santiago", 1L);
        verify(comunaRepository, times(1)).save(any(Comuna.class));
        verify(comunaRepository, times(1)).findById(1L);
}

    @Test
    void createComuna_DeberiaLanzarExcepcionCuandoRegionNoExiste() {
        // Arrange
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> comunaService.createComuna(testComuna)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(comunaRepository, never()).save(any());
    }

    @Test
    void createComuna_DeberiaLanzarExcepcionCuandoNombreYaExisteEnRegion() {
        // Arrange
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
        when(comunaRepository.findByNameAndRegionId("Santiago", 1L)).thenReturn(Optional.of(testComuna));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> comunaService.createComuna(testComuna)
        );
    
        assertEquals("Ya existe una comuna con ese nombre en esta región", exception.getMessage());
        verify(regionRepository, times(1)).findById(1L); //
        verify(comunaRepository, times(1)).findByNameAndRegionId("Santiago", 1L); // 
        verify(comunaRepository, never()).save(any()); // 
    }

    @Test
    void createComuna_DeberiaLanzarExcepcionCuandoNombreEstaVacio() {
        // Arrange
        testComuna.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> comunaService.createComuna(testComuna)
        );
        
        assertEquals("El nombre de la comuna no puede estar vacío", exception.getMessage());
        verify(comunaRepository, never()).save(any());
    }

    @Test
    void updateComuna_DeberiaActualizarComunaExitosamente() {
        // Arrange
        Comuna updatedComuna = new Comuna();
        updatedComuna.setName("Santiago Centro");
    
        when(comunaRepository.findById(1L))
            .thenReturn(Optional.of(testComuna))  // Primera llamada en map()
            .thenReturn(Optional.of(testComuna)); // Segunda llamada después de save()
        
        when(comunaRepository.findByNameAndRegionId("Santiago Centro", 1L)).thenReturn(Optional.empty());
        when(comunaRepository.save(any(Comuna.class))).thenReturn(testComuna);

        // Act
        Optional<Comuna> result = comunaService.updateComuna(1L, updatedComuna);

        // Assert
        assertTrue(result.isPresent());
        verify(comunaRepository, times(2)).findById(1L);
        verify(comunaRepository, times(1)).save(any(Comuna.class));
    }

    @Test
    void deleteComuna_DeberiaEliminarComunaExitosamente() {
        // Arrange
        when(comunaRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = comunaService.deleteComuna(1L);

        // Assert
        assertTrue(result);
        verify(comunaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComuna_DeberiaRetornarFalsoCuandoComunaNoExiste() {
        // Arrange
        when(comunaRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = comunaService.deleteComuna(999L);

        // Assert
        assertFalse(result);
        verify(comunaRepository, never()).deleteById(any());
    }
}