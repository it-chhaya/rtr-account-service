package kh.edu.cstad.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotNull
        String accountNumber,
        @NotNull
        @Positive
        Long customerId,
        @NotNull
        BigDecimal initBalance,
        @NotNull
        String accountTypeCode,
        @NotNull
        @Positive
        Long branchId
) {
}
