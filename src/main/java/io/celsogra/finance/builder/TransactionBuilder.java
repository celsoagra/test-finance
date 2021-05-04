package io.celsogra.finance.builder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;

@Component
public class TransactionBuilder {
    
    @Autowired
    private CoinBase coinBase;
    
    @Autowired
    private UTXOBase utxoBase;

    public Transaction build(TransactionDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        
        double total = 0d;
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        
        for (Map.Entry<String, TransactionOutput> item : utxoBase.entries()) {
            TransactionOutput utxo = item.getValue();
            if( !utxo.isMine(dto.getSenderAsPubKey()) ) {
                continue;
            }
            
            total += utxo.getValue();
            inputs.add(new TransactionInput(utxo.getId()));
            if (total > dto.getValue()) {
                break;
            }
        }

        Transaction transaction = Transaction.create(dto.getSenderAsPubKey(), dto.getReceiverAsPubKey(), dto.getValue(), inputs);
        transaction.setSignature(Base64.getDecoder().decode(dto.getSignature()));
        return transaction;
    }
    
    public Transaction buildGenesis() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        Transaction genesis = Transaction.create(coinBase.getPublicKey(), coinBase.getPublicKey(), coinBase.getCoinsFromGenesis(), null);
        genesis.generateSignature(coinBase.getPrivateKey());
        genesis.setTransactionId("0");
        return genesis;
        
    }
    
    
}
