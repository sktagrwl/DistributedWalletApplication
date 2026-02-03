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
public class DebitSourceWalletStep implements SagaStep{

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {

        //Step 1 : get Source wallet id and amount from context

        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing DebitSourceWalletStep for walletId: {} with amount: {}", fromWalletId, amount);

        //Step 2 : get source wallet from the DB with a lock

        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
        .orElseThrow(() -> new RuntimeException("Source Wallet not found with id: " + fromWalletId));

        log.info("Fetched source wallet: {} with balance: {}", wallet.getId(), wallet.getBalance());

        context.put("originalFromWalletBalance", wallet.getBalance());

        //Step 3 : Check if source wallet has enough balance to debit and if yes then Debit the source wallet by amount

        wallet.debit(amount);
        walletRepository.save(wallet);

        context.put("fromWalletbalanceAfterDebit", wallet.getBalance());
        
        log.info("Debited amount: {} from walletId: {}. New balance: {}", amount, wallet.getId(), wallet.getBalance());

        //Step 4 : Update the context with the changes

        log.info("DebitSourceWalletStep executed successfully for walletId: {}", fromWalletId);
        return true;

    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        //Step 1 : get Source wallet id and amount from context

        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing DebitSourceWalletStep for walletId: {} with amount: {}", fromWalletId, amount);

        //Step 2 : get source wallet from the DB with a lock

        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
        .orElseThrow(() -> new RuntimeException("Source Wallet not found with id: " + fromWalletId));

        log.info("Fetched source wallet: {} with balance: {}", wallet.getId(), wallet.getBalance());

        context.put("originalFromWalletBalance", wallet.getBalance());

        //Step 3 : Check if source wallet has enough balance to debit and if yes then Debit the source wallet by amount

        wallet.credit(amount);
        walletRepository.save(wallet);

        context.put("sourceWalletbalanceAfterCredit", wallet.getBalance());
       
        log.info("Credited amount: {} to walletId: {}. New balance: {}", amount, wallet.getId(), wallet.getBalance());


        //Step 4 : Update the context with the changes

        log.info("DebitSourceWalletStep executed successfully for walletId: {}", fromWalletId);
        return true;
        
    }

    @Override
    public String getStepName() {
        return "DebitSourceWalletStep";
    }

    


    
}
