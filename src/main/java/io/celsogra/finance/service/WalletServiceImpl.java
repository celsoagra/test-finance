package io.celsogra.finance.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.celsogra.finance.base.Blockchain;
import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionOutput;
import io.celsogra.finance.exception.NotEnoughMoneyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private Blockchain blockchain;
    
    @Autowired
    private CoinBase coinBase;
    
    @Autowired
    private TransactionBuilder transactionBuilder;

    public double balance(PublicKey wallet) {
        Set<Entry<String, TransactionOutput>> entries = blockchain.getLastBlock().getTransactionOutputEntries();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, TransactionOutput> item : entries) {
            TransactionOutput utxo = item.getValue();
            if (utxo.belongsTo(wallet)) {
                total = total.add( BigDecimal.valueOf(utxo.getValue()) );
            }
        }

        return total.doubleValue();
    }

    public double faucet(String wallet) {
        double value = coinBase.getCoinsFromFaucet();
        Transaction transaction = transactionBuilder.buildFaucet(wallet);
        transactionService.addToBlock(transaction);
        return value;
    }

    public void validateBalance(PublicKey wallet, double value) {
        if (value > this.balance(wallet)) {
            throw new NotEnoughMoneyException();
        }
    }

}
