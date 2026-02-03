package com.example.Wallet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Wallet.entities.SagaStep;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND s.status = 'COMPLETED'")
    List<SagaStep> findCompletedStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND s.status = 'PENDING'")
    List<SagaStep> findPendingStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND (s.status = 'COMPLETED' OR s.status = 'COMPENSATED')")
    List<SagaStep> findCompletedOrCompensatedStepsBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);
    
}
