package kh.edu.cstad.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import kh.edu.cstad.account.query.AccountBalanceResponse;
import kh.edu.cstad.account.query.AccountResponse;
import kh.edu.cstad.account.command.CreateAccountCommand;
import kh.edu.cstad.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {


    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createAccount(
            @Valid @RequestBody CreateAccountCommand createAccountCommand
    ) throws JsonProcessingException {
        return accountService.createAccount(createAccountCommand);
    }


    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }


    @GetMapping("/number/{accountNumber}")
    public AccountResponse getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber);
    }


    @GetMapping
    public List<AccountResponse> getAllAccounts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit
    ) {
        log.debug("Start getAllAccounts");
        List<AccountResponse> accounts = accountService.getAllAccounts(page, limit);
        log.debug("End getAllAccounts");
        return accounts;
    }


    @GetMapping("/{accountId}/balance")
    public AccountBalanceResponse getBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId);
    }

}
