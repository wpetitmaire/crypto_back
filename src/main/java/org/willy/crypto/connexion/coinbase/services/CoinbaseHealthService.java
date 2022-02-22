package org.willy.crypto.connexion.coinbase.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CoinbaseHealthService {
    final static Logger logger = LogManager.getLogger(CoinbaseHealthService.class);


}
