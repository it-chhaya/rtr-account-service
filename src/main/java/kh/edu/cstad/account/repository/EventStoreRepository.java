package kh.edu.cstad.account.repository;

import kh.edu.cstad.account.domain.EventStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventStoreRepository extends JpaRepository<EventStore, Long> {

    long countByAggregateId(String aggregateId);

    List<EventStore> findByAggregateIdOrderByVersionAsc(String accountNumber);
}
