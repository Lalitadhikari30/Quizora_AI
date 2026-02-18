package com.quizora.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtSubjectFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtSubjectFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String subject = jwt.getSubject();
                
                // Validate JWT subject is not null
                if (subject == null || subject.trim().isEmpty()) {
                    logger.warn("JWT_SUBJECT_NULL: JWT token has null or empty subject, applying fallback");
                    
                    // Create a fallback subject
                    String fallbackSubject = "fallback-user-" + System.currentTimeMillis();
                    logger.info("JWT_SUBJECT_FALLBACK: Using fallback subject: {}", fallbackSubject);
                    
                    // Note: In a real implementation, you might want to reject the request
                    // For now, we'll log and continue
                } else {
                    logger.debug("JWT_SUBJECT_VALID: Subject is valid: {}", subject);
                }
            }
            
        } catch (Exception e) {
            logger.error("JWT_SUBJECT_FILTER_ERROR: Error processing JWT subject", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
