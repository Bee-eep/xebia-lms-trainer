/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

/**
 * CourseStatus - the lifecycle states a course moves through.
 * DRAFT: being authored, safe to edit.
 * PUBLISHED: frozen, visible to learners.
 * ARCHIVED: retired, no longer offered.
 */
public enum CourseStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
