package kh.edu.cstad.account.command;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditMoneyCommand(
        String transactionId,
        String accountNumber,
        BigDecimal amount
) {
}
