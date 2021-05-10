package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class HashGenerationException extends ApplicationHandledException {

    public HashGenerationException() {
        super(ExceptionCode.HASH_GENERATION_ERROR, ExceptionCode.HASH_GENERATION_ERROR.getMessage());
    }
    
    public HashGenerationException(String message) {
        super(ExceptionCode.HASH_GENERATION_ERROR, message);
    }
}