package com.xperienceit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by kshitiz on 13/2/18.
 */

public interface PayTransactionApi {
    String TRANSACTION_STATUS_URL = "https://pguat.paytm.com/oltp/HANDLER_INTERNAL/";

    @FormUrlEncoded
    @POST("getTxnStatus")
    Call<PaytmChecksum> verifyTransaction(
            //@Field("MID") String mId,
            @Field("ORDER_ID") String orderId
    );

}
