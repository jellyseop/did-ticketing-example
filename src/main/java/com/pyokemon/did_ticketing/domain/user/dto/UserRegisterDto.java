package com.pyokemon.did_ticketing.domain.user.dto;

import com.pyokemon.did_ticketing.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Device ID is required")
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