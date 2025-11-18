package com.looprex.products.service;

import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.model.Status;
import com.looprex.products.repository.CategoryRepository;
import com.looprex.products.repository.ProductRepository;
import com.looprex.products.repository.StatusRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
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
    void getAllProducts_DeberiaRetornarListaDeProductos() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ASUS ROG Strix RTX 4090", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_DeberiaRetornarProductoCuandoExiste() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ASUS ROG Strix RTX 4090", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductsByCategory_DeberiaRetornarProductosDeLaCategoria() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryId(1L)).thenReturn(products);

        // Act
        List<Product> result = productService.getProductsByCategory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    void searchProducts_DeberiaRetornarProductosQueCoincidan() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByName("RTX")).thenReturn(products);

        // Act
        List<Product> result = productService.searchProducts("RTX");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchByName("RTX");
    }

    @Test
    void createProduct_DeberiaCrearProductoExitosamente() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(statusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.createProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals("ASUS ROG Strix RTX 4090", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(statusRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_DeberiaLanzarExcepcionCuandoNombreEstaVacio() {
        // Arrange
        testProduct.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProduct)
        );
        
        assertEquals("El nombre del producto no puede estar vacío", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_DeberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProduct)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_DeberiaLanzarExcepcionCuandoEstadoNoExiste() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(statusRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProduct)
        );
        
        assertTrue(exception.getMessage().contains("no existe"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_DeberiaLanzarExcepcionCuandoPrecioEsNegativo() {
        // Arrange
        testProduct.setPrice(-100);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProduct)
        );
        
        assertTrue(exception.getMessage().contains("precio"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_DeberiaActualizarProductoExitosamente() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setStock(10);
        updatedProduct.setPrice(1799990);
        
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(testProduct))
            .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Optional<Product> result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertTrue(result.isPresent());
        verify(productRepository, times(2)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_DeberiaEliminarProductoExitosamente() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = productService.deleteProduct(1L);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_DeberiaRetornarFalsoCuandoProductoNoExiste() {
        // Arrange
        when(productRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = productService.deleteProduct(999L);

        // Assert
        assertFalse(result);
        verify(productRepository, never()).deleteById(any());
    }
}