package com.animalleague.april.auth.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animalleague.april.auth.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
