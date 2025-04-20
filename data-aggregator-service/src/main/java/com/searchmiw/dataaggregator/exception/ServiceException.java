package com.searchmiw.dataaggregator.exception;

import org.springframework.graphql.execution.ErrorType;

public class ServiceException extends RuntimeException {
    
    private final ErrorType errorType;
    
    public ServiceException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public ServiceException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
}
