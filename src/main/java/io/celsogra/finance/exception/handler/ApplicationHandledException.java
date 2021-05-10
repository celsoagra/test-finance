package io.celsogra.finance.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationHandledException extends RuntimeException {
    private ExceptionCode code;
    private String message;

}
