package com.pyokemon.did_ticketing.domain.vc.service;

import com.pyokemon.did_ticketing.domain.vc.model.VerifiableCredential;

import java.util.Map;

/**
 * 키 관리 서비스 인터페이스
 * Web3j ECKeyPair를 사용하는 인터페이스
 */
public interface KeyManagementService {

    /**
     * 시크릿 경로 생성
     * @param tenantId 테넌트 ID
     * @return 시크릿 경로
     */
    String generateSecretPath(String tenantId);
    String generateSecretPath(Long tenantId);

    /**
     * 시크릿 저장
     * @param path 저장 경로
     * @param data 저장할 데이터
     * @return 저장 결과
     */
    Map<String, String> writeSecret(String path, Map<String, String> data);
    
    /**
     * 시크릿 조회
     * @param path 조회 경로
     * @return 조회된 시크릿 데이터
     */
    Map<String, String> readSecret(String path);
    
    /**
     * 시크릿 삭제
     * @param path 삭제할 시크릿 경로
     */
    void deleteSecret(String path);
    
    /**
     * 메시지에 서명
     * @param secretPath 개인키가 저장된 시크릿 경로
     * @param vc 서명할 메시지
     * @return 서명 결과
     */
    String sign(String secretPath, VerifiableCredential vc);
    
    /**
     * 서명 검증
     * @param secretPath 개인키가 저장된 시크릿 경로
     * @param message 원본 메시지
     * @param signature 서명 값
     * @return 검증 결과
     */
    boolean verify(String secretPath, String message, String signature);
} 