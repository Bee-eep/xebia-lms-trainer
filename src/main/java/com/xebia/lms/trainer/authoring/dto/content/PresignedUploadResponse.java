/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

/**
 * PresignedUploadResponse - handed back to the trainer's browser so it can
 * PUT the file straight to S3. `s3Key` is what the trainer then sends back
 * in ContentForm once the upload finishes, so the content block can be
 * linked to the right object.
 *
 * @param uploadUrl the time-limited S3 PUT URL
 * @param s3Key     the object key the file will be stored under
 * @param expiresInMinutes how long uploadUrl stays valid
 */
public record PresignedUploadResponse(String uploadUrl, String s3Key, int expiresInMinutes) {
}
