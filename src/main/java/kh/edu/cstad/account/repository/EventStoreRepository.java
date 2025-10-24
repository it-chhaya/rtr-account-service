package kh.edu.cstad.account.repository;

import kh.edu.cstad.account.domain.EventStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStoreRepository
extends JpaRepository<EventStore, Long> {

    long countByAggregateId(String aggregateId);

}
