package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class SignatureGenerationException extends ApplicationHandledException {

    public SignatureGenerationException() {
        super(ExceptionCode.SIGNATURE_ERROR, ExceptionCode.SIGNATURE_ERROR.getMessage());
    }
    
    public SignatureGenerationException(String message) {
        super(ExceptionCode.SIGNATURE_ERROR, message);
    }
}