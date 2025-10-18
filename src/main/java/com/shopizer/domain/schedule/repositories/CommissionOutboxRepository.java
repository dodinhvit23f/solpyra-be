package com.shopizer.domain.schedule.repositories;

import com.shopizer.constant.CommissionOutboxStatus;
import com.shopizer.entities.CommissionOutbox;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommissionOutboxRepository extends JpaRepository<CommissionOutbox, BigInteger> {

    @Query("""
        SELECT c FROM CommissionOutbox c
        WHERE c.status IN :status
        ORDER BY c.createdAt ASC
    """)
    List<CommissionOutbox> findByStatusOrderByCreatedAt(Collection<CommissionOutboxStatus> status);
}