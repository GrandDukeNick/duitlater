package com.granddukenick.duitlater.repository;

import com.granddukenick.duitlater.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByUsername(String username);
    Optional<StaffEntity> findByEmail(String email);
    boolean existsByUsername(String username);
}