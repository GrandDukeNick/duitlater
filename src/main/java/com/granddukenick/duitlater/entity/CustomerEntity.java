package com.granddukenick.duitlater.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USR_CUST")
@Data
@NoArgsConstructor
public class CustomerEntity {

    @Id
    @Column(name = "CUST_UUID")
    private UUID customerUuid;

    @Column(name = "FULL_NM", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PHONE_NBR", length = 20)
    private String phoneNumber;

    @Column(name = "DT_CRT", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "DT_UPD")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        customerUuid = UUID.randomUUID();
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
