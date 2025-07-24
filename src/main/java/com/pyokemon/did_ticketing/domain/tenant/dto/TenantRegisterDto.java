package com.pyokemon.did_ticketing.domain.tenant.dto;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantRegisterDto {

    @NotBlank(message = "Email is required")
    private String tenantName;

    public Tenant toEntity() {
        Tenant tenant = new Tenant();
        tenant.setName(tenantName);

        return tenant;
    }
}
