package com.train.security.utils;


import com.train.security.dto.common.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExp;

    @Value("${app.jwt.refreshExpiration}")
    private long refreshExp;

    public String generateAccessToken(UserDetailImpl userDetail){
        return generateToken(null,userDetail,this.jwtExp);
    }

    public String generateRefreshToken(UserDetailImpl userDetail){
        Map<String ,Object> claims = Map.of("scopes","refresh");
        return generateToken(claims,userDetail,this.refreshExp);
    }

    public String generateToken(Map<String, Object> claims, UserDetailImpl userDetail,long exp) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetail.getEmail())
                .issuer(this.issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getSecretKey(),Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetailImpl userDetail) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetail.getEmail()) && !isTokenExpired(token));
    }

    public boolean validateRefreshToken(String token, UserDetailImpl userDetail) {
        final String username = getUsernameFromToken(token);
        final String scope = getClaimFromToken(token, claims -> claims.get("scopes", String.class));
        if (scope == null || !scope.equals("refresh")) {
            log.warn("Invalid token scope: expected 'refresh' but found '{}'", scope);
            return false;
        }
        return (username.equals(userDetail.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Claims getAllClaims(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (final IllegalArgumentException e){
            log.warn("JWT claims string is empty.");
        }
        catch (final ExpiredJwtException e){
            log.warn("JWT token is expired");
        }
        catch (final SignatureException e){
            log.warn("Invalid JWT signature");
        }
        catch (final MalformedJwtException e){
            log.warn("Invalid JWT token");
        }
        return null;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        if (claims == null) {
            log.warn("Cannot extract claims from token: token is invalid or null");
            throw new JwtException("Cannot extract claims from token: token is invalid or null");
        }
        return claimsResolver.apply(claims);
    }

    private SecretKey getSecretKey() {
        byte[] bytesKey = Base64.getDecoder().decode(this.secretKey);
        return Keys.hmacShaKeyFor(bytesKey);
    }
}
