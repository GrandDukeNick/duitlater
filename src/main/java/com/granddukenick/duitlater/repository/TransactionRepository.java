package com.granddukenick.duitlater.repository;

import com.granddukenick.duitlater.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Page<TransactionEntity> findByCustomerUuid(UUID customerUuid, Pageable pageable);
    Page<TransactionEntity> findByCustomerUuidAndStatusCode(UUID customerUuid, String statusCode, Pageable pageable);
    Page<TransactionEntity> findByStatusCode(String statusCode, Pageable pageable);

    Optional<TransactionEntity> findByTransactionUuid(UUID transactionUuid);
    Optional<TransactionEntity> findByIdempotencyKey(UUID idempotencyKey);

}