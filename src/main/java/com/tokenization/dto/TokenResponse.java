package com.tokenization.dto;

import com.tokenization.tokenization.entity.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private Long id;
    private String tokenValue;
    private TokenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
