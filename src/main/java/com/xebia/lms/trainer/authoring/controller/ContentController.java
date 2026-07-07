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

import java.util.List;
import java.util.UUID;

/**
 * ContentController - nested module / submodule / content-block handlers,
 * plus the media presign endpoint content blocks depend on.
 */
@RestController
@RequestMapping("/api/v1/trainer")
public class ContentController {

    private final AuthoringService authoringService;
    private final ContentMediaService contentMediaService;

    public ContentController(AuthoringService authoringService,
                             ContentMediaService contentMediaService) {
        this.authoringService = authoringService;
        this.contentMediaService = contentMediaService;
    }

    // ────────────────────────────────────────────────────────────────
    // Module
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/courses/{id}/modules")
    public ResponseEntity<ModuleResponse> addModule(
            @PathVariable("id") UUID courseId,
            @Valid @RequestBody ModuleForm form) {

        CourseModule module = authoringService.addModule(courseId, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ModuleResponse.from(module));
    }

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

    // ────────────────────────────────────────────────────────────────
    // SubModule
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/modules/{id}/submodules")
    public ResponseEntity<SubmoduleResponse> addSubmodule(
            @PathVariable("id") UUID moduleId,
            @Valid @RequestBody SubmoduleForm form) {

        Submodule submodule = authoringService.addSubmodule(moduleId, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubmoduleResponse.from(submodule));
    }

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

    // ────────────────────────────────────────────────────────────────
    // Content Blocks
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/submodules/{id}/content")
    public ResponseEntity<ContentResponse> addContent(
            @PathVariable("id") UUID submoduleId,
            @Valid @RequestBody ContentForm form) {

        Content content = authoringService.addContent(submoduleId, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContentResponse.from(content));
    }

    @GetMapping("/submodules/{id}/content")
    public ResponseEntity<List<ContentResponse>> getContents(
            @PathVariable("id") UUID submoduleId) {

        return ResponseEntity.ok(
                authoringService.getContents(submoduleId)
                        .stream()
                        .map(ContentResponse::from)
                        .toList()
        );
    }

    @GetMapping("/submodules/{id}/content/preview")
    public ResponseEntity<List<ContentResponse>> previewContent(
            @PathVariable("id") UUID submoduleId) {

        return ResponseEntity.ok(
                authoringService.previewContent(submoduleId)
                        .stream()
                        .map(ContentResponse::from)
                        .toList()
        );
    }

    @PutMapping("/submodules/{id}/content/reorder")
    public ResponseEntity<Void> reorderContent(
            @PathVariable("id") UUID submoduleId,
            @RequestBody List<UUID> orderedContentIds) {

        authoringService.reorderContent(submoduleId, orderedContentIds);
        return ResponseEntity.ok().build();
    }

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

    // ────────────────────────────────────────────────────────────────
    // Media Upload
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/content/media/presign")
    public ResponseEntity<PresignedUploadResponse> presignMediaUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {

        PresignedUploadResponse response =
                contentMediaService.createUploadUrl(fileName, contentType);

        return ResponseEntity.ok(response);
    }
}