package kh.edu.cstad.account.aggregate;

import kh.edu.cstad.account.event.AccountCreditedEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class AccountAggregate {

    private String accountNumber;
    private BigDecimal balance;
    private Long version;

    private List<Object> uncommittedEvents = new ArrayList<>();

    public AccountAggregate(String accountNumber) {
        this.accountNumber = accountNumber;
        this.version = 0L;
    }

    public void creditBalance(BigDecimal amount, String transactionId) {

        validateAmount(amount);

        // Add balance
        BigDecimal newBalance = this.balance.add(amount);

        AccountCreditedEvent accountCreditedEvent = new AccountCreditedEvent();
        accountCreditedEvent.setAccountNumber(accountNumber);
        accountCreditedEvent.setAmount(amount);
        accountCreditedEvent.setBalance(newBalance);
        accountCreditedEvent.setTxnId(transactionId);

        applyEvent(accountCreditedEvent);

        uncommittedEvents.add(accountCreditedEvent);
        log.info("Deposited {} to account {}. New balance: {}", amount, accountNumber, newBalance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount cannot be negative");
        }
    }

    public void applyEvent(Object event) {
        if (event instanceof AccountCreditedEvent) {
            apply((AccountCreditedEvent) event);
        }
        this.version++;
    }

    private void apply(AccountCreditedEvent event) {
        this.balance = event.getBalance();
    }

    public List<Object> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    // Rebuild aggregate from event history
    // Account deposited x3
    // ordered event, select from database
    public static AccountAggregate rebuild(String accountNumber, List<Object> events) {
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        for (Object event : events) {
            aggregate.applyEvent(event);
        }
        return aggregate;
    }
}
