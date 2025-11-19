package com.example.Connect_Money_API.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${security.rate-limit.capacity}")
    private Long capacity;
    @Value("${security.rate-limit.refill-tokens}")
    private Long refillTokens;
    @Value("${security.rate-limit.refill-duration}")
    private Long refillDuration;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket(){
        Bandwidth limit = Bandwidth.classic(capacity, Refill
                .intervally(refillTokens, Duration.ofSeconds(refillDuration)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        Bucket bucket = cache.computeIfAbsent(clientIp, ip -> createNewBucket());

        if (bucket.tryConsume(1)){
            filterChain.doFilter(request, response);
        }else {
            log.warn("Rate limit exceeded for this IP Address: {}", clientIp);
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"Too many requests\"}");
        }
    }

    private String getClientIp(HttpServletRequest request){
        String header = request.getHeader("X-Forwarded-For");
        if(header == null){
            return request.getRemoteAddr();
        }

        return header.split(",")[0];
    }
}
