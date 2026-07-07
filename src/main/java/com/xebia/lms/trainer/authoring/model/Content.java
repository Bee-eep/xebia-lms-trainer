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
 * Content - a single ordered block belonging to a Submodule.
 * Multiple Content rows together form a complete content page.
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submodule_id", nullable = false)
    private Submodule submodule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentType type;

    @Column(name = "heading_level", length = 4)
    private String headingLevel;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "s3_key", length = 255)
    private String s3Key;

    @Column(length = 500)
    private String url;

    @Column(length = 24)
    private String language;

    /**
     * Stores JSON data for complex block types like:
     * BULLETS, NUMBERED_LIST, ARROW_LIST,
     * TABLE, COMPARISON, etc.
     */
    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}