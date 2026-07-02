/*
 * Author : Garv
 */
package com.xebia.lms.trainer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Phase-1 security chain for the standalone trainer module.
 *
 * Full JWT RS256 verification and dynamic MODULE:ACTION RBAC (e.g.
 * TRN:COURSE:MANAGE) belong here once Identity/RBAC is wired in Phase-2.
 * Until then, endpoints stay open behind the API Gateway boundary so
 * course-authoring flows can be built and tested standalone via Postman,
 * matching the Phase-1 mock-client approach used elsewhere in this module.
 */
@Configuration
public class SecurityConfig {

    /**
     * filterChain - defines the HTTP security rules. Sessions are disabled
     * (stateless, since auth will come from a bearer token, not a cookie)
     * and CSRF is off because there is no browser session to protect.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
