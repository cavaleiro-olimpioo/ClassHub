package com.classhub.api.repository;

import com.classhub.api.domain.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {
    Optional<ApiUser> findByEmailIgnoreCase(String email);
}
