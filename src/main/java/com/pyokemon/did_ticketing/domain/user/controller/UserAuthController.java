package com.pyokemon.did_ticketing.domain.user.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.did.service.BlockchainService;
import com.pyokemon.did_ticketing.domain.user.dto.UserLoginDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import com.pyokemon.did_ticketing.domain.user.service.UserService;
import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
public class UserAuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공", 
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ApiResponseDto<BlockchainService.AccountInfo> register(@Valid @RequestBody UserRegisterDto registerDto) {
        BlockchainService.AccountInfo accountInfo = userService.register(registerDto);
        return ApiResponseDto.createOk(accountInfo);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공", 
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ApiResponseDto<TokenDto.AccessRefreshToken>> login(@Valid @RequestBody UserLoginDto loginDto) {
        TokenDto.AccessRefreshToken token = userService.login(loginDto);
        return ResponseEntity.ok(ApiResponseDto.createOk(token));
    }
    
    // Swagger 문서화를 위한 스키마 클래스
    @Schema(description = "회원가입 응답")
    private static class RegisterResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "블록체인 계정 정보")
        private BlockchainService.AccountInfo data;
    }
    
    @Schema(description = "로그인 응답")
    private static class LoginResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "토큰 정보")
        private TokenDto.AccessRefreshToken data;
    }
}
