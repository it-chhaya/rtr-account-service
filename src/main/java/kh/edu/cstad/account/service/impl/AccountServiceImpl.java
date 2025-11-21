package kh.edu.cstad.account.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.cstad.account.aggregate.AccountAggregate;
import kh.edu.cstad.account.command.ReserveMoneyCommand;
import kh.edu.cstad.account.domain.Account;
import kh.edu.cstad.account.domain.AccountType;
import kh.edu.cstad.account.domain.Branch;
import kh.edu.cstad.account.domain.EventStore;
import kh.edu.cstad.account.query.AccountBalanceResponse;
import kh.edu.cstad.account.query.AccountResponse;
import kh.edu.cstad.account.command.CreateAccountCommand;
import kh.edu.cstad.account.event.AccountCreatedEvent;
import kh.edu.cstad.account.event.AccountCreditedEvent;
import kh.edu.cstad.account.mapper.AccountMapper;
import kh.edu.cstad.account.repository.AccountRepository;
import kh.edu.cstad.account.repository.AccountTypeRepository;
import kh.edu.cstad.account.repository.BranchRepository;
import kh.edu.cstad.account.repository.EventStoreRepository;
import kh.edu.cstad.account.service.AccountProjectionService;
import kh.edu.cstad.account.service.AccountService;
import kh.edu.cstad.account.service.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final EventStoreService eventStoreService;
    private final AccountProjectionService accountProjectionService;


    @Transactional
    @Override
    public String handle(ReserveMoneyCommand command) {
        log.info("Received reserve money command: {}", command);

        // Load aggregate from event store
        AccountAggregate aggregate = eventStoreService.loadAggregate(command.accountNumber());

        if (aggregate == null) {
            throw new RuntimeException("Account not found: " + command.accountNumber());
        }

        aggregate.handle(command);

        // Persist event sourcing
        eventStoreService.saveEvents(aggregate, aggregate.getAccountNumber());

        // Update read model
        for (Object event : aggregate.getUncommittedEvents()) {
            accountProjectionService.onProjection(event);
        }

        if (command.amount().compareTo(BigDecimal.valueOf(5000)) > 0) {
            throw new RuntimeException("Amount greater than 5000");
        }

        return command.accountNumber();
    }

    // Call event store for processing event
    @Transactional
    @Override
    public String createAccount(CreateAccountCommand command) {
        log.info("Start creating account: {}", command);

        // Create aggregate
        AccountAggregate aggregate = new AccountAggregate(command.accountNumber());
        aggregate.createAccount(command);

        // Persist event sourcing
        eventStoreService.saveEvents(aggregate, aggregate.getAccountNumber());

        // Save account
        for (Object event : aggregate.getUncommittedEvents()) {
            accountProjectionService.onProjection(event);
        }

        return command.accountNumber();
    }


    @Override
    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account ID not found"));
        return accountMapper.toAccountResponse(account);
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        return accountMapper.toAccountResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts(int page, int limit) {
        Sort sortByCreatedAt = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(page, limit, sortByCreatedAt);
        return accountRepository.findAll(pageRequest)
                .stream()
                .map(accountMapper::toAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountBalanceResponse getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account ID not found"));

        return accountMapper.toAccountBalanceResponse(account);
    }
}
