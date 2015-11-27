package com.arborsoft.platform.handler;

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
        LOG.error(throwable.getMessage(), throwable);

        Map<String, Object> body = new HashMap<>();
        body.put("uri", request.getRequestURI());
        body.put("message", throwable.getMessage());
        body.put("throwable", throwable);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, NoHandlerFoundException noHandlerFoundException) {
        LOG.error(noHandlerFoundException.getMessage(), noHandlerFoundException);

        Map<String, Object> body = new HashMap<>();
        body.put("uri", request.getRequestURI());
        body.put("message", noHandlerFoundException.getMessage());
        body.put("throwable", noHandlerFoundException);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
