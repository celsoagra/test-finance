package io.celsogra.finance.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.celsogra.finance.dto.TransactionDTO;
import io.celsogra.finance.service.TransactionService;
import io.celsogra.finance.service.WalletService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTransaction(@Valid @RequestBody TransactionDTO dto)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("TransactionController.createTransaction(TransactionDTO) - Params: {}", dto);
        walletService.validateBalance(dto.getSenderAsPubKey(), dto.getValue());
        transactionService.createTransaction(dto);
        return ResponseEntity.ok().build();
    }
}
