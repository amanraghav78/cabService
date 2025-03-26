package com.example.cabservice.service;

import com.example.cabservice.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    private static final String SECRET_KEY = "supersecretkey123456";

    public String generateToken(String email, Role role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24 ))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, String email){
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getExpiration();
        return expiration.before(new Date());
    }
}
