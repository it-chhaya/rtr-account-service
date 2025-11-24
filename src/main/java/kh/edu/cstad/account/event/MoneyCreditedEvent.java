package kh.edu.cstad.account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyCreditedEvent {
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Instant timestamp;
}
