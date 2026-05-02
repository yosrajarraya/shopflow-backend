package com.shopflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // Générer un access token
    public String genererAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return genererToken(claims, userDetails, accessTokenExpiration);
    }

    // Générer un refresh token
    public String genererRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return genererToken(claims, userDetails, refreshTokenExpiration);
    }

    // Extraire l'email depuis le token
    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    // Vérifier si le token est valide
    public boolean estValide(String token, UserDetails userDetails) {
        final String email = extraireEmail(token);
        return email.equals(userDetails.getUsername()) && !estExpire(token);
    }

    // Méthodes privées

    private String genererToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getCle(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean estExpire(String token) {
        return extraireClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraireClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraireTousClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraireTousClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getCle())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getCle() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
