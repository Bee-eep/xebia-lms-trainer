/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.service;

import com.xebia.lms.trainer.authoring.dto.category.CategoryForm;
import com.xebia.lms.trainer.authoring.model.Category;
import com.xebia.lms.trainer.authoring.repository.CategoryRepository;
import com.xebia.lms.trainer.authoring.repository.CourseRepository;
import com.xebia.lms.trainer.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CategoryService - CRUD operations for Category.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Category create(CategoryForm form) {

        if (categoryRepository.existsByNameIgnoreCase(form.name())) {
            throw new IllegalArgumentException(
                    "Category already exists with name: " + form.name());
        }

        Category category = new Category();
        applyForm(category, form);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID categoryId, CategoryForm form) {

        Category category = findOrThrow(categoryId);

        categoryRepository.findByNameIgnoreCase(form.name())
                .ifPresent(existing -> {
                    if (!existing.getCategoryId().equals(categoryId)) {
                        throw new IllegalArgumentException(
                                "Category already exists with name: " + form.name());
                    }
                });

        applyForm(category, form);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category setActive(UUID categoryId, boolean active) {

        Category category = findOrThrow(categoryId);
        category.setActive(active);

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> list(Optional<Boolean> activeFilter) {

        return activeFilter
                .map(categoryRepository::findByActive)
                .orElseGet(categoryRepository::findAll);
    }

    @Transactional(readOnly = true)
    public Category getById(UUID categoryId) {
        return findOrThrow(categoryId);
    }

    @Transactional
    public void delete(UUID categoryId) {

        Category category = findOrThrow(categoryId);

        if (courseRepository.existsByCategory_CategoryId(categoryId)) {
            throw new IllegalStateException(
                    "Cannot delete category because it contains one or more courses.");
        }

        categoryRepository.delete(category);
    }

    private void applyForm(Category category, CategoryForm form) {
        category.setName(form.name());
        category.setIcon(form.icon());
        category.setColor(form.color());
        category.setDescription(form.description());
    }

    private Category findOrThrow(UUID categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + categoryId));
    }
}