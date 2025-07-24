package com.pyokemon.did_ticketing.domain.user.dto;

import com.pyokemon.did_ticketing.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원가입 요청")
public class UserRegisterDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    private String password;

    @NotBlank(message = "Device ID is required")
    @Schema(description = "디바이스 ID", example = "device-123")
    private String deviceId;

    /**
     * DTO를 User 엔티티로 변환
     * @return User 엔티티
     */
    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }
} 