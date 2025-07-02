package com.femmie.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.femmie.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Name(String categoryName);

    List<Product> findByBrandIgnoreCase(String brand);

    List<Product> findByCategory_NameAndBrandIgnoreCase(String categoryName, String brand);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByBrandIgnoreCaseAndNameContainingIgnoreCase(String brand, String name);

    long countByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);
}
