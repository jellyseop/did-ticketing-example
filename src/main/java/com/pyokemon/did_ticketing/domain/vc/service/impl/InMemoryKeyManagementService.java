package com.pyokemon.did_ticketing.domain.vc.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyokemon.did_ticketing.domain.vc.model.VerifiableCredential;
import com.pyokemon.did_ticketing.domain.vc.service.KeyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 메모리 기반 키 관리 서비스 구현체
 * 시크릿 저장소만 사용하는 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryKeyManagementService implements KeyManagementService {
    private static final String SECRET_PATH_PREFIX = "secret/data/did/tenant/";

    private final ObjectMapper objectMapper;
    // 시크릿 저장소
    private final Map<String, Map<String, String>> secretStore = new ConcurrentHashMap<>();

    @Override
    public String generateSecretPath(String tenantId) {
        return SECRET_PATH_PREFIX + tenantId;
    }

    @Override
    public String generateSecretPath(Long tenantId) {
        return SECRET_PATH_PREFIX + tenantId.toString();
    }

    @Override
    public Map<String, String> writeSecret(String path, Map<String, String> data) {
        secretStore.put(path, new HashMap<>(data));
        log.info("시크릿 저장: path={}", path);
        return data;
    }

    @Override
    public Map<String, String> readSecret(String path) {
        Map<String, String> data = secretStore.get(path);
        if (data == null) {
            throw new IllegalArgumentException("시크릿을 찾을 수 없음: " + path);
        }
        return new HashMap<>(data);
    }

    @Override
    public void deleteSecret(String path) {
        secretStore.remove(path);
        log.info("시크릿 삭제: path={}", path);
    }

    @Override
    public String sign(String secretPath, VerifiableCredential vc) {
        try {
            // 시크릿에서 개인키 조회
            Map<String, String> secretData = readSecret(secretPath);
            String privateKeyHex =  secretData.get("private_key");
            if (privateKeyHex == null) {
                throw new IllegalArgumentException("개인키를 찾을 수 없음: " + secretPath);
            }
            
            // 개인키로 ECKeyPair 생성
            BigInteger privateKeyBigInt = new BigInteger(privateKeyHex, 16);
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBigInt);

            //vc string으로 convert
            String message = objectMapper.writeValueAsString(vc);

            // Web3j의 Sign 클래스를 사용하여 서명
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] messageHash = Hash.sha3(messageBytes);
            Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);
            
            // 서명 데이터 조합 (v, r, s)
            byte[] signatureBytes = new byte[65];
            System.arraycopy(signatureData.getR(), 0, signatureBytes, 0, 32);
            System.arraycopy(signatureData.getS(), 0, signatureBytes, 32, 32);
            signatureBytes[64] = signatureData.getV()[0];
            
            String signature = Hex.toHexString(signatureBytes);
            
            log.info("메시지 서명 완료: secretPath={}", secretPath);
            return signature;
        } catch (Exception e) {
            log.error("서명 생성 실패: secretPath={}", secretPath, e);
            throw new RuntimeException("서명 생성 실패", e);
        }
    }

    @Override
    public boolean verify(String secretPath, String message, String signature) {
        try {
            // 시크릿에서 개인키 조회
            Map<String, String> secretData = readSecret(secretPath);
            String privateKeyHex = (String) secretData.get("private_key");
            if (privateKeyHex == null) {
                throw new IllegalArgumentException("개인키를 찾을 수 없음: " + secretPath);
            }
            
            // 개인키로 ECKeyPair 생성
            BigInteger privateKeyBigInt = new BigInteger(privateKeyHex, 16);
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBigInt);
            
            // Web3j의 Sign 클래스를 사용하여 서명 검증
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] messageHash = Hash.sha3(messageBytes);
            
            // 서명 데이터 분리 (v, r, s)
            byte[] signatureBytes = Hex.decode(signature);
            byte[] r = new byte[32];
            byte[] s = new byte[32];
            System.arraycopy(signatureBytes, 0, r, 0, 32);
            System.arraycopy(signatureBytes, 32, s, 0, 32);
            byte v = signatureBytes[64];
            
            Sign.SignatureData signatureData = new Sign.SignatureData(new byte[]{v}, r, s);
            
            // 서명에서 공개키 복구
            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, signatureData);
            
            // 복구된 공개키와 저장된 공개키 비교
            boolean result = publicKey.equals(ecKeyPair.getPublicKey());
            
            log.info("서명 검증: secretPath={}, result={}", secretPath, result);
            return result;
        } catch (Exception e) {
            log.error("서명 검증 실패: secretPath={}", secretPath, e);
            throw new RuntimeException("서명 검증 실패", e);
        }
    }
} 