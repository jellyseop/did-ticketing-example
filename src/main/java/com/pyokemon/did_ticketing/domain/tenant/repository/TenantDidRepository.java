package com.pyokemon.did_ticketing.domain.tenant.repository;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.tenant.entity.TenantDid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 테넌트 DID 저장소
 */
@Repository
public interface TenantDidRepository extends JpaRepository<TenantDid, Long> {

    /**
     * 테넌트로 DID 정보 조회
     * @param tenant 테넌트 엔티티
     * @return 테넌트 DID 정보
     */
    Optional<TenantDid> findByTenant(Tenant tenant);
    
    /**
     * DID로 테넌트 정보 조회
     * @param did DID 식별자
     * @return 테넌트 DID 정보
     */
    Optional<TenantDid> findByDid(String did);
} 