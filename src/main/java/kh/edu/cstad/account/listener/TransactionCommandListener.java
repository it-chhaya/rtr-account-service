package kh.edu.cstad.account.listener;

import kh.edu.cstad.account.domain.EventStore;
import kh.edu.cstad.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCommandListener {

    private final AccountService accountService;

    @KafkaListener(topics = "banking.transaction.deposited",
        groupId = "${spring.application.name}")
    public void handleTransactionCommand(EventStore eventStore) {
        log.info("Received transaction command: {}", eventStore.getAggregateType());
        log.info("Received transaction command: {}", eventStore.getEventType());
        log.info("Received transaction command: {}", eventStore.getEventData());

        String eventType = eventStore.getEventType();

        switch (eventType) {
            case "TransactionDeposited" -> handleDepositAccount(eventStore);
            case "TransactionWithdrawn" -> handleWithdrawalAccount(eventStore);
            default -> throw new IllegalStateException("Unknown event type: " + eventType);
        }
    }

    private void handleWithdrawalAccount(EventStore eventStore) {
        log.info("handleWithdrawalAccount");
    }

    private void handleDepositAccount(EventStore eventStore) {
        Map<String, Object> eventData = eventStore.getEventData();
        Long accountId = Long.parseLong(eventData.get("accountId").toString());
        BigDecimal amount = BigDecimal.valueOf((Integer) eventData.get("amount"));
        accountService.creditBalance(accountId, amount);
    }

}
