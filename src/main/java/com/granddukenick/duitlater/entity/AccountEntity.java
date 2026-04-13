package com.granddukenick.duitlater.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USR_CUST_ACCT")
@Data
@NoArgsConstructor
public class AccountEntity {

    @Id
    @Column(name = "ACCT_UUID", nullable = false, unique = true, updatable = false)
    private UUID accountUuid;

    @Column(name = "CUST_UUID", nullable = false)
    private UUID customerUuid;

    @Column(name = "ACCT_NBR", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "ACCT_TYPE", nullable = false, length = 10)
    private String accountType;

    @Column(name = "ACCT_NAME", length = 100)
    private String accountName;

    @Column(name = "DT_CRT", updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        accountUuid = UUID.randomUUID();
        createdDate = LocalDateTime.now();
    }
}