package com.pyokemon.did_ticketing.domain.tenant.dto;

import com.pyokemon.did_ticketing.domain.tenant.entity.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "테넌트 등록 요청")
public class TenantRegisterDto {

    @NotBlank(message = "Name is required")
    @Schema(description = "테넌트 이름", example = "Example Company")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "이메일", example = "contact@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    private String password;

    public Tenant toEntity() {
        return Tenant.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }
}
