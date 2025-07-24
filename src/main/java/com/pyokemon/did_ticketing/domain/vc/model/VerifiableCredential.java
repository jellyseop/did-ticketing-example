package com.pyokemon.did_ticketing.domain.vc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Verifiable Credential 모델
 * W3C VC 데이터 모델을 따름
 * https://www.w3.org/TR/vc-data-model/
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifiableCredential {
    
    @Builder.Default
    private List<String> context = List.of("https://www.w3.org/2018/credentials/v1");

    private String issuer;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime issuanceDate;
    
    private CredentialSubject credentialSubject;
    private Proof proof;
    
    /**
     * 자격 증명 주체
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CredentialSubject {
        private String id;  // 사용자 DID
        private String vcKey;
        private TicketInfo ticket;
    }
    
    /**
     * 티켓 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TicketInfo {
        private String event;
        private String seat;
        private String date;
        private String price;
    }
    
    /**
     * 증명 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Proof {
        private String type;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime created;
        
        private String verificationMethod;
        private String signatureValue;
    }
} 