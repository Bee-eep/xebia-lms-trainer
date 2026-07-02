/*
 * Author : Garv
 */
package com.xebia.lms.trainer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    /**
     * s3Presigner - builds a single, reusable S3Presigner bean bound to the
     * configured AWS region. Content Media Service uses this to hand out
     * time-limited upload URLs instead of routing file bytes through our
     * own server, which keeps the trainer service stateless and fast.
     */
    @Bean
    public S3Presigner s3Presigner(@Value("${aws.s3.region}") String region) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }
}
