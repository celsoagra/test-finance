package io.celsogra.finance.base;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.celsogra.finance.entity.Block;
import io.celsogra.finance.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BlockchainBase {
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

    public void addGenesisTransaction(Transaction transaction)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Block block = Block.create("0");
        block.addTransaction(transaction);
        this.lastBlock = block;
        blockchain.add(block);
    }

    public void addTransaction(Transaction transaction) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Block block = Block.create(lastBlock.getHash());
        block.addTransaction(transaction);
        this.lastBlock = block;
        blockchain.add(block);
    }

}
