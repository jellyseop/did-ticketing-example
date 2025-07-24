package com.pyokemon.did_ticketing.domain.vc.repository;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.vc.entity.VcMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VC 메타데이터 리포지토리
 */
@Repository
public interface VcMetadataRepository extends JpaRepository<VcMetadata, Long> {
    
    /**
     * VC 키로 메타데이터 조회
     * @param vcKey VC 키
     * @return VC 메타데이터
     */
    Optional<VcMetadata> findByVcKey(String vcKey);
}

