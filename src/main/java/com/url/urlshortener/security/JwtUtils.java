package com.url.urlshortener.security;

import com.url.urlshortener.service.UserDetailsImpl;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    //Authorization Bearer <Token>
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
    public String generateJwtToken(UserDetailsImpl userDetails) {
         String username = userDetails.getUsername();
         String password = userDetails.getPassword();
         String authorities = userDetails.getAuthorities().stream()
                 .map(authority->authority.getAuthority())
                 .collect(Collectors.joining(","));

         return Jwts.builder()
                 .subject(username)
                 .claim("role",authorities)
                 .issuedAt(new Date())
                 .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                 .signWith(key())
                 .compact();
    }
    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public  boolean validateJwtToken(String token){
        try {
            Jwts.parser().verifyWith((SecretKey) key())
                    .build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token");
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private Key key(){
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
