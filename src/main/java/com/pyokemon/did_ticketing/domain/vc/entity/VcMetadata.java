package com.pyokemon.did_ticketing.domain.vc.entity;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * VC(Verifiable Credential) 메타데이터 엔티티
 */
@Entity
@Table(name = "vc_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VcMetadata {
    
    /**
     * VC 상태 열거형
     */
    public enum Status {
        ACTIVE,    // 활성 상태
        PENDING,   // 대기 상태
        REVOKED,   // 취소됨
        USED,      // 사용됨
        EXPIRED    // 만료됨
    }
    
    @Id
    @Column(name = "vc_metadata_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vc_key")
    private String vcKey;

    @Column(nullable = false)
    private String tenantId;
    
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * VC 상태를 변경합니다.
     * @param status 변경할 상태
     */
    public void changeStatus(Status status) {
        this.status = status;
    }
}
