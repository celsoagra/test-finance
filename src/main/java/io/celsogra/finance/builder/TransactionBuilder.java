package io.celsogra.finance.builder;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.celsogra.finance.base.Blockchain;
import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;
import io.celsogra.finance.exception.NotEnoughMoneyException;
import io.celsogra.finance.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransactionBuilder {
    
    @Autowired
    private CoinBase coinBase;
    
    @Autowired
    private Blockchain blockchain;

    public Transaction build(TransactionDTO dto) {
        double totalToSpend = dto.getValue() + coinBase.getTaxToBePayed();
        PublicKey sender = dto.getSenderAsPubKey();
        ArrayList<TransactionInput> inputs = getOutuputsAndReturnInputs(sender, totalToSpend);
        Transaction transaction = Transaction.create(dto.getSenderAsPubKey(), dto.getReceiverAsPubKey(), dto.getValue(), inputs);
        transaction.setSignature(Base64.getDecoder().decode(dto.getSignature()));
        return transaction;
    }
    
    public Transaction buildGenesis() {
        Transaction genesis = Transaction.create(coinBase.getPublicKey(), coinBase.getPublicKey(), coinBase.getCoinsFromGenesis(), null);
        genesis.generateSignature(coinBase.getPrivateKey());
        genesis.setTransactionId("0");
        return genesis;
    }
    
    public Transaction buildFaucet(String wallet) {
        PublicKey sender = coinBase.getPublicKey();
        PublicKey reciepient = CryptUtil.getKeyFromString(wallet);
        double totalToSpend = coinBase.getCoinsFromFaucet();
        ArrayList<TransactionInput> inputs = getOutuputsAndReturnInputs(sender, totalToSpend);
        Transaction transaction = Transaction.create(sender, reciepient, totalToSpend, inputs);
        transaction.generateSignature(coinBase.getPrivateKey());
        return transaction;
    }
    
    private ArrayList<TransactionInput> getOutuputsAndReturnInputs(PublicKey sender, double totalToSpend) {
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        Set<Entry<String, TransactionOutput>> entries = blockchain.getLastBlock().getTransactionOutputEntries();
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<String, TransactionOutput> item : entries) {
            TransactionOutput utxo = item.getValue();
            
            if( utxo.belongsTo(sender) ) {
                total = total.add( utxo.getValueAsBigDecimal() );
                inputs.add( TransactionInput.builder().transactionOutputId(utxo.getId()).utxo(utxo).build() );
                if (total.doubleValue() > totalToSpend) {
                    break;
                }
            }
        }
        
        if (total.doubleValue() < totalToSpend) {
            throw new NotEnoughMoneyException();
        }
        
        return inputs;
    }
}
