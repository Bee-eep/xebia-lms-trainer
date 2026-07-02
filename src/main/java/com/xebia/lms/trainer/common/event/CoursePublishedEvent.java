/*
 * Author : Garv
 */
package com.xebia.lms.trainer.common.event;

import java.util.UUID;

/**
 * CoursePublishedEvent - fired by AuthoringService the moment a course's
 * status flips to PUBLISHED. Other parts of the system (Analytics, the
 * Learner module, a future Kafka outbox) can listen for this without the
 * AuthoringService needing to know they exist - this is the Observer
 * pattern in practice, same as described in the design-patterns brief.
 *
 * @param courseId  the course that was published
 * @param version   the frozen version number learners will see
 */
public record CoursePublishedEvent(UUID courseId, int version) {
}
