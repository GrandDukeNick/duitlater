// service/StaffService.java
package com.granddukenick.duitlater.service;

import com.granddukenick.duitlater.entity.StaffEntity;
import com.granddukenick.duitlater.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public String authenticate(String username, String rawPassword) {
        log.info("Authentication attempt for user: {}", username);

        StaffEntity staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!staff.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!passwordEncoder.matches(rawPassword, staff.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        staff.setLastLogin(LocalDateTime.now());
        staffRepository.save(staff);

        String token = jwtService.generateToken(staff.getUsername(), staff.getRole(), staff.getStaffUuid().toString());
        log.info("Authentication successful for user: {}", username);

        return token;
    }
}