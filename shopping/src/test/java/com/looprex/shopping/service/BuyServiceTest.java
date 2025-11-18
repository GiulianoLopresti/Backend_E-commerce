package com.looprex.shopping.service;

import com.looprex.shopping.client.AddressClient;
import com.looprex.shopping.client.StatusClient;
import com.looprex.shopping.client.UserClient;
import com.looprex.shopping.model.Buy;
import com.looprex.shopping.repository.BuyRepository;
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
class BuyServiceTest {

    @Mock
    private BuyRepository buyRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private AddressClient addressClient;

    @Mock
    private StatusClient statusClient;

    @InjectMocks
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
    void getAllBuys_DeberiaRetornarListaDeCompras() {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(buyRepository.findAllOrderByBuyDateDesc()).thenReturn(buys);

        // Act
        List<Buy> result = buyService.getAllBuys();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-2025-001", result.get(0).getOrderNumber());
        verify(buyRepository, times(1)).findAllOrderByBuyDateDesc();
    }

    @Test
    void getAllBuys_DeberiaRetornarListaVaciaCuandoNoHayCompras() {
        // Arrange
        when(buyRepository.findAllOrderByBuyDateDesc()).thenReturn(List.of());

        // Act
        List<Buy> result = buyService.getAllBuys();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(buyRepository, times(1)).findAllOrderByBuyDateDesc();
    }

    @Test
    void getBuyById_DeberiaRetornarCompraCuandoExiste() {
        // Arrange
        when(buyRepository.findById(1L)).thenReturn(Optional.of(testBuy));

        // Act
        Optional<Buy> result = buyService.getBuyById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ORD-2025-001", result.get().getOrderNumber());
        verify(buyRepository, times(1)).findById(1L);
    }

    @Test
    void getBuyById_DeberiaRetornarVacioCuandoNoExiste() {
        // Arrange
        when(buyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Buy> result = buyService.getBuyById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(buyRepository, times(1)).findById(999L);
    }

    @Test
    void getBuyByOrderNumber_DeberiaRetornarCompraCuandoExiste() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.of(testBuy));

        // Act
        Optional<Buy> result = buyService.getBuyByOrderNumber("ORD-2025-001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ORD-2025-001", result.get().getOrderNumber());
        verify(buyRepository, times(1)).findByOrderNumber("ORD-2025-001");
    }

    @Test
    void getBuysByUser_DeberiaRetornarComprasDelUsuario() {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(userClient.userExists(1L)).thenReturn(true);
        when(buyRepository.findByUserId(1L)).thenReturn(buys);

        // Act
        List<Buy> result = buyService.getBuysByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userClient, times(1)).userExists(1L);
        verify(buyRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getBuysByUser_DeberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        when(userClient.userExists(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.getBuysByUser(999L)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(userClient, times(1)).userExists(999L);
        verify(buyRepository, never()).findByUserId(any());
    }

    @Test
    void getBuysByStatus_DeberiaRetornarComprasDelEstado() {
        // Arrange
        List<Buy> buys = Arrays.asList(testBuy);
        when(statusClient.statusExists(1L)).thenReturn(true);
        when(buyRepository.findByStatusId(1L)).thenReturn(buys);

        // Act
        List<Buy> result = buyService.getBuysByStatus(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(statusClient, times(1)).statusExists(1L);
        verify(buyRepository, times(1)).findByStatusId(1L);
    }

    @Test
    void createBuy_DeberiaCrearCompraExitosamente() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.empty());
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressClient.addressExists(1L)).thenReturn(true);
        when(statusClient.statusExists(1L)).thenReturn(true);
        when(buyRepository.save(any(Buy.class))).thenReturn(testBuy);

        // Act
        Buy result = buyService.createBuy(testBuy);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-2025-001", result.getOrderNumber());
        verify(userClient, times(1)).userExists(1L);
        verify(addressClient, times(1)).addressExists(1L);
        verify(statusClient, times(1)).statusExists(1L);
        verify(buyRepository, times(1)).save(any(Buy.class));
    }

    @Test
    void createBuy_DeberiaLanzarExcepcionCuandoNumeroOrdenEstaVacio() {
        // Arrange
        testBuy.setOrderNumber("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.createBuy(testBuy)
        );
        
        assertEquals("El número de orden no puede estar vacío", exception.getMessage());
        verify(buyRepository, never()).save(any());
    }

    @Test
    void createBuy_DeberiaLanzarExcepcionCuandoNumeroOrdenYaExiste() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.of(testBuy));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.createBuy(testBuy)
        );
        
        assertTrue(exception.getMessage().contains("Ya existe una compra"));
        verify(buyRepository, never()).save(any());
    }

    @Test
    void createBuy_DeberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.empty());
        when(userClient.userExists(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.createBuy(testBuy)
        );
        
        assertTrue(exception.getMessage().contains("usuario"));
        verify(buyRepository, never()).save(any());
    }

    @Test
    void createBuy_DeberiaLanzarExcepcionCuandoDireccionNoExiste() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.empty());
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressClient.addressExists(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.createBuy(testBuy)
        );
        
        assertTrue(exception.getMessage().contains("dirección"));
        verify(buyRepository, never()).save(any());
    }

    @Test
    void createBuy_DeberiaLanzarExcepcionCuandoEstadoNoExiste() {
        // Arrange
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.empty());
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressClient.addressExists(1L)).thenReturn(true);
        when(statusClient.statusExists(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> buyService.createBuy(testBuy)
        );
        
        assertTrue(exception.getMessage().contains("estado"));
        verify(buyRepository, never()).save(any());
    }

    @Test
    void createBuy_DeberiaEstablecerFechaSiNoViene() {
        // Arrange
        testBuy.setBuyDate(null);
        when(buyRepository.findByOrderNumber("ORD-2025-001")).thenReturn(Optional.empty());
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressClient.addressExists(1L)).thenReturn(true);
        when(statusClient.statusExists(1L)).thenReturn(true);
        when(buyRepository.save(any(Buy.class))).thenReturn(testBuy);

        // Act
        Buy result = buyService.createBuy(testBuy);

        // Assert
        assertNotNull(result.getBuyDate());
        verify(buyRepository, times(1)).save(any(Buy.class));
    }

    @Test
    void updateBuy_DeberiaActualizarCompraExitosamente() {
        // Arrange
        Buy updatedBuy = new Buy();
        updatedBuy.setStatusId(2L);
        
        when(buyRepository.findById(1L)).thenReturn(Optional.of(testBuy));
        when(statusClient.statusExists(2L)).thenReturn(true);
        when(buyRepository.save(any(Buy.class))).thenReturn(testBuy);

        // Act
        Optional<Buy> result = buyService.updateBuy(1L, updatedBuy);

        // Assert
        assertTrue(result.isPresent());
        verify(statusClient, times(1)).statusExists(2L);
        verify(buyRepository, times(1)).save(any(Buy.class));
    }

    @Test
    void updateBuy_DeberiaRetornarVacioCuandoCompraNoExiste() {
        // Arrange
        when(buyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Buy> result = buyService.updateBuy(999L, testBuy);

        // Assert
        assertFalse(result.isPresent());
        verify(buyRepository, never()).save(any());
    }

    @Test
    void deleteBuy_DeberiaEliminarCompraExitosamente() {
        // Arrange
        when(buyRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = buyService.deleteBuy(1L);

        // Assert
        assertTrue(result);
        verify(buyRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBuy_DeberiaRetornarFalsoCuandoCompraNoExiste() {
        // Arrange
        when(buyRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = buyService.deleteBuy(999L);

        // Assert
        assertFalse(result);
        verify(buyRepository, never()).deleteById(any());
    }
}