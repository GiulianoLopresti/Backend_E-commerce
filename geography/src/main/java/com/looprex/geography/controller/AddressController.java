package com.looprex.geography.controller;

import com.looprex.geography.dto.ApiResponse;
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

    private static final String ADDRESS_NOT_FOUND = "Dirección no encontrada";

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Obtener todas las direcciones")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        
        if (addresses.isEmpty()) {
            ApiResponse<List<Address>> response = new ApiResponse<>(
                false,
                HttpStatus.NO_CONTENT.value(),
                "No se encontraron direcciones en el sistema",
                null,
                0L
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        
        ApiResponse<List<Address>> response = new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Direcciones obtenidas exitosamente",
            addresses,
            (long) addresses.size()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener dirección por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(@PathVariable Long id) {
        Optional<Address> addressOpt = addressService.getAddressById(id);
        
        if (addressOpt.isPresent()) {
            ApiResponse<Address> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Dirección encontrada",
                addressOpt.get()
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Address> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                ADDRESS_NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Obtener direcciones por usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Address>>> getAddressesByUserId(@PathVariable Long userId) {
        try {
            List<Address> addresses = addressService.getAddressesByUserId(userId);
            
            ApiResponse<List<Address>> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Direcciones obtenidas exitosamente",
                addresses,
                (long) addresses.size()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<Address>> response = new ApiResponse<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Crear nueva dirección")
    @PostMapping
    public ResponseEntity<ApiResponse<Address>> createAddress(@RequestBody Address address) {
        try {
            Address created = addressService.createAddress(address);
            ApiResponse<Address> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Dirección creada exitosamente",
                created
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<Address> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Actualizar dirección")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        try {
            Optional<Address> updatedOpt = addressService.updateAddress(id, address);
            
            if (updatedOpt.isPresent()) {
                ApiResponse<Address> response = new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Dirección actualizada exitosamente",
                    updatedOpt.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Address> response = new ApiResponse<>(
                    false,
                    HttpStatus.NOT_FOUND.value(),
                    ADDRESS_NOT_FOUND
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<Address> response = new ApiResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
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