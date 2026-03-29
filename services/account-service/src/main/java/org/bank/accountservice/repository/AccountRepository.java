package org.bank.accountservice.repository;

import org.bank.accountservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findAccountEntitiesByUserId(Long userId);

    @Query("""
            SELECT a FROM AccountEntity a
            JOIN FETCH a.balance
            WHERE a.id = :accountId
    """)
    Optional<AccountEntity> findByIdWithBalance(@Param("accountId") Long accountId);
}
