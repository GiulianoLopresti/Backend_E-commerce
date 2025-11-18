package com.looprex.shopping.service;

import com.looprex.shopping.client.ProductClient;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.repository.BuyRepository;
import com.looprex.shopping.repository.DetailRepository;
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
class DetailServiceTest {

    @Mock
    private DetailRepository detailRepository;

    @Mock
    private BuyRepository buyRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
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
    void getAllDetails_DeberiaRetornarListaDeDetalles() {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(detailRepository.findAll()).thenReturn(details);

        // Act
        List<Detail> result = detailService.getAllDetails();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
        verify(detailRepository, times(1)).findAll();
    }

    @Test
    void getAllDetails_DeberiaRetornarListaVaciaCuandoNoHayDetalles() {
        // Arrange
        when(detailRepository.findAll()).thenReturn(List.of());

        // Act
        List<Detail> result = detailService.getAllDetails();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(detailRepository, times(1)).findAll();
    }

    @Test
    void getDetailById_DeberiaRetornarDetalleCuandoExiste() {
        // Arrange
        when(detailRepository.findById(1L)).thenReturn(Optional.of(testDetail));

        // Act
        Optional<Detail> result = detailService.getDetailById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getQuantity());
        verify(detailRepository, times(1)).findById(1L);
    }

    @Test
    void getDetailById_DeberiaRetornarVacioCuandoNoExiste() {
        // Arrange
        when(detailRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Detail> result = detailService.getDetailById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(detailRepository, times(1)).findById(999L);
    }

    @Test
    void getDetailsByBuy_DeberiaRetornarDetallesDeLaCompra() {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(buyRepository.existsById(1L)).thenReturn(true);
        when(detailRepository.findByBuyId(1L)).thenReturn(details);

        // Act
        List<Detail> result = detailService.getDetailsByBuy(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(buyRepository, times(1)).existsById(1L);
        verify(detailRepository, times(1)).findByBuyId(1L);
    }

    @Test
    void getDetailsByBuy_DeberiaLanzarExcepcionCuandoCompraNoExiste() {
        // Arrange
        when(buyRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.getDetailsByBuy(999L)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(buyRepository, times(1)).existsById(999L);
        verify(detailRepository, never()).findByBuyId(any());
    }

    @Test
    void getDetailsByProduct_DeberiaRetornarDetallesDelProducto() {
        // Arrange
        List<Detail> details = Arrays.asList(testDetail);
        when(productClient.productExists(1L)).thenReturn(true);
        when(detailRepository.findByProductId(1L)).thenReturn(details);

        // Act
        List<Detail> result = detailService.getDetailsByProduct(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productClient, times(1)).productExists(1L);
        verify(detailRepository, times(1)).findByProductId(1L);
    }

    @Test
    void getDetailsByProduct_DeberiaLanzarExcepcionCuandoProductoNoExiste() {
        // Arrange
        when(productClient.productExists(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.getDetailsByProduct(999L)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(productClient, times(1)).productExists(999L);
        verify(detailRepository, never()).findByProductId(any());
    }

    @Test
    void createDetail_DeberiaCrearDetalleExitosamente() {
        // Arrange
        when(buyRepository.existsById(1L)).thenReturn(true);
        when(productClient.productExists(1L)).thenReturn(true);
        when(detailRepository.save(any(Detail.class))).thenReturn(testDetail);

        // Act
        Detail result = detailService.createDetail(testDetail);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        verify(buyRepository, times(1)).existsById(1L);
        verify(productClient, times(1)).productExists(1L);
        verify(detailRepository, times(1)).save(any(Detail.class));
    }

    @Test
    void createDetail_DeberiaLanzarExcepcionCuandoBuyIdEsNull() {
        // Arrange
        testDetail.setBuyId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.createDetail(testDetail)
        );
        
        assertTrue(exception.getMessage().contains("compra"));
        verify(detailRepository, never()).save(any());
    }

    @Test
    void createDetail_DeberiaLanzarExcepcionCuandoProductIdEsNull() {
        // Arrange
        testDetail.setProductId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.createDetail(testDetail)
        );
        
        assertTrue(exception.getMessage().contains("producto"));
        verify(detailRepository, never()).save(any());
    }

    @Test
    void createDetail_DeberiaLanzarExcepcionCuandoCantidadEsMenorA1() {
        // Arrange
        testDetail.setQuantity(0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.createDetail(testDetail)
        );
        
        assertTrue(exception.getMessage().contains("cantidad"));
        verify(detailRepository, never()).save(any());
    }

    @Test
    void createDetail_DeberiaLanzarExcepcionCuandoCompraNoExiste() {
        // Arrange
        when(buyRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.createDetail(testDetail)
        );
        
        assertTrue(exception.getMessage().contains("compra"));
        verify(detailRepository, never()).save(any());
    }

    @Test
    void createDetail_DeberiaLanzarExcepcionCuandoProductoNoExiste() {
        // Arrange
        when(buyRepository.existsById(1L)).thenReturn(true);
        when(productClient.productExists(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detailService.createDetail(testDetail)
        );
        
        assertTrue(exception.getMessage().contains("producto"));
        verify(detailRepository, never()).save(any());
    }

    @Test
    void updateDetail_DeberiaActualizarDetalleExitosamente() {
        // Arrange
        Detail updatedDetail = new Detail();
        updatedDetail.setQuantity(3);
        updatedDetail.setSubtotal(5699970);
        
        when(detailRepository.findById(1L)).thenReturn(Optional.of(testDetail));
        when(detailRepository.save(any(Detail.class))).thenReturn(testDetail);

        // Act
        Optional<Detail> result = detailService.updateDetail(1L, updatedDetail);

        // Assert
        assertTrue(result.isPresent());
        verify(detailRepository, times(1)).save(any(Detail.class));
    }

    @Test
    void updateDetail_DeberiaRetornarVacioCuandoDetalleNoExiste() {
        // Arrange
        when(detailRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Detail> result = detailService.updateDetail(999L, testDetail);

        // Assert
        assertFalse(result.isPresent());
        verify(detailRepository, never()).save(any());
    }

    @Test
    void deleteDetail_DeberiaEliminarDetalleExitosamente() {
        // Arrange
        when(detailRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = detailService.deleteDetail(1L);

        // Assert
        assertTrue(result);
        verify(detailRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDetail_DeberiaRetornarFalsoCuandoDetalleNoExiste() {
        // Arrange
        when(detailRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = detailService.deleteDetail(999L);

        // Assert
        assertFalse(result);
        verify(detailRepository, never()).deleteById(any());
    }
}