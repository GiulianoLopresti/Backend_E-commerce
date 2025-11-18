package com.looprex.products.controller;

import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.model.Status;
import com.looprex.products.repository.CategoryRepository;
import com.looprex.products.repository.ProductRepository;
import com.looprex.products.repository.StatusRepository;
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

    private final StatusRepository statusRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public InitController(StatusRepository statusRepository,
                         CategoryRepository categoryRepository,
                         ProductRepository productRepository) {
        this.statusRepository = statusRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/seed")
    @Operation(
        summary = "Cargar datos iniciales",
        description = "Inserta estados, categorías y productos de ejemplo en la base de datos. " +
                      "Solo ejecutar una vez al inicializar el sistema"
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

        // ========== STATUSES ==========
        if (statusRepository.count() == 0) {
            Status activo = new Status();
            activo.setName("Activo");
            statusRepository.save(activo);

            Status inactivo = new Status();
            inactivo.setName("Inactivo");
            statusRepository.save(inactivo);

            Status pendiente = new Status();
            pendiente.setName("Pendiente");
            statusRepository.save(pendiente);

            Status completado = new Status();
            completado.setName("Completado");
            statusRepository.save(completado);

            Status cancelado = new Status();
            cancelado.setName("Cancelado");
            statusRepository.save(cancelado);

            Status enEnvio = new Status();
            enEnvio.setName("En envío");
            statusRepository.save(enEnvio);

            mensaje.append("Estados creados. ");
        }

        // ========== CATEGORIES ==========
        if (categoryRepository.count() == 0) {
            Category graficas = new Category();
            graficas.setName("Tarjetas de Graficas");
            categoryRepository.save(graficas);

            Category ram = new Category();
            ram.setName("Ram");
            categoryRepository.save(ram);

            Category procesadores = new Category();
            procesadores.setName("Procesadores");
            categoryRepository.save(procesadores);

            mensaje.append("Categorías creadas. ");

            // ========== PRODUCTS ==========
            // Obtener IDs de los estados y categorías creados
            Long activoId = statusRepository.findByName("Activo").map(Status::getStatusId).orElse(1L);
            Long graficasId = categoryRepository.findByName("Tarjetas de Graficas").map(Category::getCategoryId).orElse(1L);
            Long ramId = categoryRepository.findByName("Ram").map(Category::getCategoryId).orElse(2L);
            Long procesadoresId = categoryRepository.findByName("Procesadores").map(Category::getCategoryId).orElse(3L);

            // Producto 1: Tarjeta Gráfica
            Product product1 = new Product();
            product1.setStock(15);
            product1.setProductPhoto(null);
            product1.setName("ASUS ROG Strix RTX 4090");
            product1.setDescription("Tarjeta gráfica de alto rendimiento con 24GB GDDR6X");
            product1.setPrice(1899990);
            product1.setStatusId(activoId);
            product1.setCategoryId(graficasId);
            productRepository.save(product1);

            // Producto 2: RAM
            Product product2 = new Product();
            product2.setStock(30);
            product2.setProductPhoto(null);
            product2.setName("Ram DDR5 Corsair Vengeance");
            product2.setDescription("Corsair Vengeance DDR5 32GB (2x16GB) 5600MHz");
            product2.setPrice(129990);
            product2.setStatusId(activoId);
            product2.setCategoryId(ramId);
            productRepository.save(product2);

            // Producto 3: Procesador
            Product product3 = new Product();
            product3.setStock(20);
            product3.setProductPhoto(null);
            product3.setName("AMD Ryzen 9 7950X");
            product3.setDescription("Procesador de 16 núcleos y 32 hilos a 5.7GHz");
            product3.setPrice(599990);
            product3.setStatusId(activoId);
            product3.setCategoryId(procesadoresId);
            productRepository.save(product3);

            // Producto 4: Tarjeta Gráfica
            Product product4 = new Product();
            product4.setStock(25);
            product4.setProductPhoto(null);
            product4.setName("NVIDIA GeForce RTX 4080");
            product4.setDescription("Tarjeta gráfica gaming con 16GB GDDR6X");
            product4.setPrice(1299990);
            product4.setStatusId(activoId);
            product4.setCategoryId(graficasId);
            productRepository.save(product4);

            // Producto 5: RAM
            Product product5 = new Product();
            product5.setStock(40);
            product5.setProductPhoto(null);
            product5.setName("G.Skill Trident Z5 RGB");
            product5.setDescription("G.Skill Trident Z5 RGB DDR5 64GB (2x32GB) 6000MHz");
            product5.setPrice(249990);
            product5.setStatusId(activoId);
            product5.setCategoryId(ramId);
            productRepository.save(product5);

            // Producto 6: Procesador
            Product product6 = new Product();
            product6.setStock(18);
            product6.setProductPhoto(null);
            product6.setName("Intel Core i9-14900K");
            product6.setDescription("Procesador Intel de 14va generación, 24 núcleos");
            product6.setPrice(699990);
            product6.setStatusId(activoId);
            product6.setCategoryId(procesadoresId);
            productRepository.save(product6);

            mensaje.append("Productos creados.");
        } else {
            mensaje.append("Los datos ya existen.");
        }

        return ResponseEntity.ok(mensaje.toString());
    }
}