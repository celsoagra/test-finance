package io.celsogra.finance.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionInput;
import io.celsogra.finance.entity.TransactionOutput;
import io.celsogra.finance.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private UTXOBase utxoBase;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private CoinBase coinBase;

    public double balance(PublicKey wallet) {
        double total = 0;

        for (Map.Entry<String, TransactionOutput> item : utxoBase.entries()) {
            TransactionOutput utxo = item.getValue();
            if (utxo.isMine(wallet)) {
                total += utxo.getValue();
            }
        }

        return total;
    }

    public double faucet(String wallet) {
        double total = 0d;
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        
        for (Map.Entry<String, TransactionOutput> item : utxoBase.entries()) {
            TransactionOutput utxo = item.getValue();
            
            if( !utxo.isMine(coinBase.getPublicKey()) ) {
                continue;
            }
            
            total += utxo.getValue();
            inputs.add(new TransactionInput(utxo.getId()));
            if (total > coinBase.getCoinsFromFaucet()) {
                break;
            }
        }
        
        log.info("faucet inputs: {}", inputs);
        
        try {
            Transaction transaction = Transaction.create(coinBase.getPublicKey(), CryptUtil.getKeyFromString(wallet), coinBase.getCoinsFromFaucet(), inputs);
            transaction.generateSignature(coinBase.getPrivateKey());
            transactionService.createTransaction(transaction);
            return coinBase.getCoinsFromFaucet();
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            return 0.0d;
        }
    }
    
    public void validateBalance(PublicKey wallet, double value) {
        if (value > this.balance(wallet)) {
            throw new RuntimeException("not enough money");
        }
    }

}
