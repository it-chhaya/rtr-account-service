package kh.edu.cstad.account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class DepositCompletedEvent {
    private String transactionId;
    private String toAccountNumber;
    private BigDecimal amount;
    private BigDecimal newBalance;
}
