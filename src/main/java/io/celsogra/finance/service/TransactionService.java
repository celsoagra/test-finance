package io.celsogra.finance.service;

import io.celsogra.finance.dto.TransactionDTO;

public interface TransactionService {
    void createTransaction(TransactionDTO dto);
}
