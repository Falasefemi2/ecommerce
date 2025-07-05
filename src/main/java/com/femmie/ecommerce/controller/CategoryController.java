package com.femmie.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.femmie.ecommerce.response.ResponseMessage;
import com.femmie.ecommerce.service.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        String message = String.format(
                ResponseMessage.CATEGORIES_FOUND,
                categories.size(),
                categories.size() == 1 ? "y" : "ies"
        );
        return ResponseEntity.ok(new ApiResponse<>(message, categories));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Category>> addCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.addCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(ResponseMessage.CATEGORY_ADDED, savedCategory));
        } catch (AlreadyExistException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(ResponseMessage.INTERNAL_ERROR + ": " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            String message = String.format(ResponseMessage.CATEGORY_FOUND_BY_ID, id);
            return ResponseEntity.ok(new ApiResponse<>(message, category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(String.format(ResponseMessage.CATEGORY_NOT_FOUND, id), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(ResponseMessage.INTERNAL_ERROR + ": " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<Category>> getCategoryByName(@RequestParam String name) {
        try {
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse<>(ResponseMessage.CATEGORY_FOUND_BY_NAME, category));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(String.format(ResponseMessage.CATEGORY_NAME_NOT_FOUND, name), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(ResponseMessage.INTERNAL_ERROR, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(ResponseMessage.CATEGORY_DELETED, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(String.format(ResponseMessage.CATEGORY_NOT_FOUND, id), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(ResponseMessage.INTERNAL_ERROR + ": " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            return ResponseEntity.ok(new ApiResponse<>(ResponseMessage.CATEGORY_UPDATED, updatedCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(String.format(ResponseMessage.CATEGORY_NOT_FOUND, id), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(ResponseMessage.INTERNAL_ERROR + ": " + e.getMessage(), null));
        }
    }
}
