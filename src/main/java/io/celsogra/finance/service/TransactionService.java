package io.celsogra.finance.service;

import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;

public interface TransactionService {
    void createTransaction(TransactionDTO dto);
    
    void createTransaction(Transaction transaction);
}
