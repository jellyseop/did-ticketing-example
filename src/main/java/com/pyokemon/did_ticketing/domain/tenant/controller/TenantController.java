package com.pyokemon.did_ticketing.domain.tenant.controller;


import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.tenant.dto.TenantRegisterDto;
import com.pyokemon.did_ticketing.domain.tenant.service.TenantService;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tenant/v1/auth")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping(value = "/register")
    public ApiResponseDto<String> register(@RequestBody @Valid TenantRegisterDto registerDto) {
        tenantService.register(registerDto);
        return ApiResponseDto.defaultOk();
    }
}
