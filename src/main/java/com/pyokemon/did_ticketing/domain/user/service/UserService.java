package com.pyokemon.did_ticketing.domain.user.service;


import com.pyokemon.did_ticketing.common.exception.BadParameter;
import com.pyokemon.did_ticketing.common.exception.NotFound;
import com.pyokemon.did_ticketing.domain.did.service.BlockchainService;
import com.pyokemon.did_ticketing.domain.did.service.DidService;
import com.pyokemon.did_ticketing.domain.user.dto.UserLoginDto;
import com.pyokemon.did_ticketing.domain.user.dto.UserRegisterDto;
import com.pyokemon.did_ticketing.domain.user.entity.User;
import com.pyokemon.did_ticketing.domain.user.entity.UserDevice;
import com.pyokemon.did_ticketing.domain.user.entity.UserDid;
import com.pyokemon.did_ticketing.domain.user.repository.UserDeviceRepository;
import com.pyokemon.did_ticketing.domain.user.repository.UserDidRepository;
import com.pyokemon.did_ticketing.domain.user.repository.UserRepository;
import com.pyokemon.did_ticketing.security.jwt.TokenGenerator;
import com.pyokemon.did_ticketing.security.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserDidRepository userDidRepository;
    private final DidService didService;
    private final TokenGenerator tokenGenerator;

    @Transactional
    public BlockchainService.AccountInfo register(UserRegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성
        User user = registerDto.toEntity();
        userRepository.save(user);

        // 디바이스 생성
        UserDevice device = UserDevice.builder()
                .user(user)
                .deviceId(registerDto.getDeviceId())
                .build();
        userDeviceRepository.save(device);

        //did 요청
        DidService.DidResult didResult = didService.createDid();

        UserDid userDid = UserDid.builder()
                .user(user)
                .did(didResult.getDid())
                .build();
        userDidRepository.save(userDid);

        return didResult.getAccountInfo();
    }

    @Transactional
    public TokenDto.AccessRefreshToken login(UserLoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));

        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new NotFound("아이디 또는 비밀번호를 확인하세요.");
        }

        UserDevice device = userDeviceRepository.findByUser(user)
                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));
        if (!device.getDeviceId().equals(loginDto.getDeviceId())) {
            throw new BadParameter("사용자 디바이스 정보가 일치하지 않습니다.");
        }

        // userId를 문자열로 변환하여 토큰에 포함
        String userId = user.getId().toString();
        log.info("Generating token for user: {}, userId: {}", loginDto.getEmail(), userId);
        return tokenGenerator.generateAccessRefreshToken(loginDto.getEmail(), userId, "mobile");
    }
} 