package com.granddukenick.duitlater.repository;

import com.granddukenick.duitlater.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByCustomerUuid(UUID customerUuid);
    Optional<AccountEntity> findByAccountUuid(UUID accountUuid);
    Optional<AccountEntity> findByCustomerUuidAndAccountNumber(UUID customerUuid, String accountNumber);
}