/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.category.CategoryForm;
import com.xebia.lms.trainer.authoring.dto.category.CategoryResponse;
import com.xebia.lms.trainer.authoring.model.Category;
import com.xebia.lms.trainer.authoring.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CategoryController - trainer routes for Category management.
 */
@RestController
@RequestMapping("/api/v1/trainer/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryForm form) {
        Category category = categoryService.create(form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryForm form) {

        Category category = categoryService.update(id, form);
        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(
            @RequestParam(value = "status", required = false) String status) {

        Optional<Boolean> filter = switch (status == null ? "" : status.toLowerCase()) {
            case "active" -> Optional.of(true);
            case "inactive" -> Optional.of(false);
            default -> Optional.empty();
        };

        List<CategoryResponse> response = categoryService.list(filter)
                .stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CategoryResponse> setStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {

        Category category = categoryService.setActive(id, active);
        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}