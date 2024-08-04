package de.aittr.lmsbe.handler;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AmazonS3Exception.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        AmazonS3Exception s3Exception = (AmazonS3Exception) ex;

        Map<String, String> response = Map.of(
                "errorCode", s3Exception.getErrorCode(),
                "errorMessage", s3Exception.getErrorMessage()
        );

        if (HttpStatus.resolve(s3Exception.getStatusCode()) != null &&
                HttpStatus.INTERNAL_SERVER_ERROR.equals(HttpStatus.resolve(s3Exception.getStatusCode()))) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(response, new HttpHeaders(), Objects.requireNonNull(HttpStatus.resolve(s3Exception.getStatusCode())));
    }
}
