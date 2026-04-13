package com.granddukenick.duitlater.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SchedulePaymentRequest {

    @NotNull(message = "Customer UUID is required")
    private UUID customerUuid;

    @NotNull(message = "Account UUID is required")
    private UUID accountUuid;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Amount exceeds limit")
    private BigDecimal fromAmount;

    @NotBlank(message = "From currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 letters (USD, MYR, SGD, etc.)")
    private String fromCurrency;

    @NotBlank(message = "To currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 letters (USD, MYR, SGD, etc.)")
    private String toCurrency;

    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDate scheduledDate;

    @NotBlank(message = "Recipient account is required")
    private String recipientAccount;

    private String recipientName;

    private UUID idempotencyKey;
}