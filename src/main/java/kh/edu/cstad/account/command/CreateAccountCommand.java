package kh.edu.cstad.account.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountCommand(
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
