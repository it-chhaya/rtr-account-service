package kh.edu.cstad.account.aggregate;

import kh.edu.cstad.account.command.CreateAccountCommand;
import kh.edu.cstad.account.event.AccountCreatedEvent;
import kh.edu.cstad.account.event.AccountCreditedEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class AccountAggregate {

    private String accountNumber;
    private BigDecimal balance;
    private Long customerId;
    private Long branchId;
    private String accountTypeCode;
    private Long version;

    private List<Object> uncommittedEvents = new ArrayList<>();

    public AccountAggregate(String accountNumber) {
        this.accountNumber = accountNumber;
        this.version = 0L;
    }

    public void createAccount(CreateAccountCommand command) {

        // Start validate
        // Init balance
        validateInitBalance(command.initBalance());

        // Validate account exists or not
        if (this.version > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account number already exists");
        }

        // Create and publish event
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
        accountCreatedEvent.setAccountNumber(command.accountNumber());
        accountCreatedEvent.setCustomerId(command.customerId());
        accountCreatedEvent.setAccountTypeCode(command.accountTypeCode());
        accountCreatedEvent.setBranchId(command.branchId());
        accountCreatedEvent.setInitBalance(command.initBalance());
        accountCreatedEvent.setCreatedAt(LocalDateTime.now());

        applyEvent(accountCreatedEvent);
        uncommittedEvents.add(accountCreatedEvent);
        log.info("Account created: {}", accountCreatedEvent);
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

    private void validateInitBalance(BigDecimal initBalance) {
        if (initBalance == null || initBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Init balance cannot be negative");
        }
    }

    public void applyEvent(Object event) {
        if (event instanceof AccountCreatedEvent) {
            apply((AccountCreatedEvent) event);
        } else if (event instanceof AccountCreditedEvent) {
            apply((AccountCreditedEvent) event);
        }
        this.version++;
    }

    private void apply(AccountCreatedEvent event) {
        this.accountNumber = event.getAccountNumber();
        this.balance = event.getInitBalance();
        this.customerId = event.getCustomerId();
        this.branchId = event.getBranchId();
        this.accountTypeCode = event.getAccountTypeCode();
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
