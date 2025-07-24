package com.pyokemon.did_ticketing.domain.tenant.repository;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 테넌트 저장소
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * 이름으로 테넌트 조회
     * @param name 테넌트 이름
     * @return 테넌트 정보
     */
    Optional<Tenant> findByName(String name);
    
    /**
     * 이름으로 테넌트 존재 여부 확인
     * @param name 테넌트 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);
}