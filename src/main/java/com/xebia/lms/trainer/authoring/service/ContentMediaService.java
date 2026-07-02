/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.service;

import com.xebia.lms.trainer.authoring.dto.PresignedUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * ContentMediaService - hands out short-lived, direct-to-S3 upload URLs
 * for IMAGE/PDF/VIDEO content blocks, so large files never pass through
 * the trainer service itself (matches NFR-04: media uses short-lived
 * signed URLs).
 */
@Service
public class ContentMediaService {

    private final S3Presigner presigner;
    private final String bucket;
    private final int presignTtlMinutes;

    public ContentMediaService(S3Presigner presigner,
                                @Value("${aws.s3.bucket}") String bucket,
                                @Value("${aws.s3.presign-ttl-minutes}") int presignTtlMinutes) {
        this.presigner = presigner;
        this.bucket = bucket;
        this.presignTtlMinutes = presignTtlMinutes;
    }

    /**
     * createUploadUrl - builds a fresh S3 object key (so two trainers can
     * never overwrite each other's file, even with the same filename) and
     * returns a presigned PUT URL valid for presignTtlMinutes. The trainer's
     * browser uploads the file straight to S3 using this URL, then submits
     * the returned s3Key in a ContentForm to link it to a content block.
     */
    public PresignedUploadResponse createUploadUrl(String originalFileName, String contentType) {
        String s3Key = "content/" + UUID.randomUUID() + "-" + originalFileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignTtlMinutes))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new PresignedUploadResponse(presigned.url().toString(), s3Key, presignTtlMinutes);
    }
}
