package kh.edu.cstad.account.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.cstad.account.domain.Account;
import kh.edu.cstad.account.domain.AccountType;
import kh.edu.cstad.account.domain.Branch;
import kh.edu.cstad.account.domain.EventStore;
import kh.edu.cstad.account.dto.AccountBalanceResponse;
import kh.edu.cstad.account.dto.AccountResponse;
import kh.edu.cstad.account.dto.CreateAccountRequest;
import kh.edu.cstad.account.event.AccountCreatedEvent;
import kh.edu.cstad.account.event.AccountCreditedEvent;
import kh.edu.cstad.account.mapper.AccountMapper;
import kh.edu.cstad.account.repository.AccountRepository;
import kh.edu.cstad.account.repository.AccountTypeRepository;
import kh.edu.cstad.account.repository.BranchRepository;
import kh.edu.cstad.account.repository.EventStoreRepository;
import kh.edu.cstad.account.service.AccountService;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final BranchRepository branchRepository;

    private final AccountMapper accountMapper;

    private final EventStoreRepository eventStoreRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public AccountResponse createAccount(CreateAccountRequest createAccountRequest) throws JsonProcessingException {

        // Start validate
        // Init balance
        if (createAccountRequest.initBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Initial balance cannot be negative");
        }

        // Account number
        if (accountRepository.existsByAccountNumber(createAccountRequest.accountNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account number already exists");
        }

        // Account type
        AccountType accountType = accountTypeRepository
                .findById(createAccountRequest.accountTypeCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account type not found"
                ));

        // Branch
        Branch branch = branchRepository
                .findById(createAccountRequest.branchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Branch not found"
                ));

        Account account = new Account();
        account.setCustomerId(createAccountRequest.customerId());
        account.setAccountNumber(createAccountRequest.accountNumber());
        account.setAccountType(accountType);
        account.setBranch(branch);
        account.setBalance(createAccountRequest.initBalance());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setCreatedBy("rtr");
        account.setUpdatedBy("rtr");
        account.setStatus(true);
        account = accountRepository.save(account);

        // Create and publish event
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
        accountCreatedEvent.setId(account.getId());
        accountCreatedEvent.setAccountNumber(account.getAccountNumber());
        accountCreatedEvent.setCustomerId(account.getCustomerId());
        accountCreatedEvent.setAccountTypeCode(account.getAccountType().getTypeCode());
        accountCreatedEvent.setBranch(account.getBranch().getName());
        accountCreatedEvent.setBalance(account.getBalance());
        accountCreatedEvent.setCreatedAt(account.getCreatedAt());

        EventStore eventStore = new EventStore();
        eventStore.setId(UUID.randomUUID().toString());
        eventStore.setEventId(UUID.randomUUID());
        eventStore.setEventType("ACCOUNT_CREATED_EVENT");
        eventStore.setAggregateId(account.getId().toString());
        eventStore.setAggregateType(Account.class.getSimpleName());
        eventStore.setTimestamp(account.getCreatedAt().toInstant(ZoneOffset.UTC));
        eventStore.setEventData(objectMapper.convertValue(accountCreatedEvent, new TypeReference<Map<String, Object>>() {}));
        eventStore.setVersion(String.valueOf(eventStoreRepository.countByAggregateId(account.getId().toString()) + 1));

        eventStoreRepository.save(eventStore);
        kafkaTemplate.send("account-events", eventStore);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        String formatted = account.getCreatedAt().format(dtf);

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountTypeCode(account.getAccountType().getTypeCode())
                .branch(account.getBranch().getName())
                .balance(account.getBalance())
                .createdAt(formatted)
                .status(accountType.getStatus())
                .customerId(account.getCustomerId())
                .build();
    }


    @Transactional
    @Override
    public AccountResponse creditBalance(Long accountId, BigDecimal amount, String txnId) {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount cannot be negative");
        }

        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy("rtr");
        account = accountRepository.save(account);

        AccountCreditedEvent accountCreditedEvent = new AccountCreditedEvent();
        accountCreditedEvent.setAccountNumber(account.getId().toString());
        accountCreditedEvent.setAmount(amount);
        accountCreditedEvent.setBalance(account.getBalance());
        accountCreditedEvent.setTxnId(txnId);

        EventStore eventStore = new EventStore();
        eventStore.setId(UUID.randomUUID().toString());
        eventStore.setEventId(UUID.randomUUID());
        eventStore.setEventType("AccountCredited");
        eventStore.setAggregateId(account.getId().toString());
        eventStore.setAggregateType(Account.class.getSimpleName());
        eventStore.setTimestamp(account.getCreatedAt().toInstant(ZoneOffset.UTC));
        eventStore.setEventData(objectMapper.convertValue(accountCreditedEvent, new TypeReference<Map<String, Object>>() {}));
        eventStore.setVersion(String.valueOf(eventStoreRepository.countByAggregateId(account.getId().toString()) + 1));

        try {
            eventStoreRepository.save(eventStore);
            kafkaTemplate.send("account-events", eventStore.getAggregateId(), eventStore);
        } catch (Exception e) {
            log.error("Credit balance failed", e);
            eventStore.setEventType("AccountCreditFailed");
            kafkaTemplate.send("account-events", eventStore.getAggregateId(), eventStore);
        }

        return accountMapper.toAccountResponse(account);
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
