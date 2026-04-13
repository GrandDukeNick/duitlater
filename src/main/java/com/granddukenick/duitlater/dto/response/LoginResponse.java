package com.granddukenick.duitlater.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}