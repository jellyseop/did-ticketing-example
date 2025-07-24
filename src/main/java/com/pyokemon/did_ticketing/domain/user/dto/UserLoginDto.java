package com.pyokemon.did_ticketing.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청")
public class UserLoginDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @NotBlank(message = "Device ID is required")
    @Schema(description = "디바이스 ID", example = "device-123")
    private String deviceId;
} 