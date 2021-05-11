package io.celsogra.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyPair;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import io.celsogra.finance.base.Blockchain;
import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.exception.NotEnoughMoneyException;
import io.celsogra.finance.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @MockBean
    private TransactionService transactionService;

    @SpyBean
    private Blockchain blockchain;

    @SpyBean
    private CoinBase coinBase;

    @SpyBean
    private TransactionBuilder transactionBuilder;

    @BeforeAll
    static void beforeAll() {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String WALLET_KEY = "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEiADAlxMogaypyb/14Zu4MdmX5I2sVobKhbuUU0PkXdrRWexDABFumCSFpWK7Pocb";
    private static final double EXPECTED_BALANCE = 50.0d;
    private static final double EXPECTED_HIGHER_BALANCE = 999.0d;
    private static final double EXPECTED_FAUCET = 5.0d;

    @Test
    void testBalanceWithSuccess() {
        double balance = walletService.balance(coinBase.getPublicKey());
        assertEquals(EXPECTED_BALANCE, balance);
    }
    
    @Test
    void testFaucetWithSuccess() {
        KeyPair keypair = CryptUtil.generateKeyPair();
        double gotFaucet = walletService.faucet(CryptUtil.getStringFromKey(keypair.getPublic()));
        assertEquals(EXPECTED_FAUCET, gotFaucet);
    }
    
    @Test
    void testvalidateBalanceWithSuccess() {
        walletService.validateBalance(coinBase.getPublicKey(), EXPECTED_BALANCE);
    }
    
    @Test
    void testvalidateBalanceWithHigherResult() {
        assertThrows(NotEnoughMoneyException.class, () -> walletService.validateBalance(coinBase.getPublicKey(), EXPECTED_HIGHER_BALANCE) );
    }
    
    
    
}
