package io.celsogra.finance.service;

import java.math.BigDecimal;
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
import io.celsogra.finance.exception.DecimalsAllowedTransactionException;
import io.celsogra.finance.exception.InvalidSignatureException;
import io.celsogra.finance.exception.MinimumTransactionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        if (dto.getValue() < minimumTransaction) {
            throw new MinimumTransactionException();
        }
        
        String[] splitter = Double.valueOf(dto.getValue()).toString().split("\\.");
        if (splitter.length > 1 && splitter[1].length() > numberOfDecimals) {
            throw new DecimalsAllowedTransactionException();
        }
        
        Transaction transaction = builder.build(dto);
        this.addToBlock(transaction);
    }
    
    public void addToBlock(Transaction transaction) {
        if (transaction == null) return;
        
        Map<String,TransactionOutput> utxos = processAndGetUtxos(transaction);
        if(Objects.isNull(utxos)) {
            return;
        }
        
        blockchain.addTransaction(transaction, utxos);
    }
    
    private Map<String,TransactionOutput> processAndGetUtxos(Transaction transaction) {
        Map<String,TransactionOutput> utxos = blockchain.cloneUTXOs();
        
        if (transaction.verifiySignature() == false) {
            throw new InvalidSignatureException();
        }
        
        double inputValue = transaction.getInputsValue();
        if (inputValue < minimumTransaction) {
            log.error("Transaction Inputs too small {}", inputValue);
            return null;
        }

        String[] splitter = Double.valueOf(inputValue).toString().split("\\.");
        if (splitter.length > 1 && splitter[1].length() > numberOfDecimals) {
            log.error("Transaction Inputs must have decimals: {}", numberOfDecimals);
            return null;
        }
        
        BigDecimal leftOver = BigDecimal.valueOf(inputValue).subtract( transaction.getValueAsBigDecimal() );
        transaction.setTransactionId(transaction.calulateHash());
                
        // coins are not created in faucet. They're created from each transaction
        if (!transaction.getSender().equals(coinBase.getPublicKey())) {
            
            leftOver = leftOver.subtract( coinBase.getTaxToBePayedAsBigDecimal() );
            
            TransactionOutput mineNewCoinsToCoinbase = new TransactionOutput(coinBase.getPublicKey(), coinBase.getCoinsFromFaucet(), transaction.getTransactionId());
            utxos.put(mineNewCoinsToCoinbase.getId(), mineNewCoinsToCoinbase);
            
            TransactionOutput taxToCoinbase = new TransactionOutput(coinBase.getPublicKey(), coinBase.getTaxToBePayed(), transaction.getTransactionId());
            utxos.put(taxToCoinbase.getId(), taxToCoinbase);
        }

        TransactionOutput sendValueToRecipient = new TransactionOutput(transaction.getReciepient(), transaction.getValue(), transaction.getTransactionId());
        utxos.put(sendValueToRecipient.getId(), sendValueToRecipient);
        
        TransactionOutput sendLeftOverToSender = new TransactionOutput(transaction.getSender(), leftOver.doubleValue(), transaction.getTransactionId());
        utxos.put(sendLeftOverToSender.getId(), sendLeftOverToSender);

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : transaction.getInputs()) {
            if (i.getUtxo() == null) continue;
            utxos.remove(i.getUtxo().getId());
        }
        
        return utxos;
    }

}
