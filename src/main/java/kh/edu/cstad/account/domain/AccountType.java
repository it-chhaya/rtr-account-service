package kh.edu.cstad.account.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account_types")
public class AccountType {

    @Id
    private String typeCode;

    private String description;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

}
