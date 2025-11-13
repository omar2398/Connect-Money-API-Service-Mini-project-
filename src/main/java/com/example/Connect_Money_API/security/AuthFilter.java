package com.example.Connect_Money_API.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    AuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String clientId = jwtService.extractClientId(token);

        try {
            if(clientId != null && SecurityContextHolder.getContext().getAuthentication() == null){
                if(jwtService.isTokenValid(token)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            clientId,
                            null,
                            Collections.emptyList()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Authentication failed because of: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
