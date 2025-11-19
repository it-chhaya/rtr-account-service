package kh.edu.cstad.account.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.cstad.account.domain.EventStore;
import kh.edu.cstad.account.event.DepositFailedEvent;
import kh.edu.cstad.account.event.DepositRequestedEvent;
import kh.edu.cstad.account.saga.AccountSagaOrchestrator;
import kh.edu.cstad.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCommandListener {

    private final AccountSagaOrchestrator accountSaga;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "banking.transaction.deposited",
        groupId = "${spring.application.name}")
    public void handleDepositRequestedEvent(String event) {

        System.out.println("Received deposited event: " + event);

        DepositRequestedEvent depositRequestedEvent = null;
        try {
            depositRequestedEvent = objectMapper.readValue(event, DepositRequestedEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        try {
            accountSaga.handleDepositRequest(depositRequestedEvent);
        } catch (RuntimeException e) {
            log.error("Deposit failed: {}", e.getMessage());

            DepositFailedEvent failedEvent = DepositFailedEvent.builder()
                    .transactionId(depositRequestedEvent.getTransactionId())
                    .accountNumber(depositRequestedEvent.getToAccountNumber())
                    .amount(depositRequestedEvent.getAmount())
                    .reason(e.getMessage())
                    .build();

            kafkaTemplate.send("deposit-failed", failedEvent);
        }
    }

}
