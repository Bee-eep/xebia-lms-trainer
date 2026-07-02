
package com.xebia.lms.trainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrainerApplication {

    /**
     * main - JVM entry point. Boots the Spring context, which in turn
     * runs Flyway migrations, opens the DB connection pool, and starts
     * the embedded web server on the configured port.
     */
    public static void main(String[] args) {
        SpringApplication.run(TrainerApplication.class, args);
    }
}
