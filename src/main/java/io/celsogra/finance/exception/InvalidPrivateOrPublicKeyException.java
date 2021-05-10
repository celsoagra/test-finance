package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class InvalidPrivateOrPublicKeyException extends ApplicationHandledException {

    public InvalidPrivateOrPublicKeyException() {
        super(ExceptionCode.INVALID_PRIVATE_OR_PUBLIC_KEY, ExceptionCode.INVALID_PRIVATE_OR_PUBLIC_KEY.getMessage());
    }
    
    public InvalidPrivateOrPublicKeyException(String message) {
        super(ExceptionCode.INVALID_PRIVATE_OR_PUBLIC_KEY, message);
    }
}