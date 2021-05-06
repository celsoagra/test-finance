package io.celsogra.finance.genesis;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.celsogra.finance.base.Blockchain;
import io.celsogra.finance.builder.TransactionBuilder;
import io.celsogra.finance.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenesisInit {

    @Autowired
    private TransactionBuilder TransactionBuilder;

    @Autowired
    private Blockchain blockchain;

    @PostConstruct
    private void postConstruct() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException, UnsupportedEncodingException {

        Transaction genesis = TransactionBuilder.buildGenesis();
        blockchain.addGenesisTransaction(genesis);
    }

}
