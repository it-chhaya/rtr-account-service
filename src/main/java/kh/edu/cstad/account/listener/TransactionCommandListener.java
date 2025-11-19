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
//
//        DepositRequestedEvent depositRequestedEvent = DepositRequestedEvent.builder()
//                .transactionId(event.get("transactionId").toString())
//                .accountNumber(event.get("toAccountNumber").toString())
//                .amount(BigDecimal.valueOf((int)event.get("amount")))
//                .remark(event.get("remark").toString())
//                .currency(event.get("currency").toString())
//                .build();

        accountSaga.handleDepositRequest(depositRequestedEvent);
    }

}
