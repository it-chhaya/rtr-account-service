package kh.edu.cstad.account.listener;

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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "banking.transaction.deposited",
        groupId = "${spring.application.name}")
    public void handleDepositRequestedEvent(Map<String, Object> event) {

        System.out.println("Received deposited event: " + event);

        DepositRequestedEvent depositRequestedEvent = DepositRequestedEvent.builder()
                .accountNumber(event.get("accountNumber").toString())
                .amount(BigDecimal.valueOf((int)event.get("amount")))
                .transactionId(event.get("txnId").toString())
                .build();

        accountSaga.handleDepositRequest(depositRequestedEvent);
    }

}
