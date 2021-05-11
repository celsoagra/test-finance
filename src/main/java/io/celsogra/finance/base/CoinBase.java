package io.celsogra.finance.base;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.celsogra.finance.util.CryptUtil;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CoinBase {
    
    @Value("${app.default.coin.genesis}")
    private double genesisCoins;
    
    @Value("${app.default.coin.faucet}")
    private double faucetCoins;
    
    @Value("${app.default.coin.tax}")
    private double taxToBePayed;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private boolean generated = false;
    
    public double getTaxToBePayed() {
        return taxToBePayed;
    }
    
    public BigDecimal getTaxToBePayedAsBigDecimal() {
        return BigDecimal.valueOf(taxToBePayed);
    }
    
    public double getCoinsFromGenesis() {
        return genesisCoins;
    }
    
    public double getCoinsFromFaucet() {
        return faucetCoins;
    }

    public String getPublicKeyAsString() {
        PublicKey key = getPublicKey();

        byte[] byte_pubkey = key.getEncoded();
        return Base64.getEncoder().encodeToString(byte_pubkey);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void generateKeyPair() {
        KeyPair keyPair = CryptUtil.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

}
