package io.celsogra.finance.base;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.celsogra.finance.entity.Block;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Blockchain {
    private ArrayList<Block> blockchain = new ArrayList<Block>();
    Block lastBlock;

    public Block get(int index) {
        return blockchain.get(0);
    }

    public int get() {
        return blockchain.size();
    }

    public Block getLastBlock() {
        return this.lastBlock;
    }

    public void addGenesisTransaction(Transaction transaction) {
        TransactionOutput ouput = new TransactionOutput(transaction.getReciepient(), transaction.getValue(), transaction.getTransactionId());
        
        Block block = Block.create("0");
        block.addTransaction(transaction);
        block.putTransactionOutput(ouput.getId(), ouput);
        block.setCalculatedHash();
        
        this.lastBlock = block;
        blockchain.add(block);
    }

    public void addTransaction(Transaction transaction, Map<String,TransactionOutput> utxos) {
        Block block = Block.create(lastBlock.getHash());
        block.addTransaction(transaction);
        block.setUtxos(utxos);
        block.setCalculatedHash();
        
        this.lastBlock = block;
        blockchain.add(block);
    }
    
    public Map<String,TransactionOutput> cloneUTXOs() {
        Map<String,TransactionOutput> currentUtxos = this.lastBlock.getUtxos();
        return currentUtxos.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
