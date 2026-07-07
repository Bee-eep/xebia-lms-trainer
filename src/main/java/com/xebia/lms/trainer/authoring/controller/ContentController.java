/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.content.ContentForm;
import com.xebia.lms.trainer.authoring.dto.content.ContentResponse;
import com.xebia.lms.trainer.authoring.dto.content.PresignedUploadResponse;
import com.xebia.lms.trainer.authoring.dto.module.ModuleForm;
import com.xebia.lms.trainer.authoring.dto.module.ModuleResponse;
import com.xebia.lms.trainer.authoring.dto.submodule.SubmoduleForm;
import com.xebia.lms.trainer.authoring.dto.submodule.SubmoduleResponse;
import com.xebia.lms.trainer.authoring.model.Content;
import com.xebia.lms.trainer.authoring.model.CourseModule;
import com.xebia.lms.trainer.authoring.model.Submodule;
import com.xebia.lms.trainer.authoring.service.AuthoringService;
import com.xebia.lms.trainer.authoring.service.ContentMediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * ContentController - nested module / submodule / content-block handlers,
 * plus the media presign endpoint content blocks depend on.
 * Required RBAC scope for every endpoint here: TRN:COURSE:MANAGE
 * (enforced once Identity/RBAC is wired in Phase-2; see SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/trainer")
public class ContentController {

    private final AuthoringService authoringService;
    private final ContentMediaService contentMediaService;

    public ContentController(AuthoringService authoringService, ContentMediaService contentMediaService) {
        this.authoringService = authoringService;
        this.contentMediaService = contentMediaService;
    }

    /**
     * addModule - POST /api/v1/trainer/courses/{id}/modules
     * Adds a top-level section to a DRAFT course.
     */
    @PostMapping("/courses/{id}/modules")
    public ResponseEntity<ModuleResponse> addModule(
            @PathVariable("id") UUID courseId,
            @Valid @RequestBody ModuleForm form) {
        CourseModule module = authoringService.addModule(courseId, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuleResponse.from(module));
    }

    /**
     * addSubmodule - POST /api/v1/trainer/modules/{id}/submodules
     * Adds a lesson to a module.
     */
    @PostMapping("/modules/{id}/submodules")
    public ResponseEntity<SubmoduleResponse> addSubmodule(
            @PathVariable("id") UUID moduleId,
            @Valid @RequestBody SubmoduleForm form) {
        Submodule submodule = authoringService.addSubmodule(moduleId, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(SubmoduleResponse.from(submodule));
    }

    /**
     * addContent - POST /api/v1/trainer/submodules/{id}/content
     * Adds a content block (text/code/image/pdf/video) to a lesson. For
     * media types, call the presign endpoint below first to obtain s3Key.
     */
    @PostMapping("/submodules/{id}/content")
    public ResponseEntity<ContentResponse> addContent(
            @PathVariable("id") UUID submoduleId,
            @Valid @RequestBody ContentForm form) {
        Content content = authoringService.addContent(submoduleId, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContentResponse.from(content));
    }

    /**
     * presignMediaUpload - POST /api/v1/trainer/content/media/presign
     * Returns a short-lived S3 PUT URL for an IMAGE/PDF/VIDEO block. The
     * caller uploads the file to that URL directly, then calls addContent
     * with the returned s3Key.
     */
    @PostMapping("/content/media/presign")
    public ResponseEntity<PresignedUploadResponse> presignMediaUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {
        PresignedUploadResponse response = contentMediaService.createUploadUrl(fileName, contentType);
        return ResponseEntity.ok(response);
    }

    // ── Module update / delete ──────────────────────────────────────

    @PutMapping("/modules/{id}")
    public ResponseEntity<ModuleResponse> updateModule(
            @PathVariable("id") UUID moduleId,
            @Valid @RequestBody ModuleForm form) {
        CourseModule updated = authoringService.updateModule(moduleId, form);
        return ResponseEntity.ok(ModuleResponse.from(updated));
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable("id") UUID moduleId) {
        authoringService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    // ── Submodule update / delete ───────────────────────────────────

    @PutMapping("/submodules/{id}")
    public ResponseEntity<SubmoduleResponse> updateSubmodule(
            @PathVariable("id") UUID submoduleId,
            @Valid @RequestBody SubmoduleForm form) {
        Submodule updated = authoringService.updateSubmodule(submoduleId, form);
        return ResponseEntity.ok(SubmoduleResponse.from(updated));
    }

    @DeleteMapping("/submodules/{id}")
    public ResponseEntity<Void> deleteSubmodule(@PathVariable("id") UUID submoduleId) {
        authoringService.deleteSubmodule(submoduleId);
        return ResponseEntity.noContent().build();
    }

    // ── Content update / delete ─────────────────────────────────────

    @PutMapping("/content/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable("id") UUID contentId,
            @Valid @RequestBody ContentForm form) {
        Content updated = authoringService.updateContent(contentId, form);
        return ResponseEntity.ok(ContentResponse.from(updated));
    }

    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable("id") UUID contentId) {
        authoringService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}
