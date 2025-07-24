package com.pyokemon.did_ticketing.domain.user.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.did.service.BlockchainService;
import com.pyokemon.did_ticketing.domain.user.dto.UserLoginDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import com.pyokemon.did_ticketing.domain.user.service.UserService;
import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponseDto<BlockchainService.AccountInfo> register(@Valid @RequestBody UserRegisterDto registerDto) {
        BlockchainService.AccountInfo accountInfo = userService.register(registerDto);
        return ApiResponseDto.createOk(accountInfo);
    }

    @PostMapping("/login")
    public ApiResponseDto<TokenDto.AccessRefreshToken> login(@Valid @RequestBody UserLoginDto loginDto) {
        TokenDto.AccessRefreshToken token = userService.login(loginDto);
        return ApiResponseDto.createOk(token);
    }
}
