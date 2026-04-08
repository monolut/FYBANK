package org.bank.accountservice.repository;

import org.bank.accountservice.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {

    Optional<BalanceEntity> findBalanceByAccountId(Long accountId);

    @Modifying
    @Query("delete from BalanceEntity b where b.account.userId = :userId")
    void deleteAllByUserId(Long userId);
}
