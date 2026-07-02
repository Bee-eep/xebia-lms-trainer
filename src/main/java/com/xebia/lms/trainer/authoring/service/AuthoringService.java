/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.service;

import com.xebia.lms.trainer.authoring.dto.ContentForm;
import com.xebia.lms.trainer.authoring.dto.CourseForm;
import com.xebia.lms.trainer.authoring.dto.ModuleForm;
import com.xebia.lms.trainer.authoring.dto.SubmoduleForm;
import com.xebia.lms.trainer.authoring.model.Content;
import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.model.CourseModule;
import com.xebia.lms.trainer.authoring.model.CourseStatus;
import com.xebia.lms.trainer.authoring.model.Submodule;
import com.xebia.lms.trainer.authoring.repository.ContentRepository;
import com.xebia.lms.trainer.authoring.repository.CourseRepository;
import com.xebia.lms.trainer.authoring.repository.ModuleRepository;
import com.xebia.lms.trainer.authoring.repository.SubmoduleRepository;
import com.xebia.lms.trainer.common.event.CoursePublishedEvent;
import com.xebia.lms.trainer.common.exception.InvalidCourseStateException;
import com.xebia.lms.trainer.common.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AuthoringService - owns the course-authoring lifecycle: create a course,
 * build it out with modules -> submodules -> content, then publish it.
 *
 * Dependencies arrive through the constructor (never @Autowired fields),
 * which keeps them explicit and makes the service trivial to unit test
 * with mock repositories.
 */
@Service
public class AuthoringService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final SubmoduleRepository submoduleRepository;
    private final ContentRepository contentRepository;
    private final ApplicationEventPublisher events;

    public AuthoringService(CourseRepository courseRepository,
                             ModuleRepository moduleRepository,
                             SubmoduleRepository submoduleRepository,
                             ContentRepository contentRepository,
                             ApplicationEventPublisher events) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.submoduleRepository = submoduleRepository;
        this.contentRepository = contentRepository;
        this.events = events;
    }

    /**
     * createCourse - starts a new course for a trainer, always in DRAFT
     * status at version 1. trainerId comes from the caller's identity, not
     * the request body, so a trainer can never author a course as someone
     * else.
     */
    @Transactional
    public Course createCourse(UUID trainerId, CourseForm form) {
        Course course = new Course();
        course.setTrainerId(trainerId);
        course.setDomainId(form.domainId());
        course.setTitle(form.title());
        course.setSummary(form.summary());
        course.setLevel(form.level());
        return courseRepository.save(course);
    }

    /**
     * addModule - appends a top-level section to a course. Guarded so
     * modules can only be added while the course is still DRAFT; once
     * PUBLISHED the structure is frozen for learners already enrolled.
     */
    @Transactional
    public CourseModule addModule(UUID courseId, ModuleForm form) {
        Course course = requireDraftCourse(courseId);

        CourseModule module = new CourseModule();
        module.setCourseId(course.getCourseId());
        module.setTitle(form.title());
        module.setSortOrder(form.sortOrder());
        return moduleRepository.save(module);
    }

    /**
     * addSubmodule - appends a lesson to a module. Guarded so the parent
     * module must exist before a lesson can be attached to it.
     */
    @Transactional
    public Submodule addSubmodule(UUID moduleId, SubmoduleForm form) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("module not found: " + moduleId));

        Submodule submodule = new Submodule();
        submodule.setModuleId(module.getModuleId());
        submodule.setTitle(form.title());
        submodule.setSortOrder(form.sortOrder());
        submodule.setEstMinutes(form.estMinutes());
        return submoduleRepository.save(submodule);
    }

    /**
     * addContent - appends a content block (text, code, image, PDF or
     * video) to a lesson. Guarded so the parent submodule must exist
     * first.
     */
    @Transactional
    public Content addContent(UUID submoduleId, ContentForm form) {
        Submodule submodule = submoduleRepository.findById(submoduleId)
                .orElseThrow(() -> new ResourceNotFoundException("submodule not found: " + submoduleId));

        Content content = new Content();
        content.setSubmoduleId(submodule.getSubmoduleId());
        content.setType(form.type());
        content.setBody(form.body());
        content.setS3Key(form.s3Key());
        content.setLanguage(form.language());
        content.setSortOrder(form.sortOrder());
        return contentRepository.save(content);
    }

    /**
     * publish - freezes the current DRAFT version of a course so learners
     * see a stable snapshot. Only a DRAFT course can be published; calling
     * this twice, or on an ARCHIVED course, is rejected rather than
     * silently accepted, per the "fail fast" standard. Emits
     * CoursePublishedEvent so other modules (Analytics, Learner) can react
     * without AuthoringService knowing they exist.
     */
    @Transactional
    public Course publish(UUID courseId) {
        Course course = requireDraftCourse(courseId);

        course.setStatus(CourseStatus.PUBLISHED);
        Course published = courseRepository.save(course);

        events.publishEvent(new CoursePublishedEvent(published.getCourseId(), published.getVersion()));
        return published;
    }

    /**
     * requireDraftCourse - shared guard used by every write operation that
     * must only run while a course is still being authored. Centralizing
     * this check means the DRAFT-only rule can't drift between methods.
     */
    private Course requireDraftCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("course not found: " + courseId));

        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new InvalidCourseStateException(
                    "course " + courseId + " is " + course.getStatus() + ", not DRAFT");
        }
        return course;
    }
}
