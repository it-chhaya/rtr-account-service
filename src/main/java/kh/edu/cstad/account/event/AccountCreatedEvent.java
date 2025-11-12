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
    private String accountNumber;
    private Long customerId;
    private BigDecimal initBalance;
    private String accountTypeCode;
    private Long branchId;
    private LocalDateTime createdAt;
}
