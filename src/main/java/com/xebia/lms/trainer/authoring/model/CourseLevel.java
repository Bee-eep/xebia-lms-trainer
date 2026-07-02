/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

/**
 * CourseLevel - the target audience difficulty for a course. Stored as an
 * enum rather than a raw string so an invalid level fails at compile time,
 * not as a silent typo in the database.
 */
public enum CourseLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}
