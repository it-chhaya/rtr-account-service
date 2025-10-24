package kh.edu.cstad.account.repository;

import kh.edu.cstad.account.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository
extends JpaRepository<Branch, Long> {
}
