package com.example.Wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Wallet.entities.SagaInstance;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

    
    
}
