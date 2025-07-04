package com.femmie.ecommerce.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femmie.ecommerce.model.Category;
import com.femmie.ecommerce.model.Product;
import com.femmie.ecommerce.service.product.ProductService;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddProduct() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product inputProduct = new Product();
        inputProduct.setName("iPhone 15");
        inputProduct.setBrand("Apple");
        inputProduct.setPrice(new BigDecimal("999.99"));
        inputProduct.setInventory(50);
        inputProduct.setDescription("Latest iPhone model");
        inputProduct.setCategory(category);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("iPhone 15");
        savedProduct.setBrand("Apple");
        savedProduct.setPrice(new BigDecimal("999.99"));
        savedProduct.setInventory(50);
        savedProduct.setDescription("Latest iPhone model");
        savedProduct.setCategory(category);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        String jsonRequest = objectMapper.writeValueAsString(inputProduct);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("iPhone 15"))
                .andExpect(jsonPath("$.data.brand").value("Apple"))
                .andExpect(jsonPath("$.data.price").value(999.99))
                .andExpect(jsonPath("$.data.inventory").value(50))
                .andExpect(jsonPath("$.data.description").value("Latest iPhone model"))
                .andExpect(jsonPath("$.data.category.id").value(1))
                .andExpect(jsonPath("$.data.category.name").value("Electronics"));

    }

    @Test
    void testCountProductsByBrandAndName() throws Exception {
        String brand = "Apple";
        String name = "iPhone";
        Long count = 3L;

        when(productService.countProductsByBrandAndName(brand, name)).thenReturn(count);

        mockMvc.perform(get("/api/v1/products/count")
                .param("brand", brand)
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(3))
                .andExpect(jsonPath("$.message").value("Found 3 product(s) for brand 'Apple' and name 'iPhone'"));

    }

    @Test
    void testDeleteProduct() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product productToDelete = new Product();
        productToDelete.setName("iPhone 15 Pro");
        productToDelete.setBrand("Apple");
        productToDelete.setPrice(new BigDecimal("1199.99"));
        productToDelete.setInventory(30);
        productToDelete.setDescription("Latest iPhone Pro model");
        productToDelete.setCategory(category);

        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));

    }

    @Test
    void testGetAllProducts() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iPhone 15");
        product1.setBrand("Apple");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setInventory(50);
        product1.setDescription("Latest iPhone model");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Galaxy S23");
        product2.setBrand("Samsung");
        product2.setPrice(new BigDecimal("799.99"));
        product2.setInventory(100);
        product2.setDescription("Latest Samsung model");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.findAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Galaxy S23"));
    }

    @Test
    void testGetProductById() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setBrand("Apple");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(50);
        product.setDescription("Latest iPhone model");
        product.setCategory(category);

        when(productService.findProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("iPhone 15"));
    }

    @Test
    void testGetProductsByBrand() throws Exception {
        String brand = "Apple";

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iPhone 15");
        product1.setBrand(brand);
        product1.setPrice(new BigDecimal("999.99"));
        product1.setInventory(50);
        product1.setDescription("Latest iPhone model");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Air");
        product2.setBrand(brand);
        product2.setPrice(new BigDecimal("1299.99"));
        product2.setInventory(100);
        product2.setDescription("Latest MacBook Air model");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.findProductsByBrand(brand)).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/brand")
                .param("brand", brand))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.data[1].name").value("MacBook Air"));
    }

    @Test
    void testGetProductsByBrandAndName() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        String brand = "Apple";
        String name = "iPhone 15";

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setBrand("Apple");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(50);
        product.setDescription("Latest iPhone model");
        product.setCategory(category);

        when(productService.findProductsByBrandAndName(brand, name))
                .thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/api/v1/products/brand-and-name")
                .param("brand", brand)
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value(name));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iPhone 15");
        product1.setBrand("Apple");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setInventory(50);
        product1.setDescription("Latest iPhone model");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Galaxy S23");
        product2.setBrand("Samsung");
        product2.setPrice(new BigDecimal("799.99"));
        product2.setInventory(100);
        product2.setDescription("Latest Samsung model");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.findProductsByCategoryName(category.getName())).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/category/{category}", category.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Galaxy S23"));

    }

    @Test
    void testGetProductsByCategoryAndBrand() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setBrand("Apple");
        product.setPrice(new BigDecimal("999.99"));
        product.setInventory(50);
        product.setDescription("Latest iPhone model");
        product.setCategory(category);

        List<Product> products = List.of(product);

        when(productService.findProductsByCategoryAndBrand("Electronics", "Apple")).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/category-and-brand")
                .param("category", "Electronics")
                .param("brand", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.data[0].brand").value("Apple"))
                .andExpect(jsonPath("$.message").value("Found 1 product(s) in category 'Electronics' with brand 'Apple'"));

    }

    @Test
    void testGetProductsByName() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 15 Pro");
        product.setBrand("Apple");
        product.setPrice(new BigDecimal("1199.99"));
        product.setInventory(30);
        product.setDescription("Latest iPhone Pro model");

        List<Product> products = List.of(product);

        when(productService.findProductsByNameContaining("iphone")).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/name")
                .param("name", "iphone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.message").value("Found 1 product(s) containing 'iphone'"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product productToUpdate = new Product();
        productToUpdate.setName("iPhone 15 Pro");
        productToUpdate.setBrand("Apple");
        productToUpdate.setPrice(new BigDecimal("1199.99"));
        productToUpdate.setInventory(30);
        productToUpdate.setDescription("Latest iPhone Pro model");
        productToUpdate.setCategory(category);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("iPhone 15 Pro");
        updatedProduct.setBrand("Apple");
        updatedProduct.setPrice(new BigDecimal("1199.99"));
        updatedProduct.setInventory(30);
        updatedProduct.setDescription("Latest iPhone Pro model");
        updatedProduct.setCategory(category);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);
        String jsonRequest = objectMapper.writeValueAsString(productToUpdate);

        mockMvc.perform(put("/api/v1/products/1", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.data.brand").value("Apple"))
                .andExpect(jsonPath("$.data.price").value(1199.99))
                .andExpect(jsonPath("$.data.inventory").value(30))
                .andExpect(jsonPath("$.data.description").value("Latest iPhone Pro model"))
                .andExpect(jsonPath("$.data.category.id").value(1))
                .andExpect(jsonPath("$.data.category.name").value("Electronics"));

    }
}
