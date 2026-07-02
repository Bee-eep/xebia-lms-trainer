/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * CourseRepository - Spring Data generates the implementation at startup.
 * Beyond the standard findById/save/delete from JpaRepository, no custom
 * queries are needed yet for the authoring flow.
 */
public interface CourseRepository extends JpaRepository<Course, UUID> {
}
