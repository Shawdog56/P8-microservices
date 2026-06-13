package com.auth.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if the header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (Remove "Bearer " which is 7 characters)
        jwt = authHeader.substring(7);

        // 4. Extract the username (email) from the token
        userEmail = jwtService.extractUsername(jwt);

        // 5. If we have an email and the user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Get the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. Check if the token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create an authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Enforce the authentication details
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Tell Spring Security that this user is officially logged in for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}