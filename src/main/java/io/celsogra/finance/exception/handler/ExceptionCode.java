package io.celsogra.finance.exception.handler;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {
    NOT_ENOUGH_MONEY(HttpStatus.BAD_REQUEST, "Not enough money");
    
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
