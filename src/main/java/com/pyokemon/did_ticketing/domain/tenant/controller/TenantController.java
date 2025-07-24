package com.pyokemon.did_ticketing.domain.tenant.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.tenant.dto.TenantRegisterDto;
import com.pyokemon.did_ticketing.domain.tenant.service.TenantService;
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
@RequestMapping("/api/tenant/v1/auth")
@RequiredArgsConstructor
@Tag(name = "테넌트 API", description = "테넌트 관련 API")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register")
    @Operation(summary = "테넌트 등록", description = "새로운 테넌트를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등록 성공", 
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<ApiResponseDto<String>> register(@Valid @RequestBody TenantRegisterDto registerDto) {
        tenantService.register(registerDto);
        return ResponseEntity.ok(ApiResponseDto.createOk("테넌트 등록이 완료되었습니다."));
    }
    
    // Swagger 문서화를 위한 스키마 클래스
    @Schema(description = "테넌트 등록 응답")
    private static class RegisterResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "응답 데이터")
        private String data;
    }
}
