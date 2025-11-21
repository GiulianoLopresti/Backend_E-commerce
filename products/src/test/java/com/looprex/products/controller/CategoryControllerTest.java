package com.looprex.products.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.products.model.Category;
import com.looprex.products.service.CategoryService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Tarjetas de Graficas");
    }

    @Test
    void getAllCategories_DeberiaRetornar200ConListaDeCategorias() throws Exception {
        // Arrange
        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setName("Ram");
        
        List<Category> categories = Arrays.asList(testCategory, category2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Tarjetas de Graficas"))
                .andExpect(jsonPath("$.data[1].name").value("Ram"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void getAllCategories_DeberiaRetornar204CuandoNoHayCategorias() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(204));
    }

    @Test
    void getCategoryById_DeberiaRetornar200CuandoCategoriaExiste() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        // Act & Assert
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.name").value("Tarjetas de Graficas"));
    }

    @Test
    void getCategoryById_DeberiaRetornar404CuandoCategoriaNoExiste() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createCategory_DeberiaRetornar201CuandoCreacionEsExitosa() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    void createCategory_DeberiaRetornar400CuandoHayError() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new IllegalArgumentException("Ya existe una categoría con ese nombre"));

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateCategory_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        // Arrange
        when(categoryService.updateCategory(any(Long.class), any(Category.class)))
                .thenReturn(Optional.of(testCategory));

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateCategory_DeberiaRetornar404CuandoCategoriaNoExiste() throws Exception {
        // Arrange
        when(categoryService.updateCategory(any(Long.class), any(Category.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteCategory_DeberiaRetornar200CuandoEliminacionEsExitosa() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Categoría eliminada exitosamente"));
    }

    @Test
    void deleteCategory_DeberiaRetornar404CuandoCategoriaNoExiste() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Categoría no encontrada"))
                .when(categoryService).deleteCategory(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void deleteCategory_DeberiaRetornar400CuandoTieneProductosAsociados() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("No se puede eliminar la categoría porque tiene 3 producto(s) asociado(s). Elimina los productos primero."))
                .when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}