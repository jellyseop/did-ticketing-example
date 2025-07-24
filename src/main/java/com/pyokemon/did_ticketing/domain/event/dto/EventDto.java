package com.pyokemon.did_ticketing.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이벤트 정보")
public class EventDto {
    @Schema(description = "이벤트 ID", example = "1")
    private String id;
    
    @Schema(description = "이벤트 제목", example = "BTS 월드투어 2023")
    private String title;
    
    @Schema(description = "이벤트 일시", example = "2023년 12월 15일 19:30")
    private String date;
    
    @Schema(description = "이벤트 장소", example = "서울 올림픽 주경기장")
    private String location;
    
    @Schema(description = "좌석 정보", example = "스탠딩 A구역 12번")
    private String seat;
    
    @Schema(description = "발행자", example = "Ticketmaster Korea")
    private String issuer;

    @Schema(description = "발행자 ID", example = "1")
    private String issuerId;
    
    @Schema(description = "상태", example = "active", allowableValues = {"active", "completed", "upcoming"})
    private String status;
    
    @Schema(description = "이벤트 유형", example = "concert", allowableValues = {"concert", "fanmeeting", "musical", "sports"})
    private String type;
    
    @Schema(description = "예매자 ID", example = "1")
    private String userId;
} 