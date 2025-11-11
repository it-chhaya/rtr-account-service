package kh.edu.cstad.account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class DepositFailedEvent {
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String reason;
}
