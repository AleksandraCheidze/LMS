package de.aittr.lmsbe.github.exception;

import de.aittr.lmsbe.exception.RestException;
import org.springframework.http.HttpStatus;

public class GithubUploadFileException extends RestException {

    public GithubUploadFileException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public GithubUploadFileException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
