/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

/**
 * ContentType - Supported content blocks for the Content Editor.
 *
 * HEADING        -> body + headingLevel
 * TEXT           -> body
 * BULLETS        -> data (JSON array)
 * ARROW_LIST     -> data (JSON array)
 * NUMBERED_LIST  -> data (JSON array)
 * QUOTE          -> body
 * CODE           -> body + language
 * IMAGE          -> s3Key
 * VIDEO          -> s3Key
 * LINK           -> url + optional body
 * TABLE          -> data (JSON)
 * COMPARISON     -> data (JSON)
 * CALLOUT        -> body
 * DIVIDER        -> no payload
 */
public enum ContentType {

    HEADING,

    TEXT,

    BULLETS,

    ARROW_LIST,

    NUMBERED_LIST,

    QUOTE,

    CODE,

    IMAGE,

    VIDEO,

    LINK,

    TABLE,

    COMPARISON,

    CALLOUT,

    DIVIDER
}