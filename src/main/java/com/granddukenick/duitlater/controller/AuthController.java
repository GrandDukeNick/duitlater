package com.granddukenick.duitlater.controller;

import com.granddukenick.duitlater.dto.request.LoginRequest;
import com.granddukenick.duitlater.dto.response.LoginResponse;
import com.granddukenick.duitlater.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final StaffService staffService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - username: {}", request.getUsername());
        String token = staffService.authenticate(request.getUsername(), request.getPassword());
        log.info("Response: Login successful for user: {}", request.getUsername());

        return ResponseEntity.ok(new LoginResponse(token, "Bearer", 86400000L));
    }
}