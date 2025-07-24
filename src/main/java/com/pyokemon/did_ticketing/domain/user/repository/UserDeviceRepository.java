package com.pyokemon.did_ticketing.domain.user.repository;

import com.pyokemon.did_ticketing.domain.user.entity.User;
import com.pyokemon.did_ticketing.domain.user.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    /**
     * 사용자의 활성화된 디바이스 조회
     * @param user 사용자
     * @return 디바이스 Optional
     */
    Optional<UserDevice> findByUser(User user);
} 