package com.granddukenick.duitlater.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateTransactionRequest {

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(CANCEL|COMPLETE)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Action must be CANCEL or COMPLETE")
    private String action;
    private String reason;
}