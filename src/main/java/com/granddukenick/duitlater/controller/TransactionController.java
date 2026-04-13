package com.granddukenick.duitlater.controller;

import com.granddukenick.duitlater.dto.request.SchedulePaymentRequest;
import com.granddukenick.duitlater.dto.request.UpdateTransactionRequest;
import com.granddukenick.duitlater.dto.response.ApiResponse;
import com.granddukenick.duitlater.entity.TransactionEntity;
import com.granddukenick.duitlater.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> schedulePayment(@Valid @RequestBody SchedulePaymentRequest request) {
        try {
            log.info("POST /api/transactions - customerUuid={}, accountUuid={}, amount={}, scheduledDate={}",
                    request.getCustomerUuid(),
                    request.getFromAmount(),
                    request.getScheduledDate());

            TransactionEntity created = transactionService.schedulePayment(request);
            log.info("Response: Transaction scheduled with UUID: {}", created.getTransactionUuid());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Unexpected error scheduling payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Sorry, we are unable to process your request."));
        }
    }

    // PUT method = UPDATE operation
    @PutMapping("/{transactionUuid}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID transactionUuid,
            @Valid @RequestBody UpdateTransactionRequest request) {

        TransactionEntity updated = transactionService.updateTransactionStatus(transactionUuid, request.getAction(), request.getReason());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<TransactionEntity>> getAllTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "scheduledDate") String sortBy, @RequestParam(defaultValue = "desc") String sortDir, @RequestParam(required = false) UUID customerUuid, @RequestParam(required = false) String statusCode) {
        log.info("GET /api/transactions - page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        log.info("=== DEBUG: Received customerUuid: {} ===", customerUuid);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionEntity> transactions;
        if (customerUuid != null) {
            if (statusCode != null && !statusCode.isEmpty()) {
                transactions = transactionService.getTransactionsByCustomerUuidAndStatus(customerUuid, statusCode, pageable);
                log.info("Filtering by uuid={} and status={}", customerUuid,  statusCode);
            } else {
                transactions = transactionService.getTransactionsByCustomerUuid(customerUuid, pageable);
            }
        } else {
            if (statusCode != null && !statusCode.isEmpty()) {
                transactions = transactionService.getTransactionsByStatus(statusCode, pageable);
                log.info("Filtering by status: {}", statusCode);
            } else {
                transactions = transactionService.getAllTransactions(pageable);
            }
        }
        transactionService.getAllTransactions(pageable);
        log.info("Response: Returning {} transactions", transactions.getNumberOfElements());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/uuid/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<TransactionEntity> getByUuid(@PathVariable UUID uuid) {
        log.info("GET /api/transactions/uuid/{} - Request", uuid);
        TransactionEntity transaction = transactionService.getTransactionByUuid(uuid);
        return ResponseEntity.ok(transaction);
    }
}