package org.willy.crypto.connexion.coinbase.objects.transaction;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration of types of transaction
 * <ul>
 *     <li>send</li>
 *     <li>request</li>
 *     <li>transfer</li>
 *     <li>buy</li>
 *     <li>sell</li>
 *     <li>fiat_deposit</li>
 *     <li>fiat_withdrawal</li>
 *     <li>exchange_deposit</li>
 *     <li>exchange_withdrawal</li>
 *     <li>vault_withdrawal</li>
 *     <li>advanced_trade_fill</li>
 * </ul>
 */
public enum TransactionType {
    @SerializedName("send")
    SEND,

    @SerializedName("request")
    REQUEST,

    @SerializedName("transfer")
    TRANSFERT,

    @SerializedName("buy")
    BUY,

    @SerializedName("sell")
    SELL,

    @SerializedName("fiat_deposit")
    FIAT_DEPOSIT,

    @SerializedName("fiat_withdrawal")
    FiAT_WITHDRAWAL,

    @SerializedName("exchange_deposit")
    EXCHANGE_DEPOSIT,

    @SerializedName("exchange_withdrawal")
    EXCHANGE_WITHDRAWAL,

    @SerializedName("vault_withdrawal")
    VAULT_WITHDRAWAL,

    @SerializedName("advanced_trade_fill")
    ADVANCED_TRADE_FILL;
}
