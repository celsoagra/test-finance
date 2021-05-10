package io.celsogra.finance.dto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import io.celsogra.finance.util.CryptUtil;
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
    
    public PublicKey getSenderAsPubKey() {
        return CryptUtil.getKeyFromString(sender);
    }
    
    public PublicKey getReceiverAsPubKey() {
        return CryptUtil.getKeyFromString(receiver);
    }

}
