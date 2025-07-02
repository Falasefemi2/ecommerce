package com.femmie.ecommerce.controller;

import com.femmie.ecommerce.model.Product;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.product.IProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.femmie.ecommerce.exception.ResourceNotFoundException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(new ApiResponse("Products fetched successfully", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.findProductById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
            return ResponseEntity.ok(new ApiResponse("Product fetched successfully", product));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("Product not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Internal Server Error", null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.createProduct(product);
            return ResponseEntity
                    .status(201)
                    .body(new ApiResponse("Product created successfully", newProduct));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse("Internal Server Error", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") Long productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseEntity.ok(new ApiResponse("Product updated successfully", updatedProduct));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("Product not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Internal Server Error", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("id") Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Product deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Product not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Delete failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name) {
        try {
            List<Product> products = productService.findProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse(
                    String.format("Found %d product(s) matching brand '%s' and name '%s'", products.size(), brand,
                            name),
                    products));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Failed to retrieve products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(
            @RequestParam String category,
            @RequestParam String brand) {
        try {
            List<Product> products = productService.findProductsByCategoryAndBrand(category, brand);
            return ResponseEntity.ok(new ApiResponse(
                    String.format("Found %d product(s) in category '%s' with brand '%s'", products.size(), category,
                            brand),
                    products));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Failed to retrieve products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/name")
    public ResponseEntity<ApiResponse> getProductsByName(@RequestParam String name) {
        try {
            List<Product> products = productService.findProductsByNameContaining(name);
            return ResponseEntity.ok(new ApiResponse(
                    String.format("Found %d product(s) containing '%s'", products.size(), name),
                    products));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse("Failed to fetch products by name: " + e.getMessage(), null));
        }
    }

    @GetMapping("/brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam String brand) {
        try {
            List<Product> products = productService.findProductsByBrand(brand);
            return ResponseEntity.ok(
                    new ApiResponse(
                            String.format("Found %d product(s) for brand '%s'", products.size(), brand),
                            products));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse("Failed to fetch products by brand: " + e.getMessage(), null));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.findProductsByCategoryName(category);
            return ResponseEntity.ok(
                    new ApiResponse(
                            String.format("Found %d product(s) in category '%s'", products.size(), category),
                            products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse("Failed to fetch products by category: " + e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name) {
        try {
            Long count = productService.countProductsByBrandAndName(brand, name);
            String message = String.format("Found %d product(s) for brand '%s' and name '%s'", count, brand, name);
            return ResponseEntity.ok(new ApiResponse(message, count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Internal Server Error: " + e.getMessage(), null));
        }
    }

}
