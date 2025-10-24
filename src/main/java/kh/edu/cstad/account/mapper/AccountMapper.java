package kh.edu.cstad.account.mapper;

import kh.edu.cstad.account.domain.Account;
import kh.edu.cstad.account.dto.AccountBalanceResponse;
import kh.edu.cstad.account.dto.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "accountType.typeCode", target = "accountTypeCode")
    @Mapping(source = "branch.name", target = "branch")
    AccountResponse toAccountResponse(Account account);

    AccountBalanceResponse toAccountBalanceResponse(Account account);

}
