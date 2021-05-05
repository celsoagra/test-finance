package io.celsogra.finance.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.entity.Transaction;

public interface TransactionService {
    void addToBlock(TransactionDTO dto);
    
    void addToBlock(Transaction transaction) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchProviderException, SignatureException;
}
