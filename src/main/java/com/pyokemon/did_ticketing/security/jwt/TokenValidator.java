package com.pyokemon.did_ticketing.security.jwt;

import com.pyokemon.did_ticketing.security.jwt.authentication.JwtAuthentication;
import com.pyokemon.did_ticketing.security.jwt.authentication.UserPrincipal;
import com.pyokemon.did_ticketing.security.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenValidator {
    private static final Logger log = LoggerFactory.getLogger(TokenValidator.class);
    private final JwtConfigProperties configProperties;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    // BASE64 디코딩 대신 직접 바이트 배열로 변환
                    secretKey = Keys.hmacShaKeyFor(configProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        return secretKey;
    }

    public JwtAuthentication validateToken(String token) {
        String userId = null;

        final Claims claims = this.verifyAndGetClaims(token);
        log.info("claims: {}", claims);
        if (claims == null) {
            return null;
        }

        Date expirationDate = claims.getExpiration();
        log.info("expirationDate: {}", expirationDate);
        if (expirationDate == null || expirationDate.before(new Date())) {
            return null;
        }

        userId = claims.get("userId", String.class);
        log.info("userId: {}", userId);
        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) {
            return null;
        }

        UserPrincipal principal = new UserPrincipal(userId);
        return new JwtAuthentication(principal, token, getGrantedAuthorities("user"));
    }

    private Claims verifyAndGetClaims(String token) {
        Claims claims;

        try {
            log.debug("Verifying token: {}", token);
            claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.debug("Token verified successfully");
        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            return null;
        }

        return claims;
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return grantedAuthorities;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(configProperties.getHeader());
    }
}
