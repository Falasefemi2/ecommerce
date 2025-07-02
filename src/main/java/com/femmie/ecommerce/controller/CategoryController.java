package com.femmie.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.femmie.ecommerce.exception.AlreadyExistException;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Category;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            String message = String.format("Found %d categor%s",
                    categories.size(),
                    categories.size() == 1 ? "y" : "ies");
            return ResponseEntity.ok(new ApiResponse(message, categories));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error retrieving categories: " + e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.addCategory(category);
            return ResponseEntity.ok(new ApiResponse("Category added successfully", savedCategory));
        } catch (AlreadyExistException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Internal Server Error", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            String message = "Category found with id: " + id;
            return ResponseEntity.ok(new ApiResponse(message, category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Category not found with id: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Internal Server Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam String name) {
        try {
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse("Category found", category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("Category not found with name: " + name, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse("Internal Server Error", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse("Category deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Category not found with id: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Internal Server Error: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            return ResponseEntity.ok(new ApiResponse("Category updated successfully", updatedCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Category not found with id: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Internal Server Error: " + e.getMessage(), null));
        }
    }
}
