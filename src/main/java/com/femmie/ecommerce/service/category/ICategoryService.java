package com.femmie.ecommerce.service.category;

import java.util.List;

import com.femmie.ecommerce.model.Category;

public interface ICategoryService {
    Category getCategoryById(Long id);

    Category getCategoryByName(String name);

    List<Category> getAllCategories();

    Category updateCategory(Category category, Long id);

    void deleteCategory(Long id);

    Category addCategory(Category category);
}
