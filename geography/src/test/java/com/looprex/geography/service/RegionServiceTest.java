package com.looprex.geography.service;

import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.RegionRepository;
import com.looprex.geography.repository.ComunaRepository;
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
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @InjectMocks
    private RegionService regionService;

    private Region testRegion;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setRegionId(1L);
        testRegion.setName("Región Metropolitana");
    }

    @Test
    void getAllRegions_DeberiaRetornarListaDeRegiones() {
        // Arrange
        Region region2 = new Region();
        region2.setRegionId(2L);
        region2.setName("Región de Valparaíso");
        
        List<Region> regions = Arrays.asList(testRegion, region2);
        when(regionRepository.findAll()).thenReturn(regions);

        // Act
        List<Region> result = regionService.getAllRegions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Región Metropolitana", result.get(0).getName());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void getAllRegions_DeberiaRetornarListaVaciaCuandoNoHayRegiones() {
        // Arrange
        when(regionRepository.findAll()).thenReturn(List.of());

        // Act
        List<Region> result = regionService.getAllRegions();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void getRegionById_DeberiaRetornarRegionCuandoExiste() {
        // Arrange
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));

        // Act
        Optional<Region> result = regionService.getRegionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Región Metropolitana", result.get().getName());
        verify(regionRepository, times(1)).findById(1L);
    }

    @Test
    void getRegionById_DeberiaRetornarVacioCuandoNoExiste() {
        // Arrange
        when(regionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Region> result = regionService.getRegionById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(regionRepository, times(1)).findById(999L);
    }

    @Test
        void createRegion_DeberiaCrearRegionExitosamente() {
        // Arrange
        when(regionRepository.findByName("Región Metropolitana")).thenReturn(Optional.empty());
        when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
        
        // Act
        Region result = regionService.createRegion(testRegion);
        
        // Assert
        assertNotNull(result);
        assertEquals("Región Metropolitana", result.getName());
        verify(regionRepository, times(1)).findByName("Región Metropolitana");
        verify(regionRepository, times(1)).save(any(Region.class));
    }

    @Test
    void createRegion_DeberiaLanzarExcepcionCuandoNombreYaExiste() {
        // Arrange
        when(regionRepository.findByName("Región Metropolitana")).thenReturn(Optional.of(testRegion));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> regionService.createRegion(testRegion)
        );
        
        assertEquals("Ya existe una región con ese nombre", exception.getMessage());
        verify(regionRepository, never()).save(any());
    }

    @Test
    void createRegion_DeberiaLanzarExcepcionCuandoNombreEstaVacio() {
        // Arrange
        testRegion.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> regionService.createRegion(testRegion)
        );
        
        assertEquals("El nombre de la región no puede estar vacío", exception.getMessage());
        verify(regionRepository, never()).save(any());
    }

    @Test
    void updateRegion_DeberiaActualizarRegionExitosamente() {
        // Arrange
        Region updatedRegion = new Region();
        updatedRegion.setName("Región Metropolitana Actualizada");
        
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
        when(regionRepository.findByName("Región Metropolitana Actualizada")).thenReturn(Optional.empty());
        when(regionRepository.save(any(Region.class))).thenReturn(testRegion);

        // Act
        Optional<Region> result = regionService.updateRegion(1L, updatedRegion);

        // Assert
        assertTrue(result.isPresent());
        verify(regionRepository, times(1)).save(any(Region.class));
    }

    @Test
    void updateRegion_DeberiaRetornarVacioCuandoRegionNoExiste() {
        // Arrange
        when(regionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Region> result = regionService.updateRegion(999L, testRegion);

        // Assert
        assertFalse(result.isPresent());
        verify(regionRepository, never()).save(any());
    }

    @Test
    void deleteRegion_DeberiaEliminarRegionExitosamente() {
        // Arrange
        when(regionRepository.existsById(1L)).thenReturn(true);
        when(comunaRepository.findByRegionId(1L)).thenReturn(List.of()); // Sin comunas

        // Act
        regionService.deleteRegion(1L);

        // Assert
        verify(regionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRegion_DeberiaLanzarExcepcionCuandoRegionNoExiste() {
        // Arrange
        when(regionRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> regionService.deleteRegion(999L)
        );
       
        assertEquals("Región no encontrada", exception.getMessage());
        verify(regionRepository, never()).deleteById(any());
    }

    @Test
    void existsByName_DeberiaRetornarTrueCuandoExiste() {
        // Arrange
        when(regionRepository.findByName("Región Metropolitana")).thenReturn(Optional.of(testRegion));

        // Act
        boolean result = regionService.existsByName("Región Metropolitana");

        // Assert
        assertTrue(result);
    }

    @Test
    void deleteRegion_DeberiaLanzarExcepcionCuandoTieneComunasAsociadas() {
        // Arrange
        when(regionRepository.existsById(1L)).thenReturn(true);
        
        // Crear comunas mock
        Comuna comuna1 = new Comuna();
        comuna1.setComunaId(1L);
        comuna1.setName("Comuna 1");
        
        Comuna comuna2 = new Comuna();
        comuna2.setComunaId(2L);
        comuna2.setName("Comuna 2");
        
        when(comunaRepository.findByRegionId(1L))
            .thenReturn(List.of(comuna1, comuna2));

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> regionService.deleteRegion(1L)
        );

        assertTrue(exception.getMessage().contains("No se puede eliminar la región"));
        assertTrue(exception.getMessage().contains("2 comuna(s)"));
        verify(regionRepository, never()).deleteById(any());
    }

    @Test
    void existsByName_DeberiaRetornarFalseCuandoNoExiste() {
        // Arrange
        when(regionRepository.findByName("Región Inexistente")).thenReturn(Optional.empty());

        // Act
        boolean result = regionService.existsByName("Región Inexistente");

        // Assert
        assertFalse(result);
    }
}