package kh.edu.cstad.account.event;

import kh.edu.cstad.account.domain.CurrencyEnum;
import kh.edu.cstad.account.domain.TransactionStatus;
import kh.edu.cstad.account.domain.TypeEnum;
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
    private String transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private TypeEnum typeCode;
    private BigDecimal amount;
    private String remark;
    private CurrencyEnum currency;
    private TransactionStatus status;
}
