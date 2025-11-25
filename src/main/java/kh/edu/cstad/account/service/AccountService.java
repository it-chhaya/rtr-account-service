package kh.edu.cstad.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kh.edu.cstad.account.command.CancelReservationCommand;
import kh.edu.cstad.account.command.CreateAccountCommand;
import kh.edu.cstad.account.command.CreditMoneyCommand;
import kh.edu.cstad.account.command.ReserveMoneyCommand;
import kh.edu.cstad.account.query.AccountBalanceResponse;
import kh.edu.cstad.account.query.AccountResponse;

import java.util.List;

public interface AccountService {

    String handle(CancelReservationCommand command);


    String handle(CreditMoneyCommand command);


    String handle(ReserveMoneyCommand command);

    /**
     * Use to create a new account
     *
     * @param createAccountCommand information requested by client
     * @return {@link AccountResponse}
     */
    String createAccount(CreateAccountCommand createAccountCommand) throws JsonProcessingException;


    /**
     * Use to get account information by ID (primary key)
     *
     * @param id is a primary key of account
     * @return {@link AccountResponse}
     */
    AccountResponse getAccount(Long id);

    AccountResponse getAccountByNumber(String accountNumber);

    List<AccountResponse> getAllAccounts(int page, int limit);

    AccountBalanceResponse getBalance(Long accountId);
}
