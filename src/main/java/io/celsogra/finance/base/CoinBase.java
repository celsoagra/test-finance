package io.celsogra.finance.base;

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
        if (!generated) {
            generateKeyPair();
        }

        return publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        if (!generated) {
            generateKeyPair();
        }

        return privateKey;
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        generated = true;
    }

}
