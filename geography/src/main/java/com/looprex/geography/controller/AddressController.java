package com.looprex.geography.controller;

import com.looprex.geography.dto.AddressResponse;
import com.looprex.geography.dto.ApiResponse;
import com.looprex.geography.mapper.AddressMapper;
import com.looprex.geography.model.Address;
import com.looprex.geography.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
@Tag(
    name = "Direcciones", 
    description = "Endpoints para la gestión de direcciones de envío de los usuarios. " +
                  "Permite crear, listar, actualizar y eliminar direcciones asociadas a un usuario. " +
                  "Cada dirección debe estar vinculada a una Comuna válida y un Usuario existente."
)
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;  // Agregar mapper

    private static final String ADDRESS_NOT_FOUND = "Dirección no encontrada";

    public AddressController(AddressService addressService, AddressMapper addressMapper) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;  // Inyectar mapper
    }

    @Operation(
        summary = "Obtener todas las direcciones",
        description = "Retorna una lista completa de todas las direcciones registradas en el sistema. " +
                      "Incluye la información detallada de la comuna y región asociadas. " +
                      "Si no hay direcciones registradas, retorna un status 204 (No Content)."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de direcciones obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Direcciones obtenidas exitosamente",
                      "data": [
                        {
                          "addressId": 1,
                          "street": "Av. Providencia",
                          "number": "1234",
                          "userId": 1,
                          "comuna": {
                            "comunaId": 1,
                            "name": "Providencia",
                            "region": {
                              "regionId": 1,
                              "name": "Región Metropolitana"
                            }
                          }
                        }
                      ],
                      "count": 1
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "No hay direcciones en el sistema",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
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


    @Operation(
        summary = "Obtener dirección por ID",
        description = "Busca y retorna una dirección específica por su identificador único."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Dirección encontrada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 200,
                      "message": "Dirección encontrada",
                      "data": {
                        "addressId": 1,
                        "street": "Av. Providencia",
                        "number": "1234",
                        "userId": 1,
                        "comuna": {
                          "comunaId": 1,
                          "name": "Providencia",
                          "region": {
                            "regionId": 1,
                            "name": "Región Metropolitana"
                          }
                        }
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Dirección no encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "Dirección no encontrada"
                    }
                    """
                )
            )
        )
    })
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


    @Operation(
        summary = "Obtener direcciones por usuario",
        description = "Retorna todas las direcciones asociadas a un usuario específico. " +
                      "Valida que el usuario exista antes de realizar la búsqueda."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Direcciones del usuario obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "statusCode": 404,
                      "message": "El usuario con ID 99 no existe"
                    }
                    """
                )
            )
        )
    })
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


    @Operation(
        summary = "Crear nueva dirección",
        description = "Registra una nueva dirección para un usuario. Requiere calle, número, " +
                      "ID de usuario y una comuna válida. Verifica la existencia del usuario y la comuna."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Dirección creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "statusCode": 201,
                      "message": "Dirección creada exitosamente",
                      "data": {
                        "addressId": 2,
                        "street": "Calle Nueva",
                        "number": "567",
                        "userId": 1,
                        "comuna": { "comunaId": 1, "name": "Providencia" }
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Faltan campos, usuario/comuna no existen",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos de la nueva dirección",
                required = true,
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class))
            )@RequestBody Address address) {
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

    @Operation(
        summary = "Actualizar dirección",
        description = "Actualiza los datos de una dirección existente (calle, número, comuna). " +
                      "Solo se actualizan los campos proporcionados."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Dirección actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Dirección no encontrada"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos en la actualización"
        )
    })
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

    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección del sistema por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dirección eliminada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dirección no encontrada")
    })
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