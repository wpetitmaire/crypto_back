package org.willy.crypto.connexion.coinbase.objects.transaction;

public enum TransactionType {
    SEND("send"),
    REQUEST("request"),
    TRANSFERT("transfer"),
    BUY("buy"),
    SELL("sell"),
    FIAT_DEPOSIT("fiat_deposit"),
    FiAT_WITHDRAWAL("fiat_withdrawal"),
    EXCHANGE_DEPOSIT("exchange_deposit"),
    EXCHANGE_WITHDRAWAL("exchange_withdrawal"),
    VAULT_WITHDRAWAL("vault_withdrawal"),
    ADVANCED_TRADE_FILL("advanced_trade_fill");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }
}
