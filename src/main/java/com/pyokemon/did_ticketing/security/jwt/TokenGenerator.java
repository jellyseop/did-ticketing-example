package com.pyokemon.did_ticketing.security.jwt;

import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenGenerator {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public TokenGenerator(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.accessTokenValidityInMilliseconds}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refreshTokenValidityInMilliseconds}") long refreshTokenValidityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public TokenDto.AccessRefreshToken generateAccessRefreshToken(String email, String userId, String audience) {
        String accessToken = createToken(email, userId, audience, "access", accessTokenValidityInMilliseconds);
        String refreshToken = createToken(email, userId, audience, "refresh", refreshTokenValidityInMilliseconds);

        return TokenDto.AccessRefreshToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String createToken(String email, String userId, String audience, String tokenType, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("aud", audience)
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }
}
