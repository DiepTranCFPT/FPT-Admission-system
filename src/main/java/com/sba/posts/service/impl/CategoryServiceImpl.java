package com.sba.posts.service.impl;

import com.sba.posts.pojos.Category;
import com.sba.posts.repository.CategoryRepository;
import com.sba.posts.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        category.setDeleted(false);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .filter(cat -> !cat.isDeleted())
                .orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> !cat.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Category updateCategory(String id, Category category) {
        Category existing = categoryRepository.findById(id)
                .filter(cat -> !cat.isDeleted())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(category.getName());
        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.findById(id).ifPresent(cat -> {
            cat.setDeleted(true);
            categoryRepository.save(cat);
        });
    }
}

