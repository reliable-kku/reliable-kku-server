package com.deundeunhaku.reliablekkuserver.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public Boolean validate(String token, String phoneNumber) {
        String getPhoneNumberWithToken = getPhoneNumber(token);
        return getPhoneNumberWithToken.equals(phoneNumber) && !isTokenExpired(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getPhoneNumber(String token) {
        return extractAllClaims(token).get("phoneNumber", String.class);
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String generateJwtToken(String phoneNumber, long expiredTimeMs) {
        return doGenerateToken(phoneNumber, expiredTimeMs);
    }

    private String doGenerateToken(String phoneNumber, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("phoneNumber", phoneNumber);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
