package com.granddukenick.duitlater.repository;

import com.granddukenick.duitlater.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByCustomerUuid(UUID customerUuid);
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}