package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins (for development)
        // In production, replace with specific origins:
        // configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com", "https://app.yourdomain.com"));
        configuration.setAllowedOrigins(List.of("https://auth-app-hazel.vercel.app"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Expose specific headers to the client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"
        ));

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * CORS Filter Bean
     * This filter will be applied to all requests
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

}
