/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.service;

import com.xebia.lms.trainer.authoring.dto.content.ContentDetailResponse;
import com.xebia.lms.trainer.authoring.dto.content.ContentForm;
import com.xebia.lms.trainer.authoring.dto.course.CourseDetailResponse;
import com.xebia.lms.trainer.authoring.dto.course.CourseForm;
import com.xebia.lms.trainer.authoring.dto.module.ModuleDetailResponse;
import com.xebia.lms.trainer.authoring.dto.module.ModuleForm;
import com.xebia.lms.trainer.authoring.dto.submodule.SubmoduleDetailResponse;
import com.xebia.lms.trainer.authoring.dto.submodule.SubmoduleForm;
import com.xebia.lms.trainer.authoring.model.Category;
import com.xebia.lms.trainer.authoring.model.Content;
import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.model.CourseModule;
import com.xebia.lms.trainer.authoring.model.CourseStatus;
import com.xebia.lms.trainer.authoring.model.Submodule;
import com.xebia.lms.trainer.authoring.repository.CategoryRepository;
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

import java.util.List;
import java.util.UUID;

@Service
public class AuthoringService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final SubmoduleRepository submoduleRepository;
    private final ContentRepository contentRepository;
    private final ApplicationEventPublisher events;

    public AuthoringService(CategoryRepository categoryRepository,
                            CourseRepository courseRepository,
                            ModuleRepository moduleRepository,
                            SubmoduleRepository submoduleRepository,
                            ContentRepository contentRepository,
                            ApplicationEventPublisher events) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.submoduleRepository = submoduleRepository;
        this.contentRepository = contentRepository;
        this.events = events;
    }

    @Transactional
    public Course createCourse(UUID trainerId, CourseForm form) {

        Category category = categoryRepository.findById(form.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("category not found: " + form.categoryId()));

        Course course = new Course();
        course.setTrainerId(trainerId);
        course.setCategory(category);
        course.setTitle(form.title());
        course.setSummary(form.summary());
        course.setLevel(form.level());

        return courseRepository.save(course);
    }

    @Transactional
    public CourseModule addModule(UUID courseId, ModuleForm form) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("course not found: " + courseId));

        CourseModule module = new CourseModule();
        module.setCourse(course);
        module.setTitle(form.title());
        module.setSortOrder(form.sortOrder());

        return moduleRepository.save(module);
    }

    @Transactional
    public Submodule addSubmodule(UUID moduleId, SubmoduleForm form) {

        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("module not found: " + moduleId));

        Submodule submodule = new Submodule();
        submodule.setModule(module);
        submodule.setTitle(form.title());
        submodule.setSortOrder(form.sortOrder());
        submodule.setEstMinutes(form.estMinutes());

        return submoduleRepository.save(submodule);
    }

    @Transactional
    public Content addContent(UUID submoduleId, ContentForm form) {

        Submodule submodule = submoduleRepository.findById(submoduleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("submodule not found: " + submoduleId));

        Content content = new Content();
        content.setSubmodule(submodule);
        content.setType(form.type());
        content.setHeadingLevel(form.headingLevel());
        content.setBody(form.body());
        content.setS3Key(form.s3Key());
        content.setUrl(form.url());
        content.setLanguage(form.language());
        content.setData(form.data());
        content.setSortOrder(form.sortOrder());

        return contentRepository.save(content);
    }

    @Transactional
    public Course publish(UUID courseId) {

        Course course = requireDraftCourse(courseId);

        course.setStatus(CourseStatus.PUBLISHED);

        Course published = courseRepository.save(course);

        events.publishEvent(
                new CoursePublishedEvent(
                        published.getCourseId(),
                        published.getVersion()
                )
        );

        return published;
    }
/**
 * requireDraftCourse - shared guard used by every write operation that
 * must only run while a course is still being authored.
 */
private Course requireDraftCourse(UUID courseId) {

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("course not found: " + courseId));

    if (course.getStatus() != CourseStatus.DRAFT) {
        throw new InvalidCourseStateException(
                "course " + courseId + " is " + course.getStatus() + ", not DRAFT");
    }

    return course;
}

@Transactional(readOnly = true)
public List<Course> getAllCourses() {
    return courseRepository.findAll();
}

@Transactional(readOnly = true)
public List<Course> getCoursesByCategory(UUID categoryId) {
    return courseRepository.findByCategory_CategoryId(categoryId);
}

@Transactional(readOnly = true)
public List<Content> getContents(UUID submoduleId) {
    return contentRepository.findBySubmodule_SubmoduleIdOrderBySortOrder(submoduleId);
}

@Transactional(readOnly = true)
public List<Content> previewContent(UUID submoduleId) {
    return contentRepository.findBySubmodule_SubmoduleIdOrderBySortOrder(submoduleId);
}

@Transactional
public void reorderContent(UUID submoduleId, List<UUID> orderedContentIds) {

    List<Content> contents =
            contentRepository.findBySubmodule_SubmoduleIdOrderBySortOrder(submoduleId);

    for (int i = 0; i < orderedContentIds.size(); i++) {

        final int sortOrder = i;
        UUID id = orderedContentIds.get(i);

        contents.stream()
                .filter(content -> content.getContentId().equals(id))
                .findFirst()
                .ifPresent(content -> {
                    content.setSortOrder(sortOrder);
                    contentRepository.save(content);
                });
    }
}

/**
 * getCourseDetail - retrieves a course and all its modules,
 * submodules and content blocks.
 */
@Transactional(readOnly = true)
public CourseDetailResponse getCourseDetail(UUID courseId) {

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("course not found: " + courseId));

    List<CourseModule> modules =
            moduleRepository.findByCourse_CourseIdOrderBySortOrder(courseId);

    List<ModuleDetailResponse> moduleDetails = modules.stream().map(module -> {

        List<Submodule> submodules =
                submoduleRepository.findByModule_ModuleIdOrderBySortOrder(module.getModuleId());

        List<SubmoduleDetailResponse> submoduleDetails =
                submodules.stream().map(submodule -> {

                    List<Content> contents =
                            contentRepository.findBySubmodule_SubmoduleIdOrderBySortOrder(
                                    submodule.getSubmoduleId());

                    List<ContentDetailResponse> contentDetails =
                            contents.stream()
                                    .map(ContentDetailResponse::from)
                                    .toList();

                    return SubmoduleDetailResponse.from(submodule, contentDetails);

                }).toList();

        return ModuleDetailResponse.from(module, submoduleDetails);

    }).toList();

    return CourseDetailResponse.from(course, moduleDetails);
}

@Transactional
public Course updateCourse(UUID courseId, CourseForm form) {

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("course not found: " + courseId));

    Category category = categoryRepository.findById(form.categoryId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("category not found: " + form.categoryId()));

    course.setCategory(category);
    course.setTitle(form.title());
    course.setSummary(form.summary());
    course.setLevel(form.level());

    return courseRepository.save(course);
}

@Transactional
public void deleteCourse(UUID courseId) {

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("course not found: " + courseId));

    courseRepository.delete(course);
}

@Transactional
public CourseModule updateModule(UUID moduleId, ModuleForm form) {

    CourseModule module = moduleRepository.findById(moduleId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("module not found: " + moduleId));

    module.setTitle(form.title());
    module.setSortOrder(form.sortOrder());

    return moduleRepository.save(module);
}

@Transactional
public void deleteModule(UUID moduleId) {

    CourseModule module = moduleRepository.findById(moduleId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("module not found: " + moduleId));

    moduleRepository.delete(module);
}

@Transactional
public Submodule updateSubmodule(UUID submoduleId, SubmoduleForm form) {

    Submodule submodule = submoduleRepository.findById(submoduleId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("submodule not found: " + submoduleId));

    submodule.setTitle(form.title());
    submodule.setSortOrder(form.sortOrder());
    submodule.setEstMinutes(form.estMinutes());

    return submoduleRepository.save(submodule);
}

@Transactional
public void deleteSubmodule(UUID submoduleId) {

    Submodule submodule = submoduleRepository.findById(submoduleId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("submodule not found: " + submoduleId));

    submoduleRepository.delete(submodule);
}

@Transactional
public Content updateContent(UUID contentId, ContentForm form) {

    Content content = contentRepository.findById(contentId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("content not found: " + contentId));

    content.setType(form.type());
    content.setHeadingLevel(form.headingLevel());
    content.setBody(form.body());
    content.setS3Key(form.s3Key());
    content.setUrl(form.url());
    content.setLanguage(form.language());
    content.setData(form.data());
    content.setSortOrder(form.sortOrder());

    return contentRepository.save(content);
}

@Transactional
public void deleteContent(UUID contentId) {

    Content content = contentRepository.findById(contentId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("content not found: " + contentId));

    contentRepository.delete(content);
}
}