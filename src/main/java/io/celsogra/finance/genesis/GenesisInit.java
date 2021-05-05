package io.celsogra.finance.genesis;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.celsogra.finance.base.BlockchainBase;
import io.celsogra.finance.base.UTXOBase;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.entity.Transaction;
import io.celsogra.finance.entity.TransactionOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenesisInit {

    @Autowired
    private TransactionBuilder TransactionBuilder;

    @Autowired
    private UTXOBase utxoBase;

    @Autowired
    private BlockchainBase blockchainBase;

    @PostConstruct
    private void postConstruct() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException, UnsupportedEncodingException {

        Transaction genesis = TransactionBuilder.buildGenesis();
        TransactionOutput ouput = new TransactionOutput(genesis.getReciepient(), genesis.getValue(), genesis.getTransactionId());
        utxoBase.put(ouput.getId(), ouput);
        blockchainBase.addGenesisTransaction(genesis);
        
    }

}
