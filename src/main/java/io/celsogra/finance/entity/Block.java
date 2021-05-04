package io.celsogra.finance.entity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import io.celsogra.finance.util.CryptUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Block {

    private String hash;
    private String previousHash;
    private String merkleRoot;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;

    public static Block create(String previousHash) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return new Block(previousHash);
    }
    
    private Block(String previousHash) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash();
    }

    public String calculateHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return CryptUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    public void mineBlock(int difficulty) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        merkleRoot = CryptUtil.getMerkleRoot(transactions);
        String target = CryptUtil.getDificultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public boolean addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return true;
    }
}
