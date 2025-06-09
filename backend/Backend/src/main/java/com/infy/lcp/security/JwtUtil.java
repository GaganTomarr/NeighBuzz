package com.infy.lcp.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final String secret = "Our_Very_Long_And_Secure_Secret_Key_For_The_Creation_Of_The_JWT_Hopefully_Works";
	SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	private final Long expiration = 8400000L;
	
	public String generateToken(String userName, Integer userId) {
		return Jwts.builder()
				.setSubject(userName)
				.claim("userId", userId)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+expiration))
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
	}
	
	private Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}
	
	public String extractUserName(String token) {
		return getClaims(token).getSubject();
	}
	
	public Integer extractUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("userId", Integer.class);
	}
	
	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		}
		catch(JwtException e) {
			return false;
		}
	}
}
