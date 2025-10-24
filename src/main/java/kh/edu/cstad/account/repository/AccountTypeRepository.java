package kh.edu.cstad.account.repository;

import kh.edu.cstad.account.domain.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends
        JpaRepository<AccountType, String> {
}
