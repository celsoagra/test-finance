package io.celsogra.finance.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.celsogra.finance.base.CoinBase;
import io.celsogra.finance.dto.WalletDTO;
import io.celsogra.finance.service.WalletService;
import io.celsogra.finance.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WalletController {

    @Autowired
    private CoinBase coinBase;
    
    @Autowired
    private WalletService walletService;

    @GetMapping(value = "/coinbase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getCoinbase()
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("CoinbaseController.getCoinbase()");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("id", coinBase.getPublicKeyAsString()));
    }
    
    @PostMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getBalanceWallet(@RequestBody WalletDTO wallet)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("CoinbaseController.getCoinbase()");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("balance", walletService.balance(CryptUtil.getKeyFromString(wallet.getId())) ) );
    }

}
