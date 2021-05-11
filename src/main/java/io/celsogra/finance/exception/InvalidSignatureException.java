package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class InvalidSignatureException extends ApplicationHandledException {

    public InvalidSignatureException() {
        super(ExceptionCode.INVALID_SIGNATURE, ExceptionCode.INVALID_SIGNATURE.getMessage());
    }
    
    public InvalidSignatureException(String message) {
        super(ExceptionCode.INVALID_SIGNATURE, message);
    }
}