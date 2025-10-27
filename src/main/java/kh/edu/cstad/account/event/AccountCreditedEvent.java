package kh.edu.cstad.account.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AccountCreditedEvent {
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal balance;
    private String txnId;
}
