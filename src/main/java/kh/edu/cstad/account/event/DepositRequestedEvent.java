package kh.edu.cstad.account.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestedEvent {
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private String remark;
    private String transactionId;
}
