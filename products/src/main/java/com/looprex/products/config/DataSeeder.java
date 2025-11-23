package com.looprex.products.config;

import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.model.Status;
import com.looprex.products.repository.CategoryRepository;
import com.looprex.products.repository.ProductRepository;
import com.looprex.products.repository.StatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StatusRepository statusRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(StatusRepository statusRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.statusRepository = statusRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Cargar Estados
        if (statusRepository.count() == 0) {
            statusRepository.save(new Status(null, "Activo"));
            statusRepository.save(new Status(null, "Inactivo"));
            statusRepository.save(new Status(null, "Pendiente"));
            statusRepository.save(new Status(null, "Completado"));
            statusRepository.save(new Status(null, "Cancelado"));
            statusRepository.save(new Status(null, "En envío"));
        }

        // Cargar Categorías y Productos
        if (categoryRepository.count() == 0) {
            Category graficas = categoryRepository.save(new Category(null, "Tarjetas de Graficas"));
            Category ram = categoryRepository.save(new Category(null, "Ram"));
            Category procesadores = categoryRepository.save(new Category(null, "Procesadores"));

            // Solo creamos productos si acabamos de crear las categorías (para evitar duplicados o errores)
            Long activoId = statusRepository.findByName("Activo")
                    .orElseThrow(() -> new RuntimeException("Status 'Activo' not found"))
                    .getStatusId();

            // Producto 1
            Product p1 = new Product();
            p1.setStock(15);
            p1.setName("ASUS ROG Strix RTX 4090");
            p1.setDescription("Tarjeta gráfica de alto rendimiento con 24GB GDDR6X");
            p1.setPrice(1899990);
            p1.setStatusId(activoId);
            p1.setCategoryId(graficas.getCategoryId());
            productRepository.save(p1);

            // Producto 2
            Product p2 = new Product();
            p2.setStock(30);
            p2.setName("Ram DDR5 Corsair Vengeance");
            p2.setDescription("Corsair Vengeance DDR5 32GB (2x16GB) 5600MHz");
            p2.setPrice(129990);
            p2.setStatusId(activoId);
            p2.setCategoryId(ram.getCategoryId());
            productRepository.save(p2);

            // Producto 3
            Product p3 = new Product();
            p3.setStock(20);
            p3.setName("AMD Ryzen 9 7950X");
            p3.setDescription("Procesador de 16 núcleos y 32 hilos a 5.7GHz");
            p3.setPrice(599990);
            p3.setStatusId(activoId);
            p3.setCategoryId(procesadores.getCategoryId());
            productRepository.save(p3);
            
        }
    }
}