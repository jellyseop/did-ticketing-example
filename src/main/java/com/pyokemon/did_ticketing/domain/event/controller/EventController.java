package com.pyokemon.did_ticketing.domain.event.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.event.dto.EventDto;
import com.pyokemon.did_ticketing.domain.event.service.EventService;
import com.pyokemon.did_ticketing.security.jwt.authentication.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 로그인한 사용자의 예매 내역 조회
     * @param userPrincipal 인증된 사용자 정보
     * @return 사용자의 예매 이벤트 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<EventDto>>> getUserEvents(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<EventDto> events = eventService.getEventsByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponseDto.createOk(events));
    }

    /**
     * ID로 이벤트 조회
     * @param id 이벤트 ID
     * @param userPrincipal 인증된 사용자 정보
     * @return 이벤트 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        EventDto event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 자신의 예매 내역만 조회 가능
        if (!event.getUserId().equals(userPrincipal.getUserId())) {
            return ResponseEntity.status(403).body(
                ApiResponseDto.createError("FORBIDDEN", "접근 권한이 없습니다.")
            );
        }
        
        return ResponseEntity.ok(ApiResponseDto.createOk(event));
    }
}
