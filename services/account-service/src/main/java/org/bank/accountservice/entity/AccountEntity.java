package org.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bank.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String iban;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id", referencedColumnName = "id")
    private BalanceEntity balance;

    private Currency currency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;
}
