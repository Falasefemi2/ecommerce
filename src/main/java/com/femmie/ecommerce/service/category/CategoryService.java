package com.femmie.ecommerce.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.femmie.ecommerce.exception.AlreadyExistException;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Category;
import com.femmie.ecommerce.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public Category getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with name: " + name);
        }
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Category updatedCategory, Long id) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(updatedCategory.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category existingCategory = getCategoryById(id);
        categoryRepository.delete(existingCategory);
    }

    @Override
    public Category addCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistException("Category with name '" + category.getName() + "' already exists.");
        }
        return categoryRepository.save(category);
    }

}
