package com.pyokemon.did_ticketing.domain.event.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;
import com.pyokemon.did_ticketing.domain.event.service.EventService;
import com.pyokemon.did_ticketing.security.jwt.authentication.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Tag(name = "예매 API", description = "예매 조회 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final EventService eventService;

    /**
     * 로그인한 사용자의 예매 내역 조회
     * @param userPrincipal 인증된 사용자 정보
     * @return 사용자의 예매 이벤트 목록
     */
    @GetMapping
    @Operation(summary = "사용자 예매 내역 조회", description = "로그인한 사용자의 모든 예매 내역을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = EventListResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ApiResponseDto<List<BookingDto>>> getUserBookings(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BookingDto> bookings = eventService.getBookingsByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponseDto.createOk(bookings));
    }

    /**
     * ID로 이벤트 조회
     * @param id 이벤트 ID
     * @param userPrincipal 인증된 사용자 정보
     * @return 이벤트 정보
     */
    @GetMapping("/{id}")
    @Operation(summary = "예매 상세 조회", description = "특정 예매의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "이벤트 없음")
    })
    public ResponseEntity<?> getBookingById(
            @Parameter(description = "예매 ID", required = true) @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BookingDto booking = eventService.getBookingById(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 자신의 예매 내역만 조회 가능
        if (!booking.getUserId().equals(userPrincipal.getUserId())) {
            return ResponseEntity.status(403).body(
                ApiResponseDto.createError("FORBIDDEN", "접근 권한이 없습니다.")
            );
        }
        
        return ResponseEntity.ok(ApiResponseDto.createOk(booking));
    }
    
    // Swagger 문서화를 위한 스키마 클래스
    @Schema(description = "이벤트 목록 응답")
    private static class EventListResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "이벤트 목록")
        private List<BookingDto> data;
    }
    
    @Schema(description = "이벤트 상세 응답")
    private static class EventResponse {
        @Schema(description = "성공 여부")
        private String code;
        
        @Schema(description = "응답 메시지")
        private String message;
        
        @Schema(description = "이벤트 정보")
        private BookingDto data;
    }
}
