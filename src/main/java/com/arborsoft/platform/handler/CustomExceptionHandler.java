package com.arborsoft.platform.handler;

import com.arborsoft.platform.exception.DatabaseOperationException;
import com.arborsoft.platform.exception.ObjectNotFoundException;
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
        return new ResponseEntity<>(this.getThrowableInfo(request, throwable), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, DatabaseOperationException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ResponseEntity<>(this.getThrowableInfo(request, exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, NoHandlerFoundException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ResponseEntity<>(this.getThrowableInfo(request, exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, ObjectNotFoundException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ResponseEntity<>(this.getThrowableInfo(request, exception), HttpStatus.NOT_FOUND);
    }

    private <T extends Throwable> Map<String, Object> getThrowableInfo(HttpServletRequest request, T throwable) {
        Map<String, Object> body = new HashMap<>();
        body.put("uri", request.getRequestURI());
        body.put("class", throwable.getClass());
        body.put("message", throwable.getMessage());
        body.put("throwable", ExceptionUtils.getStackFrames(throwable));
        return body;
    }
}
