package kh.edu.cstad.account.query;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        Long id,
        String accountNumber,
        BigDecimal balance
) {
}
