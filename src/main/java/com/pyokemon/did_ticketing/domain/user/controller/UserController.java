package com.pyokemon.did_ticketing.domain.user.controller;

import com.pyokemon.did_ticketing.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 정보 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

} 