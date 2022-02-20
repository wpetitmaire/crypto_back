package org.willy.crypto.connexion.coinbase.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.willy.crypto.connexion.coinbase.objects.errors.ApiError;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiControllerAdvisor extends ResponseEntityExceptionHandler {

    private static final Logger loggger = LogManager.getLogger(ApiControllerAdvisor.class);

    @ExceptionHandler(CoinbaseApiException.class)
    public ResponseEntity<ApiError> CoinbaseApiExceptionHandler(CoinbaseApiException e, WebRequest request) {
        loggger.info("CoinbaseApiExceptionHandler {} - {}", e.getHttpStatus(), e.getMessage());
        return new ResponseEntity<>(
                new ApiError(LocalDateTime.now(), e.getMessage()),
                e.getHttpStatus()
        );
    }
}
