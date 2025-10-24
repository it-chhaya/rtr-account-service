package kh.edu.cstad.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.processing.SQL;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event_stores")
public class EventStore {

    @Id
    private String id;
    private UUID eventId;
    private String version;
    private String eventType;
    private String aggregateId;
    private String aggregateType;
    private Instant timestamp = Instant.now();

    @JdbcTypeCode(value = SqlTypes.JSON)
    private Map<String, Object> eventData; // JSON String
}
