/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Content - a single block inside a submodule (text, code, image, PDF or
 * video). Maps 1:1 to the `content` table. TEXT/CODE blocks use `body`;
 * IMAGE/PDF/VIDEO blocks use `s3Key` and are streamed via a presigned URL.
 */
@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "content_id")
    private UUID contentId;

    @Column(name = "submodule_id", nullable = false)
    private UUID submoduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ContentType type;

    @Column(columnDefinition = "text")
    private String body;

    @Column(name = "s3_key", length = 255)
    private String s3Key;

    @Column(length = 24)
    private String language;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
