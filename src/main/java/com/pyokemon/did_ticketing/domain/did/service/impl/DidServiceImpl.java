package com.pyokemon.did_ticketing.domain.did.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyokemon.did_ticketing.common.exception.BadParameter;
import com.pyokemon.did_ticketing.common.exception.NotFound;
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
            documentRepository.store(id, didDocument);
            
            log.info("DID 생성 완료: did={}, hash={}", id, didDocumentHash);
            return new DidResult(id, didDocument, accountInfo);
        } catch (Exception e) {
            log.error("DID 생성 실패", e);
            throw new RuntimeException("DID 생성 실패", e);
        }
    }
    
    /**
     * 테넌트 키 서버측 저장
     * @param didResult 테넌트 DID 정보
     * @return 생성된 DID 정보 경로
     */
    @Transactional
    @Override
    public String storeTenantKeys(Tenant tenant, DidResult didResult) {
        try {
            // 3. 개인키 정보 시크릿 저장소에 저장
            String secretPath = SECRET_PATH_PREFIX + tenant.getId();
            Map<String, Object> secretData = new HashMap<>();
            secretData.put("private_key", didResult.getAccountInfo().getPrivateKey());
            secretData.put("public_key", didResult.getAccountInfo().getPublicKey());
            secretData.put("did", didResult.getDid());

            keyManagementService.writeSecret(secretPath, secretData);
            
            log.info("테넌트 키쌍 저장 완료: tenantId={}, did={}, secretPath={}",
                    tenant.getId(), didResult.getDid(), secretPath);
            
            return secretPath;
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
            String blockchainHash = blockchainService.resolveDid(did);

            // 2. 로컬 저장소에서 DID로 문서 조회
            DidDocument repositoryDocument = documentRepository.findByDid(did);
            if (repositoryDocument == null) {
                log.info("DID 문서 조회 실패 (로컬 저장소): did={}", did);
                throw new NotFound("DID 문서 조회 실패");
            }

            // 3. 문서의 해시 계산 및 블록체인 해시와 비교 검증
            String repositoryJson = objectMapper.writeValueAsString(repositoryDocument);
            String repositoryHash = calculateSha3Hash(repositoryJson);

            if (!repositoryHash.equals(blockchainHash)) {
                throw new BadParameter("DID 문서가 유효하지 않습니다");
            }

            return repositoryDocument;
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
            documentRepository.update(did, document);
            
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