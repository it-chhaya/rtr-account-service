package kh.edu.cstad.account.service;

import kh.edu.cstad.account.domain.Account;
import kh.edu.cstad.account.domain.AccountType;
import kh.edu.cstad.account.domain.Branch;
import kh.edu.cstad.account.event.AccountCreatedEvent;
import kh.edu.cstad.account.event.AccountCreditedEvent;
import kh.edu.cstad.account.event.MoneyReservedEvent;
import kh.edu.cstad.account.repository.AccountRepository;
import kh.edu.cstad.account.repository.AccountTypeRepository;
import kh.edu.cstad.account.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountProjectionService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final BranchRepository branchRepository;

    // Define handler
    // 1. handle when account created
    // 1.1. save record into account table
    @Transactional
    public void onProjection(Object event) {
        if (event instanceof AccountCreatedEvent) {
            handleAccountCreated((AccountCreatedEvent) event);
        } else if (event instanceof AccountCreditedEvent) {
            handleAccountCredited((AccountCreditedEvent) event);
        } else if (event instanceof MoneyReservedEvent moneyReservedEvent) {
            handleMoneyReserved(moneyReservedEvent);
        }
    }


    private void handleMoneyReserved(MoneyReservedEvent moneyReservedEvent) {
        Account account = accountRepository.findByAccountNumber(moneyReservedEvent.getAccountNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + moneyReservedEvent.getAccountNumber()));

        account.setBalance(moneyReservedEvent.getBalanceAfter());
        account.setVersion(account.getVersion() + 1);
        account.setUpdatedAt(LocalDateTime.ofInstant(moneyReservedEvent.getTimestamp(), ZoneId.systemDefault()));
        account.setUpdatedBy("admin");

        accountRepository.save(account);
        log.info("Reserved money has been saved into read database: {}", moneyReservedEvent.getAccountNumber());
    }


    private void handleAccountCreated(AccountCreatedEvent event) {

        // Account number
        if (accountRepository.existsByAccountNumber(event.getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account number already exists");
        }

        // Account type
        AccountType accountType = accountTypeRepository
                .findById(event.getAccountTypeCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account type not found"
                ));

        // Branch
        Branch branch = branchRepository
                .findById(event.getBranchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"
                ));

        Account account = new Account();
        account.setCustomerId(event.getCustomerId());
        account.setAccountNumber(event.getAccountNumber());
        account.setAccountType(accountType);
        account.setBranch(branch);
        account.setBalance(event.getInitBalance());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setCreatedBy("rtr");
        account.setUpdatedBy("rtr");
        account.setStatus(true);
        account.setVersion(1L);

        accountRepository.save(account);

        log.info("Account projection inserted: {}", account.getAccountNumber());
    }

    private void handleAccountCredited(AccountCreditedEvent event) {
        Account account = accountRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + event.getAccountNumber()));

        account.setBalance(event.getBalance());
        account.setVersion(account.getVersion() + 1);
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("rtr");

        accountRepository.save(account);
        log.info("Account {} projection credited {}", account.getAccountNumber(),
                event.getAmount());
    }

}
