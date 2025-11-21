package kh.edu.cstad.account.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.cstad.account.command.ReserveMoneyCommand;
import kh.edu.cstad.account.event.MoneyReserveFailedEvent;
import kh.edu.cstad.account.publisher.EventPublisher;
import kh.edu.cstad.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferSagaListener {

    private final AccountService accountService;

    private final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;

    @KafkaListener(topics = "reserve-money-command",
        groupId = "${spring.application.name}")
    public void handleReserveMoneyCommand(ConsumerRecord<String, String> record) {
        try {
            ReserveMoneyCommand command = objectMapper.readValue(
                    record.value(),
                    ReserveMoneyCommand.class
            );

            accountService.handle(command);

            // Publish succeed event
            eventPublisher.publishEvent("money-reserved-event",
                    command.transactionId(),
                    command);

        } catch (Exception e) {
            log.error("Reserve money failed: {}", e.getMessage());

            MoneyReserveFailedEvent failedEvent = MoneyReserveFailedEvent.builder()
                    .transactionId(record.key())
                    .reason(e.getMessage())
                    .build();

            // Publish failed event
            eventPublisher.publishEvent("money-reserve-failed-event",
                    failedEvent.getTransactionId(),
                    failedEvent);
        }
    }

}
