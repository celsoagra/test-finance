package io.celsogra.finance.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import io.celsogra.finance.util.CryptUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionOutput implements Serializable {
    private String id;
    private PublicKey reciepient;
    private double value;
    private String parentTransactionId;

    public TransactionOutput(PublicKey reciepient, double value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = CryptUtil
                .applySha256(CryptUtil.getStringFromKey(reciepient) + Double.toString(value) + parentTransactionId);
    }

    public boolean belongsTo(PublicKey publicKey) {
        return (publicKey.equals(reciepient));
    }
    
    public BigDecimal getValueAsBigDecimal() {
        return BigDecimal.valueOf(this.value);
    }
}
