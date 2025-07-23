package com.pyokemon.did_ticketing.domain.user.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserLoginDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import com.pyokemon.did_ticketing.domain.user.service.UserService;
import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;

    @PostMapping(value = "/register")
    public ApiResponseDto<String> register(@RequestBody @Valid UserRegisterDto registerDto) {
        userService.register(registerDto);
        return ApiResponseDto.defaultOk();
    }

    @PostMapping(value = "/login")
    public ApiResponseDto<TokenDto.AccessRefreshToken> login(@RequestBody @Valid UserLoginDto loginDto) {
        TokenDto.AccessRefreshToken token = userService.login(loginDto);
        return ApiResponseDto.createOk(token);
    }
}
