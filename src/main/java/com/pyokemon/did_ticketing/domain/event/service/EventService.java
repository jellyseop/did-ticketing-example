package com.pyokemon.did_ticketing.domain.event.service;

import com.pyokemon.did_ticketing.domain.event.dto.EventDto;

import java.util.List;

/**
 * 이벤트 서비스 인터페이스
 */
public interface EventService {
    
    /**
     * 모든 이벤트 목록 조회
     * @return 이벤트 목록
     */
    List<EventDto> getAllEvents();
    
    /**
     * ID로 이벤트 조회
     * @param id 이벤트 ID
     * @return 이벤트 정보
     */
    EventDto getEventById(String id);
    
    /**
     * 사용자 ID로 이벤트 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 이벤트 목록
     */
    List<EventDto> getEventsByUserId(String userId);
} 