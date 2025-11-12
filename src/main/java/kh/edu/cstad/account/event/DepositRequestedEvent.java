package kh.edu.cstad.account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequestedEvent {
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private String remark;
    private String transactionId;
}
