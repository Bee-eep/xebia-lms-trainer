/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CategoryRepository - Repository for Category CRUD operations.
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByActive(boolean active);

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    List<Category> findByActiveAndNameContainingIgnoreCase(boolean active, String keyword);
}