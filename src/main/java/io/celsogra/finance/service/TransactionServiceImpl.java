package io.celsogra.finance.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    
    private static double MINIMUM_TRANSACTION = 0.000001d;
        
    @Async
    @Override
    public void createTransaction(TransactionDTO dto) {
        try {
            Transaction transaction = builder.build(dto);
            
            if (transaction == null) {
                return;
            }
            
            try {
                if ((processTransaction(transaction) != true)) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            
            blockchain.addTransaction(transaction);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }
    
    private boolean processTransaction(Transaction transaction) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

        if (transaction.verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput input : transaction.getInputs()) {
            input.setUTXO( utxoBase.get(input.getTransactionOutputId()) );
        }

        if (transaction.getInputsValue() < MINIMUM_TRANSACTION) {
            System.out.println("#Transaction Inputs to small: " + transaction.getInputsValue());
            return false;
        }

        transaction.processTransaction();
        
        // add outputs to Unspent list
        for (TransactionOutput o : transaction.getOutputs()) {
            utxoBase.put(o.getId(), o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : transaction.getInputs()) {
            if (i.getUTXO() == null)
                continue; // if Transaction can't be found skip it
            utxoBase.remove(i.getUTXO().getId());
        }

        return true;
    }

}
