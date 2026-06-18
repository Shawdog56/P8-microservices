package com.auth.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ALLOW ALL ORIGINS safely using Origin Patterns
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow all standard HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allow all headers (or specify exactly what you need)
        config.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization"
        ));

        // Allow credentials (like JWTs or Cookies).
        // This is why we had to use setAllowedOriginPatterns above!
        config.setAllowCredentials(true);

        // Expose headers so the frontend can read downloaded file names
        config.setExposedHeaders(Arrays.asList(
                "Content-Disposition"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}