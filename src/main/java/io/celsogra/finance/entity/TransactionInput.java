package io.celsogra.finance.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionInput {
    private String transactionOutputId; // Reference to TransactionOutputs -> transactionId
    private TransactionOutput UTXO; // Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
