package com.example.Wallet.services.saga.Steps;

import org.springframework.stereotype.Service;

import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.TransactionStatus;
import com.example.Wallet.repositories.TransactionRepository;
import com.example.Wallet.services.saga.SagaContext;
import com.example.Wallet.services.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionStatusUpdateStep implements SagaStep{

    private final TransactionRepository transactionRepository;
    
    @Override
    public boolean execute(SagaContext context) {

        //Step 1 : Get transaction id and status from context

        Long transactionId = context.getLong("transactionId");

        log.info("Executing TransactionStatusUpdateStep for transactionId: {}", transactionId);

        // Step 2 : Fetch transaction from the DB with a lock

        Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        context.put("originalTransactionStatus", transaction.getStatus());

        // Step 3 : Update the transaction status to SUCCESS

        transaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

        log.info("Updated transactionId: {} status to SUCCESS", transactionId);

        context.put("transactionStatusAfterUpdate", transaction.getStatus());

        log.info("TransactionStatusUpdateStep executed successfully for transactionId: {}", transactionId);
        
        return true;
        
    }

    @Override
    public boolean compensate(SagaContext context) {

        //Step 1 : Get transaction id and status from context

        Long transactionId = context.getLong("transactionId");

        TransactionStatus originalTransactionStatus = TransactionStatus.valueOf(context.getString("originalTransactionStatus"));

        log.info("Executing TransactionStatusUpdateStep for transactionId: {}", transactionId);

        // Step 2 : Fetch transaction from the DB 

        Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        // Step 3 : Revert the transaction status to original status

        transaction.setStatus(originalTransactionStatus);

        transactionRepository.save(transaction);

        log.info("Reverted transactionId: {} status to original status", transactionId);

        return true;
        
    }

    @Override
    public String getStepName() {
        return "TransactionStatusUpdateStep";
    }


    
}
