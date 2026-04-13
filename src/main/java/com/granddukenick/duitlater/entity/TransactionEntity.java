package com.granddukenick.duitlater.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TRX_DUITLATER")
@Data
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @Column(name = "TRX_UUID")
    private UUID transactionUuid;

    @Column(name = "CUST_UUID")
    private UUID customerUuid;

    @Column(name = "ACCT_UUID")
    private UUID accountUuid;

    @Column(name = "ORIG_AMT")
    private BigDecimal originalAmount;

    @Column(name = "ORIG_CCY")
    private String originalCcy;

    @Column(name = "CONV_AMT")
    private BigDecimal convertedAmount;

    @Column(name = "CONV_CCY")
    private String convertedCcy;

    @Column(name = "EXCH_RATE")
    private BigDecimal exchangeRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUST_UUID", insertable = false, updatable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCT_UUID", insertable = false, updatable = false)
    private AccountEntity account;

    @Column(name = "DT_SCHED", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "TO_ACCT_NBR", nullable = false, length = 50)
    private String recipientAccount;

    @Column(name = "TO_CUST_NAME", length = 100)
    private String recipientName;

    @Column(name = "STS_CD", length = 4)
    private String statusCode = "PDNG";

    @Column(name = "STS_RSN", length = 200)
    private String statusReason;

    @Column(name = "STS_UPD_DT")
    private LocalDateTime statusUpdateDate;

    @Column(name = "IDEMP_KEY", nullable = false, unique = true, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "ERR_MSG", length = 500)
    private String errorMessage;

    @Column(name = "DT_CRT", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "DT_UPD")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        transactionUuid = UUID.randomUUID();
        idempotencyKey = UUID.randomUUID();
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        statusUpdateDate = LocalDateTime.now();
        if (convertedCcy == null) convertedCcy = "MYR";
        if (statusCode == null) statusCode = "SCHEDULED";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
        statusUpdateDate = LocalDateTime.now();
    }
}