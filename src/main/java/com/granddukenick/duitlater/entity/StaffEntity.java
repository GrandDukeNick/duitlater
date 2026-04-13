package com.granddukenick.duitlater.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USR_STAFF")
@Data
@NoArgsConstructor
public class StaffEntity {

    @Id
    @Column(name = "STAFF_UUID", nullable = false, unique = true, updatable = false)
    private UUID staffUuid;

    @Column(name = "USR_NM", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PWD_HSH", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "FULL_NAME", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "DT_CRT", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "DT_UPD")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        staffUuid = UUID.randomUUID();
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}