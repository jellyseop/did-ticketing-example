package com.pyokemon.did_ticketing.domain.event.service.impl;

import com.pyokemon.did_ticketing.domain.event.dto.EventDto;
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
     * 모든 이벤트 목록 조회 (목 데이터)
     * @return 이벤트 목록
     */
    @Override
    public List<EventDto> getAllEvents() {
        return EventMockData.getMockEvents();
    }

    /**
     * ID로 이벤트 조회 (목 데이터)
     * @param id 이벤트 ID
     * @return 이벤트 정보
     */
    @Override
    public EventDto getEventById(String id) {
        return EventMockData.getEventById(id);
    }
    
    /**
     * 사용자 ID로 이벤트 목록 조회 (목 데이터)
     * @param userId 사용자 ID
     * @return 사용자의 이벤트 목록
     */
    @Override
    public List<EventDto> getEventsByUserId(String userId) {
        return EventMockData.getEventsByUserId(userId);
    }
} 