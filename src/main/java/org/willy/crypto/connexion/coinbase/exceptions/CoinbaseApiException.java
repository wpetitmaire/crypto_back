package org.willy.crypto.connexion.coinbase.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoinbaseApiException extends Exception{

    @Getter
    @Setter
    HttpStatus httpStatus;
    private final static Logger logger = LogManager.getLogger(CoinbaseApiException.class);

    public CoinbaseApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        logger.info("New CoinbaseApiException : {} - {}", httpStatus, message);
    }
}
