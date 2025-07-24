package com.pyokemon.did_ticketing.domain.tenant.service;

import com.pyokemon.did_ticketing.domain.did.service.DidService;
import com.pyokemon.did_ticketing.domain.tenant.dto.TenantRegisterDto;
import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;
    private final DidService didService;

    public void register(TenantRegisterDto registerDto) {
        Tenant tenant = registerDto.toEntity();

        tenantRepository.save(tenant);
        didService.createTenantDid(tenant);
    }


}
