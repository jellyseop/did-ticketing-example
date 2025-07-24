package com.pyokemon.did_ticketing.domain.vc.service.impl;

import com.pyokemon.did_ticketing.common.exception.BadParameter;
import com.pyokemon.did_ticketing.common.exception.NotFound;
import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;
import com.pyokemon.did_ticketing.domain.event.service.EventService;
import com.pyokemon.did_ticketing.domain.user.entity.UserDid;
import com.pyokemon.did_ticketing.domain.user.repository.UserDidRepository;
import com.pyokemon.did_ticketing.domain.vc.dto.VcRequestDto;
import com.pyokemon.did_ticketing.domain.vc.entity.VcMetadata;
import com.pyokemon.did_ticketing.domain.vc.model.VerifiableCredential;
import com.pyokemon.did_ticketing.domain.vc.repository.VcMetadataRepository;
import com.pyokemon.did_ticketing.domain.vc.service.KeyManagementService;
import com.pyokemon.did_ticketing.domain.vc.service.VcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * VC 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VcServiceImpl implements VcService {

    private final VcMetadataRepository vcMetadataRepository;
    private final KeyManagementService keyManagementService;
    private final EventService eventService;
    private final UserDidRepository userDidRepository;

    @Override
    @Transactional
    public VerifiableCredential issueVc(VcRequestDto requestDto, String userId) {
        // 예매 정보 조회 (실제 구현에서는 예매 서비스에서 조회)
        BookingDto booking = eventService.getBookingById(requestDto.getBookingId());
        if (booking == null) {
            throw new NotFound("해당 예매가 존재하지 않습니다.");
        }

        if (!booking.getUserId().equals(userId)) {
            throw new BadParameter("예매자 정보가 일치하지 않습니다.");
        }

        UserDid userDid = userDidRepository.findByUserId(Long.parseLong(userId))
                .orElseThrow(() -> new NotFound("사용자 DID가 존재하지 않습니다."));

        //booking에 있는 issuer_id로 테넌트 secret_path 조회
        String secretPath = keyManagementService.generateSecretPath(booking.getIssuerId());

        //keyservice에서 테넌트 secret_path로 secrets 조회
        Map<String, String> tenantSecrets = keyManagementService.readSecret(secretPath);

        //테넌트 키 존재x
        if (tenantSecrets == null) {
            throw new NotFound("발행자 정보가 일치하지 않습니다.");
        }

        //VC 생성
        // 1. VC 키 생성 (UUID)
        String vcKey = UUID.randomUUID().toString();
        
        // 2. VC 메타데이터 저장
        VcMetadata vcMetadata = VcMetadata.builder()
                .vcKey(vcKey)
                .tenantId(booking.getIssuerId())
                .bookingId(booking.getId())
                .status(VcMetadata.Status.ACTIVE)
                .build();
        
        vcMetadataRepository.save(vcMetadata);
        log.info("VC 메타데이터 저장 완료: vcKey={}", vcKey);

        // 4. VC 생성
        LocalDateTime now = LocalDateTime.now();

        VerifiableCredential vc = VerifiableCredential.builder()
                .issuer(tenantSecrets.get("did"))
                .issuanceDate(now)
                .credentialSubject(
                        VerifiableCredential.CredentialSubject.builder()
                                .id(userDid.getDid())
                                .vcKey(vcKey)
                                .ticket(
                                        VerifiableCredential.TicketInfo.builder()
                                                .event(booking.getTitle())
                                                .seat(booking.getSeat())
                                                .date(booking.getDate())
                                                .price(booking.getPrice() + " KRW")
                                                .build()
                                )
                                .build()
                )
                .build();
        
        // 5. 서명 생성
        String signature = keyManagementService.sign(secretPath, vc);
        
        // 6. 증명 정보 추가
        vc.setProof(
                VerifiableCredential.Proof.builder()
                        .type("Ed25519Signature2020")
                        .created(now)
                        .verificationMethod(tenantSecrets.get("did") + "#key-1")
                        .signatureValue(signature)
                        .build()
        );
        
        log.info("VC 발급 완료: vcKey={}", vcKey);
        return vc;
    }
    
    /**
     * VC 메타데이터를 조회합니다.
     * @param vcKey VC 키
     * @return VC 메타데이터
     */
    @Override
    @Transactional(readOnly = true)
    public VcMetadata getVcMetadata(String vcKey) {
        return vcMetadataRepository.findByVcKey(vcKey)
                .orElseThrow(() -> new IllegalArgumentException("VC 메타데이터를 찾을 수 없습니다: " + vcKey));
    }
    
    /**
     * VC 상태를 변경합니다.
     * @param vcKey VC 키
     * @param status 변경할 상태
     * @return 업데이트된 VC 메타데이터
     */
    @Override
    @Transactional
    public VcMetadata updateVcStatus(String vcKey, VcMetadata.Status status) {
        VcMetadata vcMetadata = getVcMetadata(vcKey);
        vcMetadata.changeStatus(status);
        return vcMetadataRepository.save(vcMetadata);
    }

} 