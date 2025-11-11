package kh.edu.cstad.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "type_code")
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false)
    private Long customerId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String createdBy;
    @Column(nullable = false)
    private String updatedBy;

    private Long version;
}
