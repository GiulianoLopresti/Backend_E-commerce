package com.looprex.products.service;

import com.looprex.products.model.Category;
import com.looprex.products.repository.CategoryRepository;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Tarjetas de Graficas");
    }

    @Test
    void getAllCategories_DeberiaRetornarListaDeCategorias() {
        // Arrange
        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setName("Ram");
        
        List<Category> categories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tarjetas de Graficas", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getAllCategories_DeberiaRetornarListaVaciaCuandoNoHayCategorias() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_DeberiaRetornarCategoriaCuandoExiste() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Tarjetas de Graficas", result.get().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_DeberiaRetornarVacioCuandoNoExiste() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getCategoryById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void createCategory_DeberiaCrearCategoriaExitosamente() {
        // Arrange
        when(categoryRepository.findByName("Tarjetas de Graficas")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.createCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals("Tarjetas de Graficas", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_DeberiaLanzarExcepcionCuandoNombreYaExiste() {
        // Arrange
        when(categoryRepository.findByName("Tarjetas de Graficas")).thenReturn(Optional.of(testCategory));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        
        assertEquals("Ya existe una categoría con ese nombre", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void createCategory_DeberiaLanzarExcepcionCuandoNombreEstaVacio() {
        // Arrange
        testCategory.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.createCategory(testCategory)
        );
        
        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_DeberiaActualizarCategoriaExitosamente() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setName("Tarjetas Gráficas Premium");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByName("Tarjetas Gráficas Premium")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Optional<Category> result = categoryService.updateCategory(1L, updatedCategory);

        // Assert
        assertTrue(result.isPresent());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_DeberiaRetornarVacioCuandoCategoriaNoExiste() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.updateCategory(999L, testCategory);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_DeberiaEliminarCategoriaExitosamente() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = categoryService.deleteCategory(1L);

        // Assert
        assertTrue(result);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_DeberiaRetornarFalsoCuandoCategoriaNoExiste() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = categoryService.deleteCategory(999L);

        // Assert
        assertFalse(result);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void existsByName_DeberiaRetornarTrueCuandoExiste() {
        // Arrange
        when(categoryRepository.findByName("Tarjetas de Graficas")).thenReturn(Optional.of(testCategory));

        // Act
        boolean result = categoryService.existsByName("Tarjetas de Graficas");

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByName_DeberiaRetornarFalseCuandoNoExiste() {
        // Arrange
        when(categoryRepository.findByName("Categoria Inexistente")).thenReturn(Optional.empty());

        // Act
        boolean result = categoryService.existsByName("Categoria Inexistente");

        // Assert
        assertFalse(result);
    }
}