package io.celsogra.finance.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.celsogra.finance.base.BlockchainBase;
import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionBuilder builder;

    @Autowired
    private BlockchainBase blockchain;

    @Autowired
    private UTXOBase utxoBase;

    @Override
    public void createTransaction(TransactionDTO dto) {
        try {
            Transaction transaction = builder.build(dto);
            this.createTransaction(transaction);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void createTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        
        try {
            
            if (!transaction.processTransaction(utxoBase.map())) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        try {
            blockchain.addTransaction(transaction);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

}
