package com.pyokemon.did_ticketing.domain.event.mock;

import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 테스트용 이벤트 목 데이터
 */
public class EventMockData {

    /**
     * 목 이벤트 데이터 목록 반환
     * @return 이벤트 목록
     */
    public static List<BookingDto> getMockEvents() {
        return Arrays.asList(
                BookingDto.builder()
                .id("1")
                .title("BTS 월드투어 2023")
                .date("2023년 12월 15일 19:30")
                .location("서울 올림픽 주경기장")
                .seat("스탠딩 A구역 12번")
                .issuer("Ticketmaster Korea")
                .issuerId("1")
                .status("active")
                .type("concert")
                .userId("1")
                .build(),
                BookingDto.builder()
                .id("2")
                .title("블랙핑크 팬미팅 2023")
                .date("2023년 12월 20일 18:00")
                .location("고척 스카이돔")
                .seat("지정석 1층 B구역 23열 7번")
                .issuer("YG Entertainment")
                .issuerId("1")
                .status("active")
                .type("fanmeeting")
                .userId("1")
                .build(),
                BookingDto.builder()
                .id("3")
                .title("뮤지컬 라이온킹")
                .date("2023년 12월 25일 14:00")
                .location("샤롯데 씨어터")
                .seat("오케스트라석 1층 중앙 5열 11번")
                .issuer("샤롯데 씨어터")
                .issuerId("1")
                .status("active")
                .type("musical")
                .userId("1")
                .build(),
                BookingDto.builder()
                .id("4")
                .title("2023 프로야구 한국시리즈")
                .date("2023년 11월 7일 18:30")
                .location("잠실 야구장")
                .seat("1루 내야지정석 23구역 4열 8번")
                .issuer("KBO")
                .issuerId("1")
                .status("completed")
                .type("sports")
                .userId("1")
                .build(),
                BookingDto.builder()
                .id("5")
                .title("에드 시런 내한공연")
                .date("2024년 2월 3일 20:00")
                .location("잠실 실내체육관")
                .seat("VIP석 A구역 1열 15번")
                .issuer("Live Nation Korea")
                .issuerId("1")
                .status("upcoming")
                .type("concert")
                .userId("1")
                .build()
        );
    }
    
    /**
     * ID로 목 이벤트 조회
     * @param id 이벤트 ID
     * @return 이벤트 (없으면 null)
     */
    public static BookingDto getBookingById(String id) {
        return getMockEvents().stream()
                .filter(event -> event.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 사용자 ID로 목 이벤트 목록 조회
     * @param userId 사용자 ID
     * @return 사용자의 이벤트 목록
     */
    public static List<BookingDto> getBookingsByUserId(String userId) {
        return getMockEvents().stream()
                .filter(event -> event.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
} 