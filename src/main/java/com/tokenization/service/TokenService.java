package com.tokenization.service;

import com.tokenization.dto.TokenResponse;
import com.tokenization.exception.TokenNotActiveException;
import com.tokenization.exception.TokenNotFoundException;
import com.tokenization.repository.TokenRepository;
import com.tokenization.tokenization.entity.Token;
import com.tokenization.tokenization.entity.TokenStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
// Git workflow practice
// Token suspension feature
@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final EncryptionService encryptionService;
    private final AuditLogService auditLogService;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(TokenRepository tokenRepository, EncryptionService encryptionService, AuditLogService auditLogService) {
        this.tokenRepository = tokenRepository;
        this.encryptionService = encryptionService;
        this.auditLogService = auditLogService;
    }

    public String generateTokenValue(){
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        StringBuilder token = new StringBuilder("TOK_");

        for (byte randomByte : randomBytes) {
            token.append(String.format("%02x", randomByte));
        }

        return token.toString();
    }

    public TokenResponse createToken(String value) {

        String tokenValue = generateTokenValue();

        String encryptedValue = encryptionService.encrypt(value);

        Token token = new Token();

        token.setTokenValue(tokenValue);
        token.setEncryptedValue(encryptedValue);
        token.setStatus(TokenStatus.ACTIVE);
        token.setCreatedAt(LocalDateTime.now());
        token.setUpdatedAt(LocalDateTime.now());

        Token savedToken = tokenRepository.save(token);
        auditLogService.log(tokenValue, "GENERATE");
        return new TokenResponse(
                savedToken.getId(),
                savedToken.getTokenValue(),
                savedToken.getStatus(),
                savedToken.getCreatedAt(),
                savedToken.getUpdatedAt()
        );
    }

    public String detokenize(String tokenValue) {

        Token token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() ->
                        new TokenNotFoundException("Token not found: " + tokenValue));

        if (token.getStatus() != TokenStatus.ACTIVE) {
            throw new TokenNotActiveException(
                    "Token is not active. Current status: " + token.getStatus()
            );
        }

        String decryptedValue =
                encryptionService.decrypt(token.getEncryptedValue());

        auditLogService.log(tokenValue, "DETOKENIZE");

        return decryptedValue;
    }

    public TokenResponse suspendToken(String tokenValue) {

        Token token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() ->
                        new TokenNotFoundException("Token not found: " + tokenValue));

        token.setStatus(TokenStatus.SUSPENDED);
        token.setUpdatedAt(LocalDateTime.now());

        Token savedToken = tokenRepository.save(token);

        auditLogService.log(tokenValue, "SUSPEND");

        return new TokenResponse(
                savedToken.getId(),
                savedToken.getTokenValue(),
                savedToken.getStatus(),
                savedToken.getCreatedAt(),
                savedToken.getUpdatedAt()
        );
    }

    public TokenResponse activateToken(String tokenValue) {

        Token token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() ->
                        new TokenNotFoundException("Token not found: " + tokenValue));

        token.setStatus(TokenStatus.ACTIVE);
        token.setUpdatedAt(LocalDateTime.now());

        Token savedToken = tokenRepository.save(token);
        auditLogService.log(tokenValue, "ACTIVATE");
        return new TokenResponse(
                savedToken.getId(),
                savedToken.getTokenValue(),
                savedToken.getStatus(),
                savedToken.getCreatedAt(),
                savedToken.getUpdatedAt()
        );
    }

    public TokenResponse deleteToken(String tokenValue) {

        Token token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() ->
                        new TokenNotFoundException("Token not found: " + tokenValue));

        token.setStatus(TokenStatus.DELETED);
        token.setUpdatedAt(LocalDateTime.now());

        Token savedToken = tokenRepository.save(token);
        auditLogService.log(tokenValue, "DELETE");
        return new TokenResponse(
                savedToken.getId(),
                savedToken.getTokenValue(),
                savedToken.getStatus(),
                savedToken.getCreatedAt(),
                savedToken.getUpdatedAt()
        );
    }

    public Page<TokenResponse> getAllTokens(
            TokenStatus status,
            Pageable pageable) {

        Page<Token> tokens;

        if (status != null) {
            tokens = tokenRepository.findByStatus(status, pageable);
        } else {
            tokens = tokenRepository.findAll(pageable);
        }

        return tokens.map(token -> new TokenResponse(
                token.getId(),
                token.getTokenValue(),
                token.getStatus(),
                token.getCreatedAt(),
                token.getUpdatedAt()
        ));
    }
}
