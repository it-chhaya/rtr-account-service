package kh.edu.cstad.account.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountCreatedEvent {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private BigDecimal balance;
    private String accountTypeCode;
    private String branch;
    private LocalDateTime createdAt;
}
