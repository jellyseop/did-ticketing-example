package com.pyokemon.did_ticketing.domain.vc.service;

import com.pyokemon.did_ticketing.domain.event.dto.BookingDto;
import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import com.pyokemon.did_ticketing.domain.vc.dto.VcRequestDto;
import com.pyokemon.did_ticketing.domain.vc.entity.VcMetadata;
import com.pyokemon.did_ticketing.domain.vc.model.VerifiableCredential;

public interface VcService {
    VerifiableCredential issueVc(VcRequestDto requestDto, String userDid);
    VcMetadata getVcMetadata(String vcKey);
    VcMetadata updateVcStatus(String vcKey, VcMetadata.Status status);
}