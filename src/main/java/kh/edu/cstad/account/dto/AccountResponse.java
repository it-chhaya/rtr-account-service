package kh.edu.cstad.account.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
