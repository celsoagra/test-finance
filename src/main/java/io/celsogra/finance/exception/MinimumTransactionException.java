package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class MinimumTransactionException extends ApplicationHandledException {

    public MinimumTransactionException() {
        super(ExceptionCode.MINIMUM_TRANSACTION_ERROR, ExceptionCode.MINIMUM_TRANSACTION_ERROR.getMessage());
    }
    
    public MinimumTransactionException(String message) {
        super(ExceptionCode.MINIMUM_TRANSACTION_ERROR, message);
    }
}
