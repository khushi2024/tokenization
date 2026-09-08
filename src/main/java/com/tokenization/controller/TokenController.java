package com.tokenization.controller;

import com.tokenization.dto.TokenResponse;
import com.tokenization.service.TokenService;
import com.tokenization.tokenization.entity.Token;
import com.tokenization.tokenization.entity.TokenStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/generate")
    public TokenResponse generateToken(@RequestBody String value){
        return tokenService.createToken(value);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{tokenValue}")
    public String detokenize(@PathVariable String tokenValue) {
        return tokenService.detokenize(tokenValue);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{tokenValue}/suspend")
    public TokenResponse suspendToken(@PathVariable String tokenValue) {
        return tokenService.suspendToken(tokenValue);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{tokenValue}/activate")
    public TokenResponse activateToken(@PathVariable String tokenValue) {
        return tokenService.activateToken(tokenValue);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{tokenValue}")
    public TokenResponse deleteToken(@PathVariable String tokenValue) {
        return tokenService.deleteToken(tokenValue);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public Page<TokenResponse> getAllTokens(
            @RequestParam(required = false) TokenStatus status,
            Pageable pageable) {

        return tokenService.getAllTokens(status, pageable);
    }
}
