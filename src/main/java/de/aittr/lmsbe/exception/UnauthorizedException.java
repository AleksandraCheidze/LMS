package de.aittr.lmsbe.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestException {
    public UnauthorizedException(HttpStatus status, String message) {
        super(status, message);
    }
}
