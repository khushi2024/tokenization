package com.tokenization.service;

import com.tokenization.repository.AuditLogRepository;
import com.tokenization.tokenization.entity.AuditLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String tokenValue, String action) {

        String performedBy = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AuditLog auditLog = new AuditLog();

        auditLog.setTokenValue(tokenValue);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}
