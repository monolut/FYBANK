package org.bank.accountservice.repository;

import org.bank.accountservice.entity.AccountEntity;
import org.bank.common.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findAllByUserId(Long userId);

    Optional<AccountEntity> findByIban(String iban);

    @Query("""
        SELECT a.currency
        FROM AccountEntity a
        WHERE a.id = :id
    """)
    Optional<CurrencyCode> findCurrencyById(@Param("id") Long id);
}
