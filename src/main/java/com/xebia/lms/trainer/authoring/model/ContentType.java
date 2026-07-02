/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

/**
 * ContentType - the kind of content block a submodule can contain.
 * TEXT and CODE store their payload in the `body` column; IMAGE, PDF and
 * VIDEO store an S3 key instead and are served via presigned URL.
 */
public enum ContentType {
    TEXT,
    CODE,
    IMAGE,
    PDF,
    VIDEO
}
