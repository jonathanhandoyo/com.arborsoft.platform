package com.arborsoft.platform.api.handler;

import com.arborsoft.platform.core.exception.DatabaseOperationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, Throwable throwable) {
        return new ResponseEntity<>(this.map(request, throwable, true), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {
            DatabaseOperationException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handleWithStackFrames(HttpServletRequest request, DatabaseOperationException exception) {
        return new ResponseEntity<>(this.map(request, exception, true), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {
            NoHandlerFoundException.class,
            ClassCastException.class,
            IllegalArgumentException.class,
            NullPointerException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handleWithoutStackFrames(HttpServletRequest request, DatabaseOperationException exception) {
        return new ResponseEntity<>(this.map(request, exception, false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> map(HttpServletRequest request, Throwable throwable, boolean withStackFrames) {
        LOG.error(throwable.getMessage(), throwable);

        Map<String, Object> body = new HashMap<>();
        body.put("uri", request.getRequestURI());
        body.put("class", throwable.getClass());
        body.put("message", throwable.getMessage());
        body.put("throwable", withStackFrames ? ExceptionUtils.getStackFrames(throwable) : null);

        return body;
    }
}
