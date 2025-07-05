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

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(new ApiResponse<>("Products fetched successfully", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.findProductById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
            return ResponseEntity.ok(new ApiResponse<>("Product fetched successfully", product));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(PRODUCT_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(INTERNAL_SERVER_ERROR, null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.createProduct(product);
            return ResponseEntity
                    .status(201)
                    .body(new ApiResponse<>("Product created successfully", newProduct));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>(INTERNAL_SERVER_ERROR, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable("id") Long productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", updatedProduct));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(PRODUCT_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(INTERNAL_SERVER_ERROR, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable("id") Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(PRODUCT_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Delete failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/brand-and-name")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name) {
        try {
            List<Product> products = productService.findProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse<>(
                    String.format("Found %d product(s) matching brand '%s' and name '%s'", products.size(), brand,
                            name),
                    products));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/category-and-brand")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategoryAndBrand(
            @RequestParam String category,
            @RequestParam String brand) {
        try {
            List<Product> products = productService.findProductsByCategoryAndBrand(category, brand);
            return ResponseEntity.ok(new ApiResponse<>(
                    String.format("Found %d product(s) in category '%s' with brand '%s'", products.size(), category,
                            brand),
                    products));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/name")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByName(@RequestParam String name) {
        try {
            List<Product> products = productService.findProductsByNameContaining(name);
            return ResponseEntity.ok(new ApiResponse<>(
                    String.format("Found %d product(s) containing '%s'", products.size(), name),
                    products));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>("Failed to fetch products by name: " + e.getMessage(), null));
        }
    }

    @GetMapping("/brand")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByBrand(@RequestParam String brand) {
        try {
            List<Product> products = productService.findProductsByBrand(brand);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            String.format("Found %d product(s) for brand '%s'", products.size(), brand),
                            products));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new ApiResponse<>("Failed to fetch products by brand: " + e.getMessage(), null));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.findProductsByCategoryName(category);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            String.format("Found %d product(s) in category '%s'", products.size(), category),
                            products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>("Failed to fetch products by category: " + e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name) {
        try {
            Long count = productService.countProductsByBrandAndName(brand, name);
            String message = String.format("Found %d product(s) for brand '%s' and name '%s'", count, brand, name);
            return ResponseEntity.ok(new ApiResponse<>(message, count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("Internal Server Error: " + e.getMessage(), null));
        }
    }

}
