package com.pyokemon.did_ticketing.domain.event.service;

import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;

import java.util.List;

/**
 * 이벤트 서비스 인터페이스
 */
public interface EventService {
    /**
     * ID로 이벤트 조회
     * @param id 이벤트 ID
     * @return 이벤트 정보
     */
    BookingDto getBookingById(String id);
    
    /**
     * 사용자 ID로 이벤트 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 이벤트 목록
     */
    List<BookingDto> getBookingsByUserId(String userId);
} 