package com.xperienceit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kshitiz on 24/1/18.
 */

public class PaytmChecksum {
    @SerializedName("CHECKSUMHASH")
    private String checksumHash;

    @SerializedName("ORDER_ID")
    private String orderId;

    @SerializedName("payt_STATUS")
    private String paytStatus;

    public PaytmChecksum(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }
}
