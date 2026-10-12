package org.bank.balanceservice.repository;

import org.bank.balanceservice.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {

    Optional<BalanceEntity> findBalanceByAccountId(Long accountId);

    @Modifying
    @Query("""
        delete from BalanceEntity b
        where b.accountId in :accountIds
    """)
    void deleteByAccountIds(@Param("accountIds") List<Long> accountIds);
}
