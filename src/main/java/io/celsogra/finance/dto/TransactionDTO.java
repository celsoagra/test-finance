package io.celsogra.finance.dto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    
    private String sender;
    private String signature;
    private String receiver;
    private double value;
    
    public PublicKey getSenderAsPubKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return getPublicKeyFromString(sender);
    }
    
    public PublicKey getReceiverAsPubKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return getPublicKeyFromString(receiver);
    }
    
    private PublicKey  getPublicKeyFromString(String str) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        byte[] encodedPublicKey = Base64.getDecoder().decode(str);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        return kf.generatePublic(spec);
    }
    
}
