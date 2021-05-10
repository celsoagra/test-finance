package io.celsogra.finance.exception.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_NAME_KEY = "name";
    private static final String EXCEPTION_MESSAGE_KEY = "message";
    
	@ExceptionHandler(ApplicationHandledException.class)
	public final ResponseEntity<Map<String, String>> handle(ApplicationHandledException e) {
	    log.error("error: {}, stacktrace: {}", e.getCode().name(), e);
	    Map<String, String> map = CollectionUtils.newHashMap(2);
	    map.put(EXCEPTION_NAME_KEY, e.getCode().name());
	    map.put(EXCEPTION_MESSAGE_KEY, e.getMessage());
	    
	    return ResponseEntity.status(e.getCode().getStatus()).body(map);
	}
	
}
