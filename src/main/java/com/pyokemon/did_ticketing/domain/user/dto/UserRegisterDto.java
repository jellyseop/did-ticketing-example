package com.pyokemon.did_ticketing.domain.user.dto;

import com.pyokemon.did_ticketing.domain.user.entity.User;
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
public class UserRegisterDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public User toEntity() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password); // 비밀번호는 해시하지 않고 그대로 저장
        return user;
    }
} 