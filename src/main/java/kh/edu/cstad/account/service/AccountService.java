package kh.edu.cstad.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kh.edu.cstad.account.dto.AccountBalanceResponse;
import kh.edu.cstad.account.dto.AccountResponse;
import kh.edu.cstad.account.dto.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    /**
     * Use to create a new account
     * @param createAccountRequest information requested by client
     * @return {@link AccountResponse}
     */
    AccountResponse createAccount(CreateAccountRequest createAccountRequest) throws JsonProcessingException;


    AccountResponse creditBalance(Long accountId, BigDecimal amount);


    /**
     * Use to get account information by ID (primary key)
     * @param id is a primary key of account
     * @return {@link AccountResponse}
     */
    AccountResponse getAccount(Long id);

    AccountResponse getAccountByNumber(String accountNumber);

    List<AccountResponse> getAllAccounts(int page, int limit);

    AccountBalanceResponse getBalance(Long accountId);
}
