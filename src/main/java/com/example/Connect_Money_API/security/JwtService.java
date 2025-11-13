package com.example.Connect_Money_API.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Hashtable;

public class JwtService {
    @Value("$security.jwt.secret_key")
    private String secret;

    @Value("$security.jwt.expiration_time")
    private Long expiration;

    private SecretKey getSigninKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String clientId){
        Hashtable<String, String> claims = new Hashtable<>();
        claims.put("client_id", clientId);

        return Jwts.builder()
                .claims(claims)
                .subject(clientId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigninKey())
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseEncryptedClaims(token)
                .getPayload();
    }

    public String extractClientId(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token){
        try{
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
