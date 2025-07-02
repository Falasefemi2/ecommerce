package com.femmie.ecommerce.service.product;

import java.util.List;
import java.util.Optional;

import com.femmie.ecommerce.model.Product;

public interface IProductService {
    Product createProduct(Product product);

    Optional<Product> findProductById(Long id);

    Product updateProduct(Long productId, Product updatedProduct);

    void deleteProductById(Long id);

    List<Product> findAllProducts();

    List<Product> findProductsByCategoryName(String categoryName);

    List<Product> findProductsByBrand(String brand);

    List<Product> findProductsByCategoryAndBrand(String categoryName, String brand);

    List<Product> findProductsByNameContaining(String name);

    List<Product> findProductsByBrandAndName(String brand, String name);

    long countProductsByBrandAndName(String brand, String name);
}
