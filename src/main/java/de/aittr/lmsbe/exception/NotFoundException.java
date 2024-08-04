package de.aittr.lmsbe.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RestException {
    public NotFoundException(String entity, Long id) {
        super(HttpStatus.NOT_FOUND, entity + " with id <" + id + "> not found.");
    }

    public NotFoundException(String entity, String name) {
        super(HttpStatus.NOT_FOUND, entity + " with name <" + name + "> not found.");
    }
}
