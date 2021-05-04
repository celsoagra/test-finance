package io.celsogra.finance.base;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.celsogra.finance.entity.TransactionOutput;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UTXOBase {
    
    private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    
    public void put(String key, TransactionOutput value) {
        UTXOs.put(key, value);
    }
    
    public TransactionOutput remove(String key) {
        return UTXOs.remove(key);
    }
    
    public TransactionOutput get(String key) {
        return UTXOs.get(key);
    }
    
    public Set<Entry<String, TransactionOutput>> entries() {
        return UTXOs.entrySet();
    }
    
    public HashMap<String,TransactionOutput> map() {
        return UTXOs;
    }
}
