package com.looprex.shopping.controller;

import com.looprex.shopping.model.Buy;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.repository.BuyRepository;
import com.looprex.shopping.repository.DetailRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/init")
@Tag(name = "Inicialización", description = "Endpoints para cargar datos iniciales del sistema")
public class InitController {

    private final BuyRepository buyRepository;
    private final DetailRepository detailRepository;

    public InitController(BuyRepository buyRepository,
                         DetailRepository detailRepository) {
        this.buyRepository = buyRepository;
        this.detailRepository = detailRepository;
    }

    @PostMapping("/seed")
    @Operation(
        summary = "Cargar datos iniciales",
        description = "Inserta compras y detalles de ejemplo en la base de datos. " +
                      "Solo ejecutar una vez al inicializar el sistema. " +
                      "IMPORTANTE: Antes de ejecutar esto, asegúrate de que los otros microservicios (Users, Geography, Products) " +
                      "estén corriendo y tengan datos iniciales cargados"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos iniciales cargados exitosamente",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    public ResponseEntity<String> seedData() {
        StringBuilder mensaje = new StringBuilder();

        // ========== BUYS ==========
        if (buyRepository.count() == 0) {
            // Compra 1
            Buy buy1 = new Buy();
            buy1.setOrderNumber("ORD-2025-001");
            buy1.setBuyDate(System.currentTimeMillis() - 86400000L); // Hace 1 día
            buy1.setSubtotal(1899990);
            buy1.setIva(361098);
            buy1.setShipping(5990);
            buy1.setTotal(2267078);
            buy1.setPaymentMethod("Tarjeta de Débito");
            buy1.setStatusId(4L); // En envío
            buy1.setAddressId(1L);
            buy1.setUserId(1L);
            buyRepository.save(buy1);

            // Compra 2
            Buy buy2 = new Buy();
            buy2.setOrderNumber("ORD-2025-002");
            buy2.setBuyDate(System.currentTimeMillis() - 172800000L); // Hace 2 días
            buy2.setSubtotal(729980);
            buy2.setIva(138696);
            buy2.setShipping(5990);
            buy2.setTotal(874666);
            buy2.setPaymentMethod("Tarjeta de Crédito");
            buy2.setStatusId(2L); // Completado
            buy2.setAddressId(2L);
            buy2.setUserId(2L);
            buyRepository.save(buy2);

            // Compra 3
            Buy buy3 = new Buy();
            buy3.setOrderNumber("ORD-2025-003");
            buy3.setBuyDate(System.currentTimeMillis());
            buy3.setSubtotal(1299990);
            buy3.setIva(246998);
            buy3.setShipping(5990);
            buy3.setTotal(1552978);
            buy3.setPaymentMethod("Transferencia");
            buy3.setStatusId(1L); // Pendiente
            buy3.setAddressId(1L);
            buy3.setUserId(1L);
            buyRepository.save(buy3);

            mensaje.append("Compras creadas. ");

            // ========== DETAILS ==========
            // Detalles de la Compra 1
            Detail detail1 = new Detail();
            detail1.setBuyId(1L);
            detail1.setProductId(1L); // ASUS ROG Strix RTX 4090
            detail1.setQuantity(1);
            detail1.setUnitPrice(1899990);
            detail1.setSubtotal(1899990);
            detailRepository.save(detail1);

            // Detalles de la Compra 2
            Detail detail2 = new Detail();
            detail2.setBuyId(2L);
            detail2.setProductId(2L); // Ram DDR5 Corsair Vengeance
            detail2.setQuantity(2);
            detail2.setUnitPrice(129990);
            detail2.setSubtotal(259980);
            detailRepository.save(detail2);

            Detail detail3 = new Detail();
            detail3.setBuyId(2L);
            detail3.setProductId(6L); // Intel Core i9-14900K
            detail3.setQuantity(1);
            detail3.setUnitPrice(699990);
            detail3.setSubtotal(699990);
            detailRepository.save(detail3);

            // Detalles de la Compra 3
            Detail detail4 = new Detail();
            detail4.setBuyId(3L);
            detail4.setProductId(4L); // NVIDIA GeForce RTX 4080
            detail4.setQuantity(1);
            detail4.setUnitPrice(1299990);
            detail4.setSubtotal(1299990);
            detailRepository.save(detail4);

            mensaje.append("Detalles creados.");
        } else {
            mensaje.append("Los datos ya existen.");
        }

        return ResponseEntity.ok(mensaje.toString());
    }
}