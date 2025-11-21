package kh.edu.cstad.account.command;

import lombok.Builder;

import java.math.BigDecimal;

// Debit balance from account
@Builder
public record ReserveMoneyCommand(
        String transactionId,
        String accountNumber,
        BigDecimal amount
) {
}
