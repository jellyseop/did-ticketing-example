package com.pyokemon.did_ticketing.domain.event.service.impl;

import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;
import com.pyokemon.did_ticketing.domain.event.mock.EventMockData;
import com.pyokemon.did_ticketing.domain.event.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 이벤트 서비스 구현체 (목 데이터 사용)
 */
@Service
public class EventServiceImpl implements EventService {
    /**
     * ID로 이벤트 조회 (목 데이터)
     * @param id 이벤트 ID
     * @return 이벤트 정보
     */
    @Override
    public BookingDto getBookingById(String id) {
        return EventMockData.getBookingById(id);
    }
    
    /**
     * 사용자 ID로 이벤트 목록 조회 (목 데이터)
     * @param userId 사용자 ID
     * @return 사용자의 이벤트 목록
     */
    @Override
    public List<BookingDto> getBookingsByUserId(String userId) {
        return EventMockData.getBookingsByUserId(userId);
    }
} 