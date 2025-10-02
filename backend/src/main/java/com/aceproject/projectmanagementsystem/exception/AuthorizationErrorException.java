package com.aceproject.projectmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationErrorException extends RuntimeException {
    public AuthorizationErrorException(String message) {
        super(message);
    }
}
