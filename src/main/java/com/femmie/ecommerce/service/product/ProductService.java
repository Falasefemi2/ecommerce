package com.femmie.ecommerce.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Category;
import com.femmie.ecommerce.model.Product;
import com.femmie.ecommerce.repository.CategoryRepository;
import com.femmie.ecommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(Product product) {
        Category category = getOrCreateCategory(product.getCategory().getName());
        product.setCategory(category);
        return productRepository.save(product);
    }

    private Category getOrCreateCategory(String categoryName) {
        return Optional.ofNullable(categoryRepository.findByName(categoryName))
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setInventory(updatedProduct.getInventory());
        existingProduct.setDescription(updatedProduct.getDescription());

        if (updatedProduct.getCategory() != null) {
            Category category = Optional.ofNullable(
                    categoryRepository.findByName(updatedProduct.getCategory().getName()))
                    .orElseGet(() -> categoryRepository.save(new Category(updatedProduct.getCategory().getName())));
            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findProductsByCategoryName(String categoryName) {
        return productRepository.findByCategory_Name(categoryName);
    }

    @Override
    public List<Product> findProductsByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand);
    }

    @Override
    public List<Product> findProductsByCategoryAndBrand(String categoryName, String brand) {
        return productRepository.findByCategory_NameAndBrandIgnoreCase(categoryName, brand);
    }

    @Override
    public List<Product> findProductsByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandIgnoreCaseAndNameContainingIgnoreCase(brand, name);
    }

    @Override
    public long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
    }

}
