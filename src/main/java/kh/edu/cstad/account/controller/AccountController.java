package kh.edu.cstad.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import kh.edu.cstad.account.dto.AccountBalanceResponse;
import kh.edu.cstad.account.dto.AccountResponse;
import kh.edu.cstad.account.dto.CreateAccountRequest;
import kh.edu.cstad.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest createAccountRequest
    ) throws JsonProcessingException {
        return accountService.createAccount(createAccountRequest);
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
        return accountService.getAllAccounts(page, limit);
    }


    @GetMapping("/{accountId}/balance")
    public AccountBalanceResponse getBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId);
    }

}
