package kh.edu.cstad.account.saga;

import kh.edu.cstad.account.aggregate.AccountAggregate;
import kh.edu.cstad.account.event.DepositCompletedEvent;
import kh.edu.cstad.account.event.DepositFailedEvent;
import kh.edu.cstad.account.event.DepositRequestedEvent;
import kh.edu.cstad.account.service.AccountProjectionService;
import kh.edu.cstad.account.service.AccountService;
import kh.edu.cstad.account.service.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountSagaOrchestrator {

    private final EventStoreService eventStoreService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountProjectionService accountProjectionService;

    @Transactional
    public void handleDepositRequest(DepositRequestedEvent event) {

        log.info("Processing deposit request: {}", event);

        // Load aggregate from event store
        AccountAggregate aggregate = eventStoreService.loadAggregate(event.getToAccountNumber());

        if (aggregate == null) {
            throw new RuntimeException("Account not found: " + event.getToAccountNumber());
        }

        // Execute command
        aggregate.creditBalance(event.getAmount(), event.getTransactionId());

        // Save new events
        eventStoreService.saveEvents(aggregate, event.getTransactionId());

        // Update read model projection
        for (Object domainEvent : aggregate.getUncommittedEvents()) {
            accountProjectionService.onProjection(domainEvent);
        }

        //Publish success event
        DepositCompletedEvent completedEvent = DepositCompletedEvent.builder()
                .transactionId(event.getTransactionId())
                .toAccountNumber(event.getToAccountNumber())
                .amount(event.getAmount())
                .newBalance(aggregate.getBalance())
                .build();

        if (completedEvent.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new RuntimeException("Account service failed to handle transaction");
        }

        kafkaTemplate.send("deposit-completed", completedEvent);
        log.info("Deposit completed successfully: {}", completedEvent);

        aggregate.markEventsAsCommitted();
    }

}
