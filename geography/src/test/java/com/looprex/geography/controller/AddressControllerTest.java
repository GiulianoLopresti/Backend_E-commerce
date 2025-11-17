package com.looprex.geography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.geography.model.Address;
import com.looprex.geography.model.Comuna;
import com.looprex.geography.model.Region;
import com.looprex.geography.service.AddressService;
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

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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
    void getAllAddresses_DeberiaRetornar200ConListaDeDirecciones() throws Exception {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        // Act & Assert
        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getAllAddresses_DeberiaRetornar204CuandoNoHayDirecciones() throws Exception {
        // Arrange
        when(addressService.getAllAddresses()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getAddressById_DeberiaRetornar200CuandoDireccionExiste() throws Exception {
        // Arrange
        when(addressService.getAddressById(1L)).thenReturn(Optional.of(testAddress));

        // Act & Assert
        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.addressId").value(1))
                .andExpect(jsonPath("$.data.street").value("Av. Providencia"));
    }

    @Test
    void getAddressById_DeberiaRetornar404CuandoDireccionNoExiste() throws Exception {
        // Arrange
        when(addressService.getAddressById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/addresses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getAddressesByUserId_DeberiaRetornar200ConDireccionesDelUsuario() throws Exception {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressService.getAddressesByUserId(1L)).thenReturn(addresses);

        // Act & Assert
        mockMvc.perform(get("/api/addresses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAddressesByUserId_DeberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        // Arrange
        when(addressService.getAddressesByUserId(999L))
                .thenThrow(new IllegalArgumentException("El usuario no existe"));

        // Act & Assert
        mockMvc.perform(get("/api/addresses/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createAddress_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(addressService.createAddress(any(Address.class))).thenReturn(testAddress);

        // Act & Assert
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createAddress_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(addressService.createAddress(any(Address.class)))
                .thenThrow(new IllegalArgumentException("La calle no puede estar vacía"));

        // Act & Assert
        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateAddress_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(addressService.updateAddress(any(Long.class), any(Address.class)))
                .thenReturn(Optional.of(testAddress));

        // Act & Assert
        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateAddress_DeberiaRetornar404CuandoDireccionNoExiste() throws Exception {
        // Arrange
        when(addressService.updateAddress(any(Long.class), any(Address.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/addresses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteAddress_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        when(addressService.deleteAddress(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteAddress_DeberiaRetornar404CuandoDireccionNoExiste() throws Exception {
        // Arrange
        when(addressService.deleteAddress(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/addresses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}