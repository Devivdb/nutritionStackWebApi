package com.nutritionstack.nutritionstackwebapi.security;

import com.nutritionstack.nutritionstackwebapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            
            log.debug("Processing JWT for user: {}", username);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    Long userId = jwtService.extractUserId(jwt);
                    List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(jwt);
                    
                    log.debug("User {} has authorities: {}", username, authorities);
                    
                    CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                            userDetails,
                            null,
                            authorities,
                            userId
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Authentication set for user: {}", username);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage(), e);
            // Don't throw exception here, just continue with the filter chain
            // The request will be processed without authentication
        }
        
        filterChain.doFilter(request, response);
    }
    
    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        try {
            List<String> authoritiesList = jwtService.extractClaim(token, claims -> 
                (List<String>) claims.get("authorities"));
            
            if (authoritiesList != null) {
                List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                log.debug("Extracted authorities from token: {}", authorities);
                return authorities;
            }
        } catch (Exception e) {
            log.warn("Failed to extract authorities from token: {}", e.getMessage());
        }
        
        // Fallback: load authorities from UserDetailsService
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.extractUsername(token));
            List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                    .collect(Collectors.toList());
            
            log.debug("Loaded authorities from UserDetailsService: {}", authorities);
            return authorities;
        } catch (Exception e) {
            log.error("Failed to load authorities from UserDetailsService: {}", e.getMessage());
            return List.of(); // Return empty list as fallback
        }
    }
}
