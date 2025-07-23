package com.pyokemon.did_ticketing.domain.user.service;


import com.pyokemon.did_ticketing.common.exception.BadParameter;
import com.pyokemon.did_ticketing.common.exception.NotFound;
import com.pyokemon.did_ticketing.domain.user.dto.UserLoginDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import com.pyokemon.did_ticketing.domain.user.entity.User;
import com.pyokemon.did_ticketing.domain.user.repository.UserRepository;
import com.pyokemon.did_ticketing.security.jwt.TokenGenerator;
import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;

    @Transactional
    public void register(UserRegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = registerDto.toEntity();
        userRepository.save(user);
    }

    @Transactional
    public TokenDto.AccessRefreshToken login(UserLoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));

        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }

        return tokenGenerator.generateAccessRefreshToken(loginDto.getEmail(), "mobile");
    }
} 