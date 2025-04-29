/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 *
 * @author raghav.mittal
 */
@Component
public class JwtUtil {

    private final String SECRET_KEY = "raghavmittal";

    public String generateToken(String email, int employeeId, boolean isAdmin) {
        return Jwts.builder()
                .setSubject(email)
                .claim("employeeId", employeeId)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *15))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    public String generateRefreshToken(String email,int employeeId,boolean isAdmin){
        return Jwts.builder()
                .setSubject(email)
                .claim("employeeId",employeeId)
                .claim("isAdmin",isAdmin)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(SignatureAlgorithm.HS512,SECRET_KEY)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token);
        String email = claims.getSubject();
        Long employeeId = claims.get("employeeId", Long.class);
        boolean isAdmin = claims.get("isAdmin", Boolean.class);

        // Create a Spring Security authentication token
        return new UsernamePasswordAuthenticationToken(
                email, // Principal
                null, // Credentials
                isAdmin ? java.util.List.of(() -> "ROLE_ADMIN") : java.util.List.of(() -> "ROLE_USER")
        );
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;  // Invalid token
        }
    }

    public boolean validateToken(String token, String email) {
        Claims claims = extractClaims(token);
        return (claims.getSubject().equals(email) && claims.getExpiration().after(new Date()));
    }
    
    public String extractTokenFromRequest(HttpServletRequest request){
        if(request.getCookies()!=null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("jwt")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
