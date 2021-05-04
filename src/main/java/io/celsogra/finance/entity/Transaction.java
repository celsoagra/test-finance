package io.celsogra.finance.entity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;

import io.celsogra.finance.util.CryptUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Transaction {
    private static double MINIMUM_TRANSACTION = 0.000001d;
    
    private String transactionId;
    private PublicKey sender;
    private PublicKey reciepient;
    private double value;
    private byte[] signature;

    private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

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

    private String calulateHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String input = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value) + ++sequence;
        return CryptUtil.applySha256(input);
    }

    public void generateSignature(PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        String data = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value);
        signature = CryptUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifiySignature()
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        String data = CryptUtil.getStringFromKey(sender) + CryptUtil.getStringFromKey(reciepient)
                + Double.toString(value);
        return CryptUtil.verifyECDSASig(sender, data, signature);
    }

    public double getInputsValue() {
        double total = 0;
        for (TransactionInput input : inputs) {
            
            if (input.getUTXO() == null)
                continue; // if Transaction can't be found skip it
            total += input.getUTXO().getValue();
        }
        return total;
    }

    // returns sum of outputs:
    public double getOutputsValue() {
        double total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }
    
    // FIXME
    public boolean processTransaction(HashMap<String,TransactionOutput> UTXOs) {

        try {
            if (!verifiySignature()) {
                System.out.println("#Transaction Signature failed to verify");
                return false;
            }
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e1) {
            return false;
        }

        for (TransactionInput i : inputs) {
            i.setUTXO(UTXOs.get(i.getTransactionOutputId()));
        }

        // check if transaction is valid:
        if (getInputsValue() < MINIMUM_TRANSACTION) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs:
        double leftOver = getInputsValue() - value; // get value of inputs then the left over change:
        
        try {
            transactionId = calulateHash();
            outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // send value to recipient
            outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // send the left over 'change' back to
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
                                                                                  // sender

        // add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            UTXOs.put(o.getId(), o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null)
                continue; // if Transaction can't be found skip it
            UTXOs.remove(i.getUTXO().getId());
        }

        return true;
    }

}
