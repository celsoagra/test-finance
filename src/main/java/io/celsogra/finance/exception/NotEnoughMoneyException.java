package io.celsogra.finance.exception;

import io.celsogra.finance.exception.handler.ApplicationHandledException;
import io.celsogra.finance.exception.handler.ExceptionCode;

public class NotEnoughMoneyException extends ApplicationHandledException {

    public NotEnoughMoneyException() {
        super(ExceptionCode.NOT_ENOUGH_MONEY, ExceptionCode.NOT_ENOUGH_MONEY.getMessage());
    }
    
    public NotEnoughMoneyException(String message) {
        super(ExceptionCode.NOT_ENOUGH_MONEY, message);
    }
}
