package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class DecimalsAllowedTransactionException extends ApplicationHandledException {

    public DecimalsAllowedTransactionException() {
        super(ExceptionCode.DECIMALS_ALLOWED, ExceptionCode.DECIMALS_ALLOWED.getMessage());
    }
    
    public DecimalsAllowedTransactionException(String message) {
        super(ExceptionCode.DECIMALS_ALLOWED, message);
    }
}
