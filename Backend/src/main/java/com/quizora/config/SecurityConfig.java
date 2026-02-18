// package com.quizora.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.jwt.JwtException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.List;
// import java.util.Collections;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Value("${SUPABASE_URL}")
//     private String supabaseUrl;

//     private final String[] publicEndpoints = {
//             "/",
//             "/api/health",
//             "/api/public/**",
//             "/api/test/**"
//     };

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//             .sessionManagement(session -> 
//                     session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                     .requestMatchers(publicEndpoints).permitAll()
//                     .requestMatchers("/api/auth/**").permitAll()
//                     .requestMatchers("/api/upload/**").authenticated()
//                     .requestMatchers("/api/**").authenticated()
//                     .anyRequest().denyAll()
//             )
//             .oauth2ResourceServer(oauth2 -> oauth2
//                     .jwt(jwt -> jwt
//                             .decoder(customJwtDecoder())
//                     )
//             );

//         return http.build();
//     }

//     @Bean
//     public JwtDecoder customJwtDecoder() {
//         return new CustomJwtDecoder();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(
//                 List.of("http://localhost:3000", "http://localhost:8080"));
//         configuration.setAllowedMethods(
//                 List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(List.of("*"));
//         configuration.setAllowCredentials(true);
//         configuration.setExposedHeaders(List.of("Content-Type", "Authorization"));

//         UrlBasedCorsConfigurationSource source =
//                 new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);

//         return source;
//     }

//     private static class CustomJwtDecoder implements JwtDecoder {
//         private final JwtDecoder defaultDecoder;

//         public CustomJwtDecoder() {
//             // Initialize default decoder for production JWTs
//             this.defaultDecoder = NimbusJwtDecoder.withJwkSetUri("https://gplfxihikpsppsbctjpv.supabase.co/auth/v1/jwks").build();
//         }

//         @Override
//         public Jwt decode(String token) throws JwtException {
//             // Handle development tokens
//             if (token.startsWith("dev-token-") || token.startsWith("mock-jwt-token")) {
//                 return createDevelopmentJwt(token);
//             }
            
//             // Use default decoder for production tokens
//             return defaultDecoder.decode(token);
//         }

//         private Jwt createDevelopmentJwt(String token) {
//             // Create a mock JWT for development tokens
//             return Jwt.withTokenValue(token)
//                     .header("alg", "none")
//                     .claim("sub", "dev-user-" + System.currentTimeMillis())
//                     .claim("email", "devuser@example.com")
//                     .claim("aud", "authenticated")
//                     .claim("role", "authenticated")
//                     .build();
//         }
//     }
// }


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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    private final String[] publicEndpoints = {
            "/",
            "/api/health",
            "/api/public/**",
            "/api/test/**",
            "/api/auth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(publicEndpoints).permitAll()
                    .requestMatchers("/api/upload/**").authenticated()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.decoder(customJwtDecoder()))
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public JwtDecoder customJwtDecoder() {
        return new CustomJwtDecoder();
    }

    private static class CustomJwtDecoder implements JwtDecoder {
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
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "dev-user-" + System.currentTimeMillis())
                    .claim("email", "devuser@example.com")
                    .claim("aud", "authenticated")
                    .claim("role", "authenticated")
                    .build();
        }
    }
}
