package com.sba.posts.service;

import com.sba.posts.pojos.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(String id);
    List<Category> getAllCategories();
    Category updateCategory(String id, Category category);
    void deleteCategory(String id); // soft delete
}

