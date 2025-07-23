    package com.f4ken0name.github.services;

    import com.f4ken0name.github.utils.BlackListImpl;
    import io.jsonwebtoken.*;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;

    import javax.crypto.spec.SecretKeySpec;
    import java.security.Key;
    import java.time.Instant;
    import java.util.Base64;
    import java.util.Date;
    import java.util.Map;
    import java.util.function.Function;

    @Service
    public class JwtHelper {

        private final Key signingKey;
        private final long tokenValidityMillis;
        private final BlackListImpl tokenBlacklist;

        public JwtHelper(
                @Value("${jwt.secret}") String base64EncodedSecretKey,
                @Value("${jwt.expiration.ms}") long tokenValidityMillis,
                BlackListImpl tokenBlacklist) {
            byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("Secret key must be at least 256 bits (32 bytes)");
            }
            this.signingKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
            this.tokenValidityMillis = tokenValidityMillis;
            this.tokenBlacklist = tokenBlacklist;
        }

        public String createToken(Map<String, Object> claims, String email) {
            Date expiryDate = Date.from(Instant.ofEpochMilli(
                    System.currentTimeMillis() + tokenValidityMillis));

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .claim("email", email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expiryDate)
                    .signWith(signingKey)
                    .compact();
        }

        public String extractEmail(String token) {
            token = removeBearerPrefix(token);
            return extractClaimBody(token, claims -> claims.get("email", String.class));
        }

        public <T> T extractClaimBody(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractClaims(token).getBody();
            return claimsResolver.apply(claims);
        }

        private Jws<Claims> extractClaims(String token) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(token);
            } catch (JwtException e) {
                throw new JwtException("Invalid JWT token: " + e.getMessage());
            }
        }

        public boolean isTokenValid(String token, String email) {
            try {
                token = removeBearerPrefix(token);
                final String tokenEmail = extractEmail(token);
                return email.equals(tokenEmail) &&
                        !isTokenExpired(token) &&
                        !tokenBlacklist.contains(token);
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }

        private boolean isTokenExpired(String token) {
            return extractExpiry(token).before(new Date());
        }

        public Date extractExpiry(String token) {
            return extractClaimBody(removeBearerPrefix(token), Claims::getExpiration);
        }

        private String removeBearerPrefix(String token) {
            if (token != null && token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token;
        }

        public String extractToken(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return null;
        }

    }