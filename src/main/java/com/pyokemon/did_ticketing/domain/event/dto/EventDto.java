package com.pyokemon.did_ticketing.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String id;
    private String title;
    private String date;
    private String location;
    private String seat;
    private String issuer;
    private String issuerId;
    private String status;
    private String type;
    private String userId;  // 예매자 ID
} 