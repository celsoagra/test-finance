package io.celsogra.finance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInput {
    private String transactionOutputId; // Reference to TransactionOutputs -> transactionId
    private TransactionOutput utxo; // Contains the Unspent transaction output
}
