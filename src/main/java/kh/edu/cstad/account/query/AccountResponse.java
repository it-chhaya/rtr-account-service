package kh.edu.cstad.account.query;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        BigDecimal balance,
        String accountTypeCode,
        String branch,
        String status,
        String createdAt
) {
}
