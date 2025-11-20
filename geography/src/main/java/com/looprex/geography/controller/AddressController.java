package com.looprex.geography.controller;

import com.looprex.geography.dto.AddressResponse;
import com.looprex.geography.dto.ApiResponse;
import com.looprex.geography.mapper.AddressMapper;
import com.looprex.geography.model.Address;
import com.looprex.geography.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Direcciones", description = "API para gestión de direcciones")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;  // Agregar mapper

    private static final String ADDRESS_NOT_FOUND = "Dirección no encontrada";

    public AddressController(AddressService addressService, AddressMapper addressMapper) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;  // Inyectar mapper
    }

    @Operation(summary = "Obtener todas las direcciones")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        
        if (addresses.isEmpty()) {
            ApiResponse<List<AddressResponse>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron direcciones en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        // Mapear a DTOs
        List<AddressResponse> addressResponses = addresses.stream()
                .map(addressMapper::toAddressResponse)
                .toList();
        
        ApiResponse<List<AddressResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Direcciones obtenidas exitosamente",
            addressResponses,
            (long) addressResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener dirección por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(@PathVariable Long id) {
        Optional<Address> addressOpt = addressService.getAddressById(id);
        
        if (addressOpt.isPresent()) {
            // Mapear a DTO
            AddressResponse addressResponse = addressMapper.toAddressResponse(addressOpt.get());
            
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Dirección encontrada",
                addressResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ADDRESS_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Obtener direcciones por usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddressesByUserId(@PathVariable Long userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        
        // Mapear a DTOs
        List<AddressResponse> addressResponses = addresses.stream()
                .map(addressMapper::toAddressResponse)
                .toList();
        
        ApiResponse<List<AddressResponse>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Direcciones obtenidas exitosamente",
            addressResponses,
            (long) addressResponses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear nueva dirección")
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@RequestBody Address address) {
        try {
            Address created = addressService.createAddress(address);
            
            // Mapear a DTO
            AddressResponse addressResponse = addressMapper.toAddressResponse(created);
            
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Dirección creada exitosamente",
                addressResponse
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Actualizar dirección")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(@PathVariable Long id, @RequestBody Address updatedAddress) {
        Optional<Address> addressOpt = addressService.updateAddress(id, updatedAddress);
        
        if (addressOpt.isPresent()) {
            // Mapear a DTO
            AddressResponse addressResponse = addressMapper.toAddressResponse(addressOpt.get());
            
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Dirección actualizada exitosamente",
                addressResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AddressResponse> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ADDRESS_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Eliminar dirección")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        boolean deleted = addressService.deleteAddress(id);
        
        if (deleted) {
            ApiResponse<Void> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Dirección eliminada exitosamente"
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ADDRESS_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}