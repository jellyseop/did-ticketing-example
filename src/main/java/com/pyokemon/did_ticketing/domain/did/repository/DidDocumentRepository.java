package com.pyokemon.did_ticketing.domain.did.repository;

import com.pyokemon.did_ticketing.domain.did.model.DidDocument;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DID Document를 메모리에 저장하고 관리하는 저장소
 * 테스트 및 개발 환경에서 블록체인과 IPFS 대신 사용
 */
@Slf4j
@Component
public class DidDocumentRepository {

    // DID -> Document 매핑
    private Map<String, DidDocument> didToDocumentMap;
    
    // 해시값 -> Document 매핑
    private Map<String, DidDocument> hashToDocumentMap;
    
    // DID -> 해시값 매핑
    private Map<String, String> didToHashMap;

    @PostConstruct
    public void init() {
        didToDocumentMap = new ConcurrentHashMap<>();
        hashToDocumentMap = new ConcurrentHashMap<>();
        didToHashMap = new ConcurrentHashMap<>();
        log.info("DID Document Repository 초기화 완료");
    }

    /**
     * DID Document 저장
     * @param did DID 식별자
     * @param documentHash 문서 해시값
     * @param document DID 문서
     */
    public void store(String did, String documentHash, DidDocument document) {
        didToDocumentMap.put(did, document);
        hashToDocumentMap.put(documentHash, document);
        didToHashMap.put(did, documentHash);
        log.info("DID Document 저장: did={}, hash={}", did, documentHash);
    }


    /**
     * 해시값으로 문서 조회
     * @param hash 문서 해시값
     * @return DID 문서
     */
    public DidDocument findByHash(String hash) {
        return hashToDocumentMap.get(hash);
    }

    /**
     * DID로 해시값 조회
     * @param did DID 식별자
     * @return 문서 해시값
     */
    public String findHashByDid(String did) {
        return didToHashMap.get(did);
    }

    /**
     * DID 문서 업데이트
     * @param did DID 식별자
     * @param documentHash 새 문서 해시값
     * @param document 새 DID 문서
     */
    public void update(String did, String documentHash, DidDocument document) {
        // 기존 해시값 제거
        String oldHash = didToHashMap.get(did);
        if (oldHash != null) {
            hashToDocumentMap.remove(oldHash);
        }
        
        // 새 문서 저장
        didToDocumentMap.put(did, document);
        hashToDocumentMap.put(documentHash, document);
        didToHashMap.put(did, documentHash);
        log.info("DID Document 업데이트: did={}, hash={}", did, documentHash);
    }

    /**
     * DID 문서 삭제 (비활성화)
     * @param did DID 식별자
     */
    public void remove(String did) {
        String hash = didToHashMap.get(did);
        if (hash != null) {
            hashToDocumentMap.remove(hash);
        }
        didToDocumentMap.remove(did);
        didToHashMap.remove(did);
        log.info("DID Document 삭제: did={}", did);
    }
} 