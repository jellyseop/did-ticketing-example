package com.pyokemon.did_ticketing.domain.tenant.entity;

import com.pyokemon.did_ticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 테넌트와 DID 연결 정보 엔티티
 */
@Entity
@Table(name = "tenant_did")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TenantDid {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, unique = true)
    private String did;
    
    @Column(name = "key_id", nullable = false)
    private String keyId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public TenantDid(Tenant tenant, String did, String keyId) {
        this.tenant = tenant;
        this.did = did;
        this.keyId = keyId;
    }

} 