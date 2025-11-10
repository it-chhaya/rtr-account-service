package kh.edu.cstad.account.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.cstad.account.aggregate.AccountAggregate;
import kh.edu.cstad.account.domain.Account;
import kh.edu.cstad.account.domain.EventStore;
import kh.edu.cstad.account.repository.EventStoreRepository;
import kh.edu.cstad.account.service.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventStoreServiceImpl implements EventStoreService {

    private final ObjectMapper objectMapper;
    private final EventStoreRepository eventStoreRepository;

    @Transactional
    @Override
    public void saveEvents(AccountAggregate aggregate) {

        List<Object> events = aggregate.getUncommittedEvents();

        for (Object event : events) {
            try {
                String eventData = objectMapper.writeValueAsString(event);

                EventStore eventStore = new EventStore();
                eventStore.setId(UUID.randomUUID().toString());
                eventStore.setEventId(UUID.randomUUID());
                eventStore.setEventType(event.getClass().getSimpleName());
                eventStore.setAggregateId(aggregate.getAccountNumber());
                eventStore.setAggregateType(Account.class.getSimpleName());
                eventStore.setTimestamp(Instant.now());
                eventStore.setEventData(eventData);
                eventStore.setVersion(aggregate.getVersion());


                eventStoreRepository.save(eventStore);
                log.info("Event stored: {} for aggregate {}", event.getClass().getSimpleName(),
                        aggregate.getAccountNumber());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize event: {}", e.getMessage());
                throw new RuntimeException("Event serialization failed", e);
            }
        }

        aggregate.markEventsAsCommitted();
    }

    @Override
    public AccountAggregate loadAggregate(String accountNumber) {

        List<EventStore> eventStores = eventStoreRepository
                .findByAggregateIdOrderByVersionAsc(accountNumber);

        if (eventStores.isEmpty()) {
            return null;
        }

        // Create array list empty
        List<Object> events = new ArrayList<>();

        for (EventStore eventStore : eventStores) {
            try {
                Class<?> eventClass = Class.forName(
                        "kh.edu.cstad.account.event." + eventStore.getEventType());
                Object event = objectMapper.readValue(eventStore.getEventData(), eventClass);
                events.add(event);
            } catch (Exception e) {
                log.error("Failed to deserialize event: {}", e.getMessage());
                throw new RuntimeException("Event deserialization failed", e);
            }
        }

        AccountAggregate aggregate = AccountAggregate.rebuild(accountNumber, events);
        log.info("Aggregate rebuilt from {} events for account {}", events.size(), accountNumber);

        return aggregate;
    }

    @Override
    public List<EventStore> getEventHistory(String accountNumber) {
        return eventStoreRepository.findByAggregateIdOrderByVersionAsc(accountNumber);
    }
}
