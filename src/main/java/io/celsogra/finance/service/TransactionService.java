package io.celsogra.finance.service;

import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;

public interface TransactionService {
    void addToBlock(TransactionDTO dto);
    
    void addToBlock(Transaction transaction);
}
