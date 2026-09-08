package com.tokenization.tokenization.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenValue;

    @Column(nullable = false)
    private String action;

    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}