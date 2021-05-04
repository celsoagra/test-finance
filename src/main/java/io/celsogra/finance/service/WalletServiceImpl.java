package io.celsogra.finance.service;

import java.security.PublicKey;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.entity.TransactionOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private UTXOBase utxoBase;

    @Override
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
    public void validateBalance(PublicKey wallet, double value) {
        if (value > this.balance(wallet)) {
            throw new RuntimeException("not enough money");
        }
    }

}
