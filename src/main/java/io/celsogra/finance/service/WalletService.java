package io.celsogra.finance.service;

import java.security.PublicKey;

public interface WalletService {

    double balance(PublicKey wallet);
    
    void validateBalance(PublicKey wallet, double value);
    
}
