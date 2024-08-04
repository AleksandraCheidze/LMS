package de.aittr.lmsbe.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends RestException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
