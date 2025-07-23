package com.pyokemon.did_ticketing.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessRefreshToken {
        private String grantType;
        private String accessToken;
        private String refreshToken;
    }
}
