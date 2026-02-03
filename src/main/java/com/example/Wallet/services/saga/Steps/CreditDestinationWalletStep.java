package com.example.Wallet.services.saga.Steps;


import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.Wallet.entities.Wallet;
import com.example.Wallet.repositories.WalletRepository;
import com.example.Wallet.services.saga.SagaContext;
import com.example.Wallet.services.saga.SagaStep;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStep{

    private final WalletRepository walletRepository;

    
    @Override
    @Transactional
    public boolean execute(SagaContext context) {

        //Step 1 : Get destination wallet id from context

        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing CreditDestinationWalletStep for walletId: {} with amount: {}", toWalletId, amount);

        //Step 2 : Fetch destination wallet from the DB with a lock

        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
        .orElseThrow(() -> new RuntimeException("Destination Wallet not found with id: " + toWalletId));
        
        log.info("Fetched destination wallet: {} with balance: {}", wallet.getId(), wallet.getBalance());

        context.put("originalToWalletBalance", wallet.getBalance());
        //Step 3 : Credit the destination wallet
        wallet.credit(amount);
        walletRepository.save(wallet);

        log.info("Credited amount: {} to walletId: {}. New balance: {}", amount, wallet.getId(), wallet.getBalance());

        context.put("toWalletbalanceAfterCredit", wallet.getBalance());

        //Step 4 : Update the context with the changes(Already done above)

        log.info("CreditDestinationWalletStep executed successfully for walletId: {}", toWalletId);
        return true;

    }
    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        //Step 1 : Get destination wallet id from context

        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Compensating Credit ff distination wallet for walletId: {} with amount: {}", toWalletId, amount);

        //Step 2 : Fetch destination wallet from the DB with a lock

        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
        .orElseThrow(() -> new RuntimeException("Destination Wallet not found with id: " + toWalletId));
        
        log.info("Fetched destination wallet: {} with balance: {}", wallet.getId(), wallet.getBalance());

      
        //Step 3 : Credit the destination wallet
        wallet.debit(amount);
        walletRepository.save(wallet);

        log.info("Debited amount: {} to walletId: {}. New balance: {}", amount, wallet.getId(), wallet.getBalance());

        context.put("toWalletbalanceAfterCreditCompensate", wallet.getBalance());

        //Step 4 : Update the context with the changes(Already done above)

        log.info("Credit compensation of destination wallet step executed successfully for walletId: {}", toWalletId);
        return true;

    }

    @Override
    public String getStepName() {
        return "CreditDestinationWalletStep";
    }

    
}
