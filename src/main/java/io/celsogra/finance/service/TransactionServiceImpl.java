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
import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static double MINIMUM_TRANSACTION = 0.000001d;
    
    @Autowired
    private TransactionBuilder builder;

    @Autowired
    private BlockchainBase blockchain;
    
    @Autowired
    private CoinBase coinBase;

    @Autowired
    private UTXOBase utxoBase;

    @Override
    public void addToBlock(TransactionDTO dto) {
        try {
            Transaction transaction = builder.build(dto);
            this.addToBlock(transaction);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException();
        }
    }
    
    public void addToBlock(Transaction transaction) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchProviderException, SignatureException {
        if (transaction == null) return;
        
        if(!process(transaction)) {
            return;
        }
        
        blockchain.addTransaction(transaction);
    }
    
    private boolean process(Transaction transaction) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, UnsupportedEncodingException {
        if (transaction.verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        
        if (transaction.getInputsValue() < MINIMUM_TRANSACTION) {
            System.out.println("#Transaction Inputs to small: " + transaction.getInputsValue());
            return false;
        }
        
        double leftOver = transaction.getInputsValue() - transaction.getValue();
        transaction.setTransactionId(transaction.calulateHash());
        
        TransactionOutput sendValueToRecipient = new TransactionOutput(transaction.getReciepient(), transaction.getValue(), transaction.getTransactionId());
        utxoBase.put(sendValueToRecipient.getId(), sendValueToRecipient);
        
        TransactionOutput sendLeftOverToSender = new TransactionOutput(transaction.getSender(), leftOver, transaction.getTransactionId());
        utxoBase.put(sendLeftOverToSender.getId(), sendLeftOverToSender);
        
        if (!transaction.getSender().equals(coinBase.getPublicKey())) { // don't create coins on faucet
            // Create new coins from each transaction
            TransactionOutput mineNewCoinsToCoinbase = new TransactionOutput(coinBase.getPublicKey(), coinBase.getCoinsFromFaucet(), transaction.getTransactionId());
            utxoBase.put(mineNewCoinsToCoinbase.getId(), mineNewCoinsToCoinbase);           
        }

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : transaction.getInputs()) {
            if (i.getUtxo() == null) continue;
            utxoBase.remove(i.getUtxo().getId());
        }
        
        return true;
    }

}
