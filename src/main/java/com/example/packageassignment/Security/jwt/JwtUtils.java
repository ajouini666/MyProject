package com.example.packageassignment.Security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.packageassignment.Security.services.EmployeeDetailsImpl;

import javax.crypto.SecretKey;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(JwtUtils.class);

    @Value("${app-jwt-expiration-milliseconds}")
    private long jwtExpirationMs;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    public String generateJwtToken(Authentication authentication) {

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        EmployeeDetailsImpl employeePrincipal = (EmployeeDetailsImpl) authentication.getPrincipal();

        return Jwts.builder().setSubject((employeePrincipal
                .getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()
                        + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtSecret).build();
            parser.parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String getEmployeeNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

}
