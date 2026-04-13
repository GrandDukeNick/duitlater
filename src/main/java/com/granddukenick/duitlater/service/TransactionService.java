// service/TransactionService.java
package com.granddukenick.duitlater.service;

import com.granddukenick.duitlater.dto.request.SchedulePaymentRequest;
import com.granddukenick.duitlater.entity.TransactionEntity;
import com.granddukenick.duitlater.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ForexService forexService;

    @Transactional
    public TransactionEntity schedulePayment(SchedulePaymentRequest request) {
        log.info("Scheduling payment: customerUuid={}, accountUuid={}, amount={} {}, target currency={}",
                request.getCustomerUuid(),
                request.getAccountUuid(),
                request.getFromAmount(),
                request.getFromCurrency(),
                request.getToCurrency());

        BigDecimal exchangeRate = forexService.getExchangeRate(
                request.getFromCurrency(),
                request.getToCurrency()
        );
        log.info("Exchange rate: 1 {} = {} {}", request.getFromCurrency(), exchangeRate, request.getToCurrency());

        BigDecimal convertedAmount = request.getFromAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("Converted amount: {} {} → {} {}",
                request.getFromAmount(), request.getFromCurrency(),
                convertedAmount, request.getToCurrency());

        if (request.getIdempotencyKey() != null) {
            var existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Idempotent request detected, returning existing transaction: {}",
                        existing.get().getTransactionUuid());
                return existing.get();
            }
        }

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionUuid(UUID.randomUUID());
        transaction.setCustomerUuid(request.getCustomerUuid());
        transaction.setAccountUuid(request.getAccountUuid());

        // Original amount
        transaction.setOriginalAmount(request.getFromAmount());
        transaction.setOriginalCcy(request.getFromCurrency());

        // Converted amount
        transaction.setConvertedAmount(convertedAmount);
        transaction.setConvertedCcy(request.getToCurrency());
        transaction.setExchangeRate(exchangeRate);

        // Payment details
        transaction.setScheduledDate(request.getScheduledDate());
        transaction.setRecipientAccount(request.getRecipientAccount());
        transaction.setRecipientName(request.getRecipientName());

        // Idempotency key
        if (request.getIdempotencyKey() != null) {
            transaction.setIdempotencyKey(request.getIdempotencyKey());
        } else {
            transaction.setIdempotencyKey(UUID.randomUUID());
        }

        // Status
        transaction.setStatusCode("SCHEDULED");

        // Timestamps
        transaction.setCreatedDate(LocalDateTime.now());
        transaction.setUpdatedDate(LocalDateTime.now());

        TransactionEntity saved = transactionRepository.save(transaction);
        log.info("Transaction saved with UUID: {}", saved.getTransactionUuid());

        return saved;
    }

    @Transactional
    public TransactionEntity updateTransactionStatus(UUID transactionUuid, String action, String reason) {
        log.info("Updating transaction {} with action: {}", transactionUuid, action);

        if (!"CANCEL".equalsIgnoreCase(action) && !"COMPLETE".equalsIgnoreCase(action)) {
            throw new RuntimeException("Invalid action. Use 'CANCEL' or 'COMPLETE'");
        }

        TransactionEntity transaction = transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionUuid));

        if (!"SCHEDULED".equals(transaction.getStatusCode())) {
            throw new RuntimeException("Cannot update transaction with status: " + transaction.getStatusCode() +
                    ". Only SCHEDULED transactions can be updated.");
        }

        // Apply the action
        if ("CANCEL".equalsIgnoreCase(action)) {
            // Validation: Cannot cancel past transactions
            if (transaction.getScheduledDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Cannot cancel past transaction. Scheduled date was: " + transaction.getScheduledDate());
            }

            transaction.setStatusCode("CANCELLED");
            transaction.setStatusReason(reason != null ? reason : "Cancelled by user");

        } else if ("COMPLETE".equalsIgnoreCase(action)) {
            // Validation: Can only complete today or past transactions
            if (transaction.getScheduledDate().isAfter(LocalDate.now())) {
                throw new RuntimeException("Cannot complete future transaction. Scheduled date is: " + transaction.getScheduledDate());
            }

            transaction.setStatusCode("COMPLETED");
            transaction.setStatusReason(reason != null ? reason : "Payment successful");
        }

        transaction.setUpdatedDate(LocalDateTime.now());
        transaction.setStatusUpdateDate(LocalDateTime.now());

        log.info("Transaction {} updated to status: {}", transactionUuid, transaction.getStatusUpdateDate());
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> getAllTransactions(Pageable pageable) {
        log.info("Fetching all transactions with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return transactionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> getTransactionsByStatus(String statusCode, Pageable pageable) {
        return transactionRepository.findByStatusCode(statusCode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> getTransactionsByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return transactionRepository.findByCustomerUuid(customerUuid, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> getTransactionsByCustomerUuidAndStatus(UUID customerUuid, String statusCode, Pageable pageable) {
        return transactionRepository.findByCustomerUuidAndStatusCode(customerUuid, statusCode, pageable);
    }

    @Transactional(readOnly = true)
    public TransactionEntity getTransactionByUuid(UUID uuid) {
        return transactionRepository.findByTransactionUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

}