package com.tokenization.repository;

import com.tokenization.tokenization.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tokenization.tokenization.entity.TokenStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByTokenValue(String tokenValue);
    Page<Token> findByStatus(TokenStatus status, Pageable pageable);
}
