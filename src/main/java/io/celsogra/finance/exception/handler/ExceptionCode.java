package io.celsogra.finance.exception.handler;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
    NOT_ENOUGH_MONEY(HttpStatus.BAD_REQUEST, "Not enough money"),
    MINIMUM_TRANSACTION_ERROR(HttpStatus.BAD_REQUEST, "transaction must bem higher than minimum Transaction"),
    DECIMALS_ALLOWED(HttpStatus.BAD_REQUEST, "theres a limited number of decimals that must be respected"),
    SIGNATURE_ERROR(HttpStatus.BAD_REQUEST, "some problems has occured with signature generation algorithm and provider"),
    HASH_GENERATION_ERROR(HttpStatus.BAD_REQUEST, "some problems has occured with message digest to create hash"),
    INVALID_PRIVATE_OR_PUBLIC_KEY(HttpStatus.BAD_REQUEST, "invalid private or public key"),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "invalid signature"),
    SIGNIATURE_NOT_INITIALIZED(HttpStatus.BAD_REQUEST, "Signature was not initialized");
    
    private final String message;
    private final HttpStatus status;
    
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public HttpStatus getStatus() {
        return status;
    }

}
