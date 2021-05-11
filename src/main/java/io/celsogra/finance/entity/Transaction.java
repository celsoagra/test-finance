package io.celsogra.finance.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

import io.celsogra.finance.util.CryptUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Transaction implements Serializable {
    
    private String transactionId;
    private PublicKey sender;
    private PublicKey reciepient;
    private double value;
    private byte[] signature;

    private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

    private static int sequence = 0;

    public static Transaction create(PublicKey from, PublicKey to, double value, ArrayList<TransactionInput> inputs) {
        return new Transaction(from, to, value, inputs);
    }

    private Transaction(PublicKey from, PublicKey to, double value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }
    
    public BigDecimal getValueAsBigDecimal() {
        return BigDecimal.valueOf(this.value);
    }

    public String calulateHash() {
        String input = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value) + ++sequence;
        return CryptUtil.applySha256(input);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value);
        signature = CryptUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifiySignature() {
        String data = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value);
        return CryptUtil.verifyECDSASig(sender, data, signature);
    }

    public double getInputsValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionInput input : inputs) {
            
            if (input.getUtxo() == null)
                continue; // if Transaction can't be found skip it
            total = total.add( BigDecimal.valueOf(input.getUtxo().getValue()) );
        }
        return total.doubleValue();
    }

}
