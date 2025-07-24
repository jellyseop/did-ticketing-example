package com.pyokemon.did_ticketing.domain.user.repository;

import com.pyokemon.did_ticketing.domain.user.entity.User;
import com.pyokemon.did_ticketing.domain.user.entity.UserDid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDidRepository extends JpaRepository<UserDid, Long> {
    /**
     * 사용자의 활성화된 DID 조회
     * @param user 사용자
     * @return DID Optional
     */
    Optional<UserDid> findByUser(User user);

    Optional<UserDid> findByUserId(Long userId);
}
