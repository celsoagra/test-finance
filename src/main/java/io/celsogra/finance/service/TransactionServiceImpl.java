package io.celsogra.finance.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.celsogra.finance.base.Blockchain;
import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Value("${app.default.coin.limit_decimals}")
    private double numberOfDecimals;
    
    @Value("${app.default.coin.minimum_transaction}")
    private double minimumTransaction;
    
    @Autowired
    private TransactionBuilder builder;

    @Autowired
    private Blockchain blockchain;
    
    @Autowired
    private CoinBase coinBase;

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
        
        Map<String,TransactionOutput> utxos = processAndGetUtxos(transaction);
        if(Objects.isNull(utxos)) {
            return;
        }
        
        blockchain.addTransaction(transaction, utxos);
    }
    
    private Map<String,TransactionOutput> processAndGetUtxos(Transaction transaction) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, UnsupportedEncodingException {
        Map<String,TransactionOutput> utxos = blockchain.cloneUTXOs();
        
        if (transaction.verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return null;
        }
        
        double inputValue = transaction.getInputsValue();
        if (inputValue < minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + transaction.getInputsValue());
            return null;
        }

        String[] splitter = Double.valueOf(inputValue).toString().split("\\.");
        if (splitter.length > 1 && splitter[1].length() > numberOfDecimals) {
            System.out.println("#Transaction Inputs must have decimals: " + numberOfDecimals);
            return null;
        }
        
        
        double leftOver = transaction.getInputsValue() - transaction.getValue();
        
        transaction.setTransactionId(transaction.calulateHash());
                
        // coins are not created in faucet. They're created from each transaction
        if (!transaction.getSender().equals(coinBase.getPublicKey())) {
            leftOver -= coinBase.getTaxToBePayed(); // sender must pay some tax to coinbase
            
            TransactionOutput mineNewCoinsToCoinbase = new TransactionOutput(coinBase.getPublicKey(), coinBase.getCoinsFromFaucet(), transaction.getTransactionId());
            utxos.put(mineNewCoinsToCoinbase.getId(), mineNewCoinsToCoinbase);
            
            TransactionOutput taxToCoinbase = new TransactionOutput(coinBase.getPublicKey(), coinBase.getTaxToBePayed(), transaction.getTransactionId());
            utxos.put(mineNewCoinsToCoinbase.getId(), mineNewCoinsToCoinbase);
        }

        TransactionOutput sendValueToRecipient = new TransactionOutput(transaction.getReciepient(), transaction.getValue(), transaction.getTransactionId());
        utxos.put(sendValueToRecipient.getId(), sendValueToRecipient);
        
        TransactionOutput sendLeftOverToSender = new TransactionOutput(transaction.getSender(), leftOver, transaction.getTransactionId());
        utxos.put(sendLeftOverToSender.getId(), sendLeftOverToSender);

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : transaction.getInputs()) {
            if (i.getUtxo() == null) continue;
            utxos.remove(i.getUtxo().getId());
        }
        
        return utxos;
    }

}
