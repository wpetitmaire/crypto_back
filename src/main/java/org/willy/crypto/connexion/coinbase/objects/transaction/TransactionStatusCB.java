package org.willy.crypto.connexion.coinbase.objects.transaction;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration of transaction status
 * <ul>
 *     <li>pending</li>
 *     <li>completed</li>
 *     <li>failed</li>
 *     <li>expired</li>
 *     <li>canceled</li>
 *     <li>waiting_for_signature</li>
 *     <li>waiting_for_clearing</li>
 * </ul>
 */
public enum TransactionStatusCB {
    @SerializedName("pending")
    PENDING,

    @SerializedName("completed")
    COMPLETED,

    @SerializedName("failed")
    FAILED,

    @SerializedName("expired")
    EXPIRED,

    @SerializedName("canceled")
    CANCELED,

    @SerializedName("waiting_for_signature")
    WAITING_FOR_SIGNATURE,

    @SerializedName("waiting_for_clearing")
    WAITING_FOR_CLEARING;

}
