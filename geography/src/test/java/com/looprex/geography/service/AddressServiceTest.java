package com.looprex.geography.service;

import com.looprex.geography.client.UserClient;
import com.looprex.geography.model.Address;
import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.repository.AddressRepository;
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
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AddressService addressService;

    private Address testAddress;
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

        testAddress = new Address();
        testAddress.setAddressId(1L);
        testAddress.setStreet("Av. Providencia");
        testAddress.setNumber("1234");
        testAddress.setUserId(1L);
        testAddress.setComuna(testComuna);
    }

    @Test
    void getAllAddresses_DeberiaRetornarListaDeDirecciones() {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressRepository.findAll()).thenReturn(addresses);

        // Act
        List<Address> result = addressService.getAllAddresses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Av. Providencia", result.get(0).getStreet());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void getAddressById_DeberiaRetornarDireccionCuandoExiste() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        Optional<Address> result = addressService.getAddressById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Av. Providencia", result.get().getStreet());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void getAddressesByUserId_DeberiaRetornarDireccionesDelUsuario() {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressRepository.findByUserId(1L)).thenReturn(addresses);

        // Act
        List<Address> result = addressService.getAddressesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userClient, times(1)).userExists(1L);
        verify(addressRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getAddressesByUserId_DeberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        when(userClient.userExists(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> addressService.getAddressesByUserId(999L)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(addressRepository, never()).findByUserId(any());
    }

    @Test
    void createAddress_DeberiaCrearDireccionExitosamente() {
        // Arrange
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(testComuna));
        when(userClient.userExists(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        Address result = addressService.createAddress(testAddress);

        // Assert
        assertNotNull(result);
        assertEquals("Av. Providencia", result.getStreet());
        verify(comunaRepository, times(1)).findById(1L);
        verify(userClient, times(1)).userExists(1L);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressRepository, times(1)).findById(1L);
}

    @Test
    void createAddress_DeberiaLanzarExcepcionCuandoCalleEstaVacia() {
        // Arrange
        testAddress.setStreet("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> addressService.createAddress(testAddress)
        );
        
        assertEquals("La calle no puede estar vacía", exception.getMessage());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void createAddress_DeberiaLanzarExcepcionCuandoComunaNoExiste() {
        // Arrange
        when(comunaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> addressService.createAddress(testAddress)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void createAddress_DeberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(testComuna));
        when(userClient.userExists(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> addressService.createAddress(testAddress)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(comunaRepository, times(1)).findById(1L); // La comuna SÍ se busca
        verify(userClient, times(1)).userExists(1L); //  Se valida el usuario
        verify(addressRepository, never()).save(any()); // NO se guarda
    }

    @Test
    void updateAddress_DeberiaActualizarDireccionExitosamente() {
    // Arrange
        Address updatedAddress = new Address();
        updatedAddress.setStreet("Nueva Calle");
        updatedAddress.setNumber("5678");
        
        //Mockear las dos llamadas a findById
        when(addressRepository.findById(1L))
            .thenReturn(Optional.of(testAddress))  // Primera llamada en map()
            .thenReturn(Optional.of(testAddress)); // Segunda llamada después de save()
    
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        Optional<Address> result = addressService.updateAddress(1L, updatedAddress);

        // Assert
        assertTrue(result.isPresent());
        verify(addressRepository, times(2)).findById(1L);
        verify(addressRepository, times(1)).save(any(Address.class));
}

    @Test
    void deleteAddress_DeberiaEliminarDireccionExitosamente() {
        // Arrange
        when(addressRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = addressService.deleteAddress(1L);

        // Assert
        assertTrue(result);
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAddress_DeberiaRetornarFalsoCuandoDireccionNoExiste() {
        // Arrange
        when(addressRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = addressService.deleteAddress(999L);

        // Assert
        assertFalse(result);
        verify(addressRepository, never()).deleteById(any());
    }
}