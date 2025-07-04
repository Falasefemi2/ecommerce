package com.femmie.ecommerce.controller;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Category;
import com.femmie.ecommerce.service.category.CategoryService;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddCategory() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.addCategory(any(Category.class))).thenReturn(category);

        String jsonRequest = objectMapper.writeValueAsString(category);

        mockMvc.perform(post("/api/v1/categories/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.message").value("Category added successfully"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        Long categoryId = 1L;

        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testDeleteCategory_NotFound() throws Exception {
        Long categoryId = 100L;

        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCategories() throws Exception {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Electronics");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Books");

        List<Category> categories = List.of(cat1, cat2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Electronics"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Books"))
                .andExpect(jsonPath("$.message").value("Found 2 categories"));
    }

    @Test
    void testGetAllCategories_Singular() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Electronics"))
                .andExpect(jsonPath("$.message").value("Found 1 category"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Electronics");

        when(categoryService.getCategoryById(id)).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.message").value("Category found with id: 1"));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        Long id = 100L;

        when(categoryService.getCategoryById(id))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/categories/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Category not found with id: 100"));
    }

    @Test
    void testGetCategoryById_InternalServerError() throws Exception {
        Long id = 1L;

        when(categoryService.getCategoryById(id))
                .thenThrow(new RuntimeException("Unexpected DB error"));

        mockMvc.perform(get("/api/v1/categories/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Internal Server Error: Unexpected DB error"));
    }

    @Test
    void testGetCategoryByName() throws Exception {
        String name = "Electronics";
        Category category = new Category();
        category.setId(1L);
        category.setName(name);

        when(categoryService.getCategoryByName(name)).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/by-name")
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.message").value("Category found"));
    }

    @Test
    void testGetCategoryByName_NotFound() throws Exception {
        String name = "NonExistentCategory";

        when(categoryService.getCategoryByName(name))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/categories/by-name")
                .param("name", name))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Category not found with name: NonExistentCategory"));
    }

    @Test
    void testGetCategoryByName_InternalServerError() throws Exception {
        String name = "Electronics";

        when(categoryService.getCategoryByName(name))
                .thenThrow(new RuntimeException("DB connection failed"));

        mockMvc.perform(get("/api/v1/categories/by-name")
                .param("name", name))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Updated Electronics");

        when(categoryService.updateCategory(any(Category.class), eq(id))).thenReturn(category);

        String jsonRequest = objectMapper.writeValueAsString(category);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated Electronics"))
                .andExpect(jsonPath("$.message").value("Category updated successfully"));
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        Long id = 99L;
        Category category = new Category();
        category.setName("Non-existent");

        when(categoryService.updateCategory(any(Category.class), eq(id)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        String jsonRequest = objectMapper.writeValueAsString(category);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Category not found with id: 99"));
    }

    @Test
    void testUpdateCategory_InternalServerError() throws Exception {
        Long id = 1L;
        Category category = new Category();
        category.setName("Something");

        when(categoryService.updateCategory(any(Category.class), eq(id)))
                .thenThrow(new RuntimeException("Unexpected DB error"));

        String jsonRequest = objectMapper.writeValueAsString(category);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Internal Server Error: Unexpected DB error"));
    }
}
