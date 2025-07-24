package com.pyokemon.did_ticketing.domain.did.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyokemon.did_ticketing.domain.did.model.DidDocument;
import com.pyokemon.did_ticketing.domain.did.repository.DidDocumentRepository;
import com.pyokemon.did_ticketing.domain.did.service.BlockchainService;
import com.pyokemon.did_ticketing.domain.did.service.DidService;
import com.pyokemon.did_ticketing.domain.did.service.KeyManagementService;
import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.tenant.entity.TenantDid;
import com.pyokemon.did_ticketing.domain.tenant.repository.TenantDidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DID 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DidServiceImpl implements DidService {
    
    private static final String DID_METHOD = "pyokemon";
    private static final String SECRET_PATH_PREFIX = "secret/data/did/tenant/";
    
    private final BlockchainService blockchainService;
    private final DidDocumentRepository documentRepository;
    private final KeyManagementService keyManagementService;
    private final TenantDidRepository tenantDidRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public DidResult createDid() {
        try {
            // 1. 새 계정 생성
            BlockchainService.AccountInfo accountInfo = blockchainService.createAccount();
            
            // 2. DID 식별자 생성
            String id = "did:" + DID_METHOD + ":" + UUID.randomUUID().toString();
            
            // 3. 검증 방법 생성
            DidDocument.VerificationMethod verificationMethod = DidDocument.VerificationMethod.builder()
                    .id(id + "#keys-1")
                    .type("EcdsaSecp256k1VerificationKey2019")
                    .controller(id)
                    .publicKeyMultibase("0x" + accountInfo.getPublicKey())
                    .build();
            
            // 4. DID 문서 생성
            DidDocument didDocument = DidDocument.builder()
                    .id(id)
                    .controller(Collections.singletonList(id))
                    .verificationMethod(Collections.singletonList(verificationMethod))
                    .authentication(Collections.singletonList(id + "#keys-1"))
                    .build();
            
            // 5. DID 문서 해시 생성
            String didDocumentJson = objectMapper.writeValueAsString(didDocument);
            String didDocumentHash = calculateSha3Hash(didDocumentJson);
            
            // 6. 블록체인에 DID 문서 해시 등록 (테스트를 위해 로컬 저장소에도 저장)
            blockchainService.registerDid(id, didDocumentHash);
            documentRepository.store(id, didDocumentHash, didDocument);
            
            log.info("DID 생성 완료: did={}, hash={}", id, didDocumentHash);
            return new DidResult(id, didDocument, accountInfo);
        } catch (Exception e) {
            log.error("DID 생성 실패", e);
            throw new RuntimeException("DID 생성 실패", e);
        }
    }
    
    /**
     * 테넌트를 위한 DID 생성
     * @param tenant 테넌트 ID
     * @return 생성된 DID 정보
     */
    @Transactional
    @Override
    public TenantDid createTenantDid(Tenant tenant) {
        try {
            // 1. 기존 테넌트 DID 확인
            //물리적 삭제 X -> !is_valid 인 아이 존재한다면 throw
            if (tenantDidRepository.findByTenant(tenant).isPresent()) {
                throw new IllegalStateException("테넌트가 이미 DID를 가지고 있습니다: " + tenant.getId());
            }
            
            // 2. DID 생성 (블록체인 계정 및 DID 문서 생성)
            DidResult didResult = createDid();
            
            // 3. 개인키 정보 시크릿 저장소에 저장
            String secretPath = SECRET_PATH_PREFIX + tenant.getId();
            Map<String, Object> secretData = new HashMap<>();
            secretData.put("private_key", didResult.getAccountInfo().getPrivateKey());
            secretData.put("public_key", didResult.getAccountInfo().getPublicKey());
            secretData.put("did", didResult.getDid());
            keyManagementService.writeSecret(secretPath, secretData);
            
            // 4. 테넌트-DID 연결 정보 저장
            TenantDid tenantDid = TenantDid.builder()
                    .tenant(tenant)
                    .did(didResult.getDid())
                    .keyId(secretPath)  // keyId에 시크릿 경로 저장
                    .build();
            
            tenant.addDid(tenantDid);
            
            log.info("테넌트 DID 생성 완료: tenantId={}, did={}, secretPath={}", 
                    tenant.getId(), didResult.getDid(), secretPath);
            
            return tenantDid;
        } catch (Exception e) {
            log.error("테넌트 DID 생성 실패: tenantId={}", tenant.getId(), e);
            throw new RuntimeException("테넌트 DID 생성 실패: " + tenant.getId(), e);
        }
    }
    
    /**
     * 테넌트 DID로 메시지 서명
     * @param tenant 테넌트 ID
     * @param message 서명할 메시지
     * @return 서명 값
     */
    public String signWithTenantDid(Tenant tenant, String message) {
        try {
            // 1. 테넌트 DID 정보 조회
            TenantDid tenantDid = tenantDidRepository.findByTenant(tenant)
                    .orElseThrow(() -> new IllegalArgumentException("테넌트 DID를 찾을 수 없습니다: " + tenant.getId()));
            
            // 2. 시크릿 경로로 서명 (keyId에 시크릿 경로가 저장되어 있음)
            String signature = keyManagementService.sign(tenantDid.getKeyId(), message);
            
            log.info("테넌트 DID로 서명 완료: tenantId={}, did={}", tenant.getId(), tenantDid.getDid());
            return signature;
        } catch (Exception e) {
            log.error("테넌트 DID 서명 실패: tenantId={}", tenant.getId(), e);
            throw new RuntimeException("테넌트 DID 서명 실패: " + tenant.getId(), e);
        }
    }
    
    /**
     * 테넌트 DID로 서명 검증
     * @param tenant 테넌트 ID
     * @param message 원본 메시지
     * @param signature 서명 값
     * @return 검증 결과
     */
    public boolean verifyWithTenantDid(Tenant tenant, String message, String signature) {
        try {
            // 1. 테넌트 DID 정보 조회
            TenantDid tenantDid = tenantDidRepository.findByTenant(tenant)
                    .orElseThrow(() -> new IllegalArgumentException("테넌트 DID를 찾을 수 없습니다: " + tenant.getId()));
            
            // 2. 시크릿 경로로 서명 검증 (keyId에 시크릿 경로가 저장되어 있음)
            boolean result = keyManagementService.verify(tenantDid.getKeyId(), message, signature);
            
            log.info("테넌트 DID 서명 검증: tenantId={}, did={}, result={}", 
                    tenant.getId(), tenantDid.getDid(), result);
            return result;
        } catch (Exception e) {
            log.error("테넌트 DID 서명 검증 실패: tenantId={}", tenant.getId(), e);
            throw new RuntimeException("테넌트 DID 서명 검증 실패: " + tenant.getId(), e);
        }
    }

    @Override
    public DidDocument resolveDid(String did) {
        try {
            // 1. 블록체인에서 DID 문서 해시 조회
            String didDocumentHash = blockchainService.resolveDid(did);
            
            // 2. 로컬 저장소에서 DID 문서 조회
            DidDocument document = documentRepository.findByHash(didDocumentHash);
            if (document != null) {
                log.info("DID 문서 조회 성공 (로컬 저장소): did={}", did);
                return document;
            }
            
            // 3. 문서가 없으면 기본 문서 생성 (실제로는 에러를 반환해야 함)
            log.warn("DID 문서를 찾을 수 없음: did={}", did);
            DidDocument.VerificationMethod verificationMethod = DidDocument.VerificationMethod.builder()
                    .id(did + "#keys-1")
                    .type("EcdsaSecp256k1VerificationKey2019")
                    .controller(did)
                    .publicKeyMultibase("0x...")  // 실제 구현에서는 저장소에서 가져온 데이터 사용
                    .build();
            
            return DidDocument.builder()
                    .id(did)
                    .controller(Collections.singletonList(did))
                    .verificationMethod(Collections.singletonList(verificationMethod))
                    .authentication(Collections.singletonList(did + "#keys-1"))
                    .build();
        } catch (Exception e) {
            log.error("DID 조회 실패: " + did, e);
            throw new RuntimeException("DID 조회 실패: " + did, e);
        }
    }
    
    @Override
    public String updateDid(String did, DidDocument document) {
        try {
            // 1. DID 문서 유효성 검증
            if (!did.equals(document.getId())) {
                throw new IllegalArgumentException("DID 식별자가 일치하지 않습니다");
            }
            
            // 2. DID 문서 해시 생성
            String didDocumentJson = objectMapper.writeValueAsString(document);
            String didDocumentHash = calculateSha3Hash(didDocumentJson);
            
            // 3. 블록체인에 DID 문서 해시 업데이트 (테스트를 위해 로컬 저장소도 업데이트)
            String txHash = blockchainService.updateDid(did, didDocumentHash);
            documentRepository.update(did, didDocumentHash, document);
            
            log.info("DID 업데이트 완료: did={}, hash={}, txHash={}", did, didDocumentHash, txHash);
            return txHash;
        } catch (Exception e) {
            log.error("DID 업데이트 실패: " + did, e);
            throw new RuntimeException("DID 업데이트 실패: " + did, e);
        }
    }
    
    @Override
    @Transactional
    public String deactivateDid(String did) {
        try {
            // DID 비활성화 (블록체인 및 로컬 저장소)
            String txHash = blockchainService.deactivateDid(did);
            documentRepository.remove(did);
            
            // 테넌트 DID 연결 정보도 삭제
            tenantDidRepository.deleteByDid(did);
            
            log.info("DID 비활성화 완료: did={}, txHash={}", did, txHash);
            return txHash;
        } catch (Exception e) {
            log.error("DID 비활성화 실패: " + did, e);
            throw new RuntimeException("DID 비활성화 실패: " + did, e);
        }
    }
    
    /**
     * SHA3-256 해시 계산
     */
    private String calculateSha3Hash(String input) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] hashBytes = digestSHA3.digest(input.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(hashBytes);
    }
} 