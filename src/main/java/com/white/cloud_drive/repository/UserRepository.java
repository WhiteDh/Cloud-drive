package com.white.cloud_drive.repository;

import com.white.cloud_drive.dto.AuthResponseDTO;
import com.white.cloud_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

}
