package com.brokerage.brokerageapi.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {
    private static final String SECRET_KEY = "very_secret_key_12345678901234567890123456789012";
    private static final long EXPIRATION = 3600000;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                   .setSubject(userDetails.getUsername())
                   .claim("roles", userDetails.getAuthorities().stream()
                                              .map(GrantedAuthority::getAuthority).toList())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                   .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                   .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }
}
