package dev.lilianagorga.mywebsite.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
  private final SecretKey secretKey;
  private final long jwtExpirationMs;

  public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMs}") long expirationMs) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.jwtExpirationMs = expirationMs;
  }

  public String generateToken(String email, List<String> roles) {
    return Jwts.builder()
            .setSubject(email)
            .claim("authorities", roles)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS)))
            .signWith(secretKey)
            .compact();
  }

  public String getEmailFromToken(String token) {
    Claims claims = parseToken(token);
    logger.info("Extracted username from token: {}", claims.getSubject());
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception e) {
      logger.error("Token not valid: {}", e.getMessage());
      return false;
    }
  }

  private Claims parseToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(secretKey)
              .build()
              .parseClaimsJws(token)
              .getBody();
      if (claims.getExpiration().before(new Date())) {
        throw new IllegalArgumentException("Token has expired.");
      }

      return claims;
    } catch (Exception e) {
      logger.error("Error during token parsing: {}", e.getMessage());
      throw new IllegalArgumentException("Error during parsing token", e);
    }
  }
}