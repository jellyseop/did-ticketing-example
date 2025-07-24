package com.pyokemon.did_ticketing.domain.vc.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;
import com.pyokemon.did_ticketing.domain.event.mock.EventMockData;
import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.tenant.repository.TenantRepository;
import com.pyokemon.did_ticketing.domain.vc.dto.VcRequestDto;
import com.pyokemon.did_ticketing.domain.vc.entity.VcMetadata;
import com.pyokemon.did_ticketing.domain.vc.model.VerifiableCredential;
import com.pyokemon.did_ticketing.domain.vc.service.VcService;
import com.pyokemon.did_ticketing.security.jwt.authentication.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * VC 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/vc")
@RequiredArgsConstructor
@Tag(name = "VC API", description = "Verifiable Credential 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class VcController {

    private final VcService vcService;
    private final TenantRepository tenantRepository;

    /**
     * 예매 정보로 VC 발급
     * @param requestDto 발급 요청 dto
     * @return 발급된 VC
     */
    @PostMapping("/issue")
    @Operation(summary = "VC 발급", description = "예매 정보를 기반으로 VC를 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "발급 성공", 
            content = @Content(schema = @Schema(implementation = VerifiableCredentialResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "예매 정보 또는 테넌트 정보 없음")
    })
    public ApiResponseDto<VerifiableCredential> issueVc(
            @Parameter(description = "예매 ID", required = true)@Valid @RequestBody VcRequestDto requestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        // VC 발급
        VerifiableCredential vc = vcService.issueVc(requestDto, userPrincipal.getUserId());
        
        return ApiResponseDto.createOk(vc);
    }
    
    /**
     * VC 상태 조회
     * @param vcKey VC 키
     * @return VC 메타데이터
     */
    @GetMapping("/{vcKey}/status")
    @Operation(summary = "VC 상태 조회", description = "VC 키로 VC 상태를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = VcMetadataResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "VC 메타데이터 없음")
    })
    public ResponseEntity<ApiResponseDto<VcMetadata>> getVcStatus(
            @Parameter(description = "VC 키", required = true) @PathVariable String vcKey) {
        
        VcMetadata metadata = vcService.getVcMetadata(vcKey);
        return ResponseEntity.ok(ApiResponseDto.createOk(metadata));
    }
    
    /**
     * VC 상태 변경
     * @param vcKey VC 키
     * @param status 변경할 상태
     * @return 업데이트된 VC 메타데이터
     */
    @PatchMapping("/{vcKey}/status")
    @Operation(summary = "VC 상태 변경", description = "VC 키로 VC 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "변경 성공", 
            content = @Content(schema = @Schema(implementation = VcMetadataResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "VC 메타데이터 없음")
    })
    public ResponseEntity<ApiResponseDto<VcMetadata>> updateVcStatus(
            @Parameter(description = "VC 키", required = true) @PathVariable String vcKey,
            @Parameter(description = "변경할 상태", required = true) @RequestParam VcMetadata.Status status) {
        
        VcMetadata metadata = vcService.updateVcStatus(vcKey, status);
        return ResponseEntity.ok(ApiResponseDto.createOk(metadata));
    }
    
    // Swagger 문서화를 위한 스키마 클래스
    @Schema(description = "VC 응답")
    private static class VerifiableCredentialResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "VC 정보")
        private VerifiableCredential data;
    }
    
    @Schema(description = "VC 메타데이터 응답")
    private static class VcMetadataResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "VC 메타데이터")
        private VcMetadata data;
    }
}
