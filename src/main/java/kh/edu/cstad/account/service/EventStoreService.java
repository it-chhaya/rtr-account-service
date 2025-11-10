package kh.edu.cstad.account.service;

import kh.edu.cstad.account.aggregate.AccountAggregate;
import kh.edu.cstad.account.domain.EventStore;

import java.util.List;

public interface EventStoreService {

    void saveEvents(AccountAggregate aggregate);

    AccountAggregate loadAggregate(String accountNumber);

    List<EventStore> getEventHistory(String accountNumber);

}
