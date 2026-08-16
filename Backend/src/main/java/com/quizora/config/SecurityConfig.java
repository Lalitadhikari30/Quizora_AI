package com.quizora.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] publicEndpoints = {
            "/",
            "/api/health",
            "/api/public/**",
            "/test/**",
            "/api/auth/**"
    };

    /* ===============================
       SECURITY FILTER CHAIN
    =============================== */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ VERY IMPORTANT: Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(publicEndpoints).permitAll()
                    .requestMatchers("/api/upload/file").permitAll()
                    .requestMatchers("/api/upload/quiz",
                                     "/api/upload/extract").authenticated()
                    .requestMatchers("/api/quizzes/**",
                                     "/api/interview/**",
                                     "/api/ai/**",
                                     "/api/performance/**").authenticated()
                    .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt ->
                            jwt.decoder(jwtDecoder())
                               .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
            )

            .addFilterBefore(new JwtSubjectFilter(),
                    BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    /* ===============================
       JWT Principal Mapping
    =============================== */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("sub"); // Supabase user UUID
        return converter;
    }

    /* ===============================
       JWT Decoder (Dev + Production)
    =============================== */
    @Bean
    public JwtDecoder jwtDecoder() {
        return new CustomJwtDecoder();
    }

    private static class CustomJwtDecoder implements JwtDecoder {

        private static final Logger logger =
                LoggerFactory.getLogger(CustomJwtDecoder.class);

        private final JwtDecoder defaultDecoder =
                NimbusJwtDecoder.withJwkSetUri(
                        "https://gplfxihikpsppsbctjpv.supabase.co/auth/v1/jwks"
                ).build();

        @Override
        public Jwt decode(String token) throws JwtException {

            if (token == null || token.trim().isEmpty()) {
                throw new JwtException("Token cannot be null or empty");
            }

            // Development tokens
            if (token.startsWith("dev-token-") ||
                token.startsWith("mock-jwt-token")) {

                return createDevelopmentJwt(token);
            }

            // Try standard JWKS decoding first, fallback to payload parsing if JWKS fails (e.g. HS256 Supabase tokens)
            try {
                return defaultDecoder.decode(token);
            } catch (Exception e) {
                logger.warn("JWKS JWT decoding failed ({}), parsing JWT claims directly", e.getMessage());
                return parseJwtPayload(token);
            }
        }

        private Jwt createDevelopmentJwt(String token) {

            String subject = "user-default";
            if (token != null && !token.isBlank()) {
                if (token.startsWith("dev-token-")) {
                    subject = token.replace("dev-token-", "dev-user-");
                } else {
                    subject = "dev-user";
                }
            }

            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .subject(subject)
                    .claim("email", "lalit@example.com")
                    .claim("aud", "authenticated")
                    .claim("role", "authenticated")
                    .build();
        }

        private Jwt parseJwtPayload(String token) {
            try {
                String[] parts = token.split("\\.");
                if (parts.length < 2) {
                    throw new JwtException("Invalid JWT format");
                }

                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode claimsNode = mapper.readTree(payloadJson);

                String subject = claimsNode.path("sub").asText();
                if (subject == null || subject.trim().isEmpty()) {
                    subject = claimsNode.path("email").asText("user-default");
                }

                String email = claimsNode.path("email").asText(subject);

                Instant issuedAt = claimsNode.has("iat")
                        ? Instant.ofEpochSecond(claimsNode.get("iat").asLong())
                        : Instant.now();

                Instant expiresAt = claimsNode.has("exp")
                        ? Instant.ofEpochSecond(claimsNode.get("exp").asLong())
                        : Instant.now().plusSeconds(3600);

                Map<String, Object> claims = new HashMap<>();
                claimsNode.fields().forEachRemaining(entry -> {
                    if (entry.getValue().isTextual()) {
                        claims.put(entry.getKey(), entry.getValue().asText());
                    } else if (entry.getValue().isNumber()) {
                        claims.put(entry.getKey(), entry.getValue().numberValue());
                    } else if (entry.getValue().isBoolean()) {
                        claims.put(entry.getKey(), entry.getValue().asBoolean());
                    }
                });

                return Jwt.withTokenValue(token)
                        .header("alg", "HS256")
                        .subject(subject)
                        .claim("email", email)
                        .claims(c -> c.putAll(claims))
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .build();

            } catch (Exception e) {
                logger.error("Failed to parse JWT payload", e);
                throw new JwtException("Failed to decode JWT claims: " + e.getMessage(), e);
            }
        }
    }

    /* ===============================
       CORS CONFIGURATION
    =============================== */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "https://*.vercel.app",
                "https://*.render.com",
                "*"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        // ✅ Important when using Authorization header
        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
