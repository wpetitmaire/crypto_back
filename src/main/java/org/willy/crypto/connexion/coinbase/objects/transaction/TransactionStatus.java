package org.willy.crypto.connexion.coinbase.objects.transaction;

public enum TransactionStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed"),
    EXPIRED("expired"),
    CANCELED("canceled"),
    WAITING_FOR_SIGNATURE("waiting_for_signature"),
    WAITING_FOR_CLEARING("waiting_for_clearing");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }
}
