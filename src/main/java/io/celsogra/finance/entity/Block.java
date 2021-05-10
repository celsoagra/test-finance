package io.celsogra.finance.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.celsogra.finance.util.CryptUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Block implements Serializable {

    private String hash;
    private String previousHash;
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private Map<String,TransactionOutput> utxos = new HashMap<String,TransactionOutput>();

    public static Block create(String previousHash) {
        return new Block(previousHash);
    }
    
    private Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }

    public String calculateHash() {
        return CryptUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(transactions.hashCode()) + Integer.toString(utxos.hashCode()) );
    }
    
    public void setCalculatedHash() {
        this.hash = calculateHash();
    }

    public boolean addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return true;
    }
    
    public void putTransactionOutput(String key, TransactionOutput value) {
        utxos.put(key, value);
    }
    
    public TransactionOutput removeTransactionOutput(String key) {
        return utxos.remove(key);
    }
    
    public TransactionOutput getTransactionOutput(String key) {
        return utxos.get(key);
    }
    
    public Set<Entry<String, TransactionOutput>> getTransactionOutputEntries() {
        return utxos.entrySet();
    }
    
    public Map<String,TransactionOutput> mapOfTransactionOutput() {
        return utxos;
    }
}
