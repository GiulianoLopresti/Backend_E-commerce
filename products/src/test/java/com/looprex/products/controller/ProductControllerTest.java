package com.looprex.products.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.model.Status;
import com.looprex.products.service.ProductService;
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

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private Status testStatus;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Tarjetas de Graficas");

        testStatus = new Status();
        testStatus.setStatusId(1L);
        testStatus.setName("Activo");

        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setStock(15);
        testProduct.setProductPhoto(null);
        testProduct.setName("ASUS ROG Strix RTX 4090");
        testProduct.setDescription("Tarjeta gráfica de alto rendimiento");
        testProduct.setPrice(1899990);
        testProduct.setStatusId(1L);
        testProduct.setCategoryId(1L);
        testProduct.setCategory(testCategory);
        testProduct.setStatus(testStatus);
    }

    @Test
    void getAllProducts_DeberiaRetornar200ConListaDeProductos() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getAllProducts_DeberiaRetornar204CuandoNoHayProductos() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getProductById_DeberiaRetornar200CuandoProductoExiste() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.name").value("ASUS ROG Strix RTX 4090"));
    }

    @Test
    void getProductById_DeberiaRetornar404CuandoProductoNoExiste() throws Exception {
        // Arrange
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getProductsByCategory_DeberiaRetornar200ConProductosDeLaCategoria() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory(1L)).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void searchProducts_DeberiaRetornar200ConResultadosDeBusqueda() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.searchProducts("RTX")).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products/search?query=RTX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getProductsByStatus_DeberiaRetornar200ConProductosDelEstado() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByStatus(1L)).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void createProduct_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createProduct_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("El nombre del producto no puede estar vacío"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateProduct_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(productService.updateProduct(any(Long.class), any(Product.class)))
                .thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateProduct_DeberiaRetornar404CuandoProductoNoExiste() throws Exception {
        // Arrange
        when(productService.updateProduct(any(Long.class), any(Product.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteProduct_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteProduct_DeberiaRetornar404CuandoProductoNoExiste() throws Exception {
        // Arrange
        when(productService.deleteProduct(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}