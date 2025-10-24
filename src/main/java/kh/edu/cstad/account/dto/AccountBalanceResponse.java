package kh.edu.cstad.account.dto;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        Long id,
        String accountNumber,
        BigDecimal balance
) {
}
