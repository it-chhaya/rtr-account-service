package kh.edu.cstad.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event_stores")
public class EventStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID eventId;
    private String version;
    private String eventType;
    private String aggregateId;
    private String aggregateType;
    private LocalDateTime timestamp = LocalDateTime.now();

    private String eventData; // JSON String
}
