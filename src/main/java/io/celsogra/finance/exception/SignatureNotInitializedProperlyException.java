package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class SignatureNotInitializedProperlyException extends ApplicationHandledException {

    public SignatureNotInitializedProperlyException() {
        super(ExceptionCode.SIGNIATURE_NOT_INITIALIZED, ExceptionCode.SIGNIATURE_NOT_INITIALIZED.getMessage());
    }

    public SignatureNotInitializedProperlyException(String message) {
        super(ExceptionCode.SIGNIATURE_NOT_INITIALIZED, message);
    }
}