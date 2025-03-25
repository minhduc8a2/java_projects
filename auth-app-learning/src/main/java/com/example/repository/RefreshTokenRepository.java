package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.RefreshToken;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken>  findByToken(String token);
    void deleteByUsername(String username);

}
