package com.quizora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${SUPABASE_JWK_URL}")
    private String jwkUrl;

    private final String[] publicEndpoints = {
            "/",
            "/api/health",
            "/api/public/**",
            "/test/**",
            "/api/auth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(publicEndpoints).permitAll()
                .requestMatchers("/api/upload/**").permitAll()
                .requestMatchers("/api/quizzes/**", "/api/interview/**", "/api/ai/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .decoder(jwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
            )
            .addFilterBefore(new JwtSubjectFilter(), org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    /* ---------- VERY IMPORTANT FIX (userId mapping) ---------- */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("sub"); // Supabase user UUID
        return converter;
    }

    /* ---------- JWT Decoder ---------- */
    @Bean
    public JwtDecoder jwtDecoder() {
        return new CustomJwtDecoder();
    }

    /* ---------- Custom JWT Decoder for Development + Production ---------- */
    private static class CustomJwtDecoder implements JwtDecoder {
        private static final Logger logger = LoggerFactory.getLogger(CustomJwtDecoder.class);
        private final JwtDecoder defaultDecoder;

        public CustomJwtDecoder() {
            // Initialize default decoder for production JWTs
            this.defaultDecoder = NimbusJwtDecoder.withJwkSetUri("https://gplfxihikpsppsbctjpv.supabase.co/auth/v1/jwks").build();
        }

        @Override
        public Jwt decode(String token) throws JwtException {
            // Handle development tokens
            if (token.startsWith("dev-token-") || token.startsWith("mock-jwt-token")) {
                return createDevelopmentJwt(token);
            }
            
            // Use default decoder for production tokens
            return defaultDecoder.decode(token);
        }

        private Jwt createDevelopmentJwt(String token) {
            // Create a mock JWT for development tokens
            String subject = "dev-user-" + System.currentTimeMillis();
            
            // Ensure subject is never null
            if (subject == null || subject.trim().isEmpty()) {
                subject = "dev-user-fallback-" + System.currentTimeMillis();
                logger.warn("JWT subject was null, using fallback: {}", subject);
            }
            
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", subject)
                    .claim("email", "devuser@example.com")
                    .claim("aud", "authenticated")
                    .claim("role", "authenticated")
                    .build();
        }
    }

    /* ---------- CORS ---------- */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081", "http://127.0.0.1:3000", "http://127.0.0.1:8081"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        configuration.setExposedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
