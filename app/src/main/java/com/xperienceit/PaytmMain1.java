package com.xperienceit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//implementing PaytmPaymentTransactionCallback to track the payment result.
public class PaytmMain1 extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    public String checksum, orderId, txnAmount, custId, callBackURL;
    String TRANSACTION_STATUS_URL = "https://xperienceit.in/back/transacStatus.php";
    private String date, time, name, email, mobile, amount;
    private XperienceObject object;
    private ArrayList<HashMap<String, String>> customizationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateCheckSum();
    }

    private void generateCheckSum() {
        //getting the tax amount first.
        Bundle extras = getIntent().getExtras();
        txnAmount = extras.getString("finalAmount");
        orderId = extras.getString("refer");
        custId = extras.getString("custId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        amount = getIntent().getStringExtra("amount");
        object = getIntent().getParcelableExtra("details");
        customizationList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("customizations");


        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PaytmApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //creating the retrofit api service
        PaytmApi apiService = retrofit.create(PaytmApi.class);
        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                PaytmConstants.M_ID,
                PaytmConstants.CHANNEL_ID,
                txnAmount,
                PaytmConstants.WEBSITE,
                PaytmConstants.CALLBACK_URL,
                PaytmConstants.INDUSTRY_TYPE_ID,
                orderId
        );
        callBackURL = paytm.getCallBackUrl();

        //creating a call object from the apiService
        Call<PaytmChecksum> call = apiService.getChecksum(
                paytm.getmId(),
                orderId,
                custId,
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                callBackURL,
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<PaytmChecksum>() {
            @Override
            public void onResponse(Call<PaytmChecksum> call, Response<PaytmChecksum> response) {
                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                checksum = response.body().getChecksumHash();
                Log.v(" first checksum ", checksum);
                initializePaytmPayment(checksum, paytm);
            }

            @Override
            public void onFailure(Call<PaytmChecksum> call, Throwable t) {
                Log.d("Checksum Failure", t.getMessage());
            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {
        //getting paytm service
        //PaytmPGService Service = PaytmPGService.getStagingService();
        //use this when using for production
        PaytmPGService Service = PaytmPGService.getProductionService();
        //creating a hashmap and adding all the values required
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PaytmConstants.M_ID);
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", custId);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", callBackURL);
        paramMap.put("CHECKSUMHASH", checksumHash);
        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);


        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);
    }

    private void verifyPayment() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TRANSACTION_STATUS_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TRANSACTION RESPONSE ", response);
                //Toast.makeText(getApplicationContext(),"VERIFICATION RES "+ response, Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "verify payment error", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderid", orderId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //all these overriden method is to detect the payment result accordingly
    @Override
    public void onTransactionResponse(Bundle bundle) {
        orderId = bundle.getString("ORDERID").toString().trim();
        verifyPayment();
        String status = bundle.getString("STATUS");
        if (status.equalsIgnoreCase("TXN_SUCCESS")) {
            Intent intent = new Intent(getApplicationContext(), BookingReceipt.class);
            intent.putExtra("response", bundle);
            intent.putExtra("details", object);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("mobile", mobile);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("amount", amount);
            intent.putExtra("customizations", customizationList);
            //Toast.makeText(this, bundle.toString(), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }

//        if(bundle.getString(""))

    }

    @Override
    public void networkNotAvailable() {
        Log.v("Bundle", "Network Not available");
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Log.v("Bundle", "client");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.v("Bundle", "some ui error occured");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.v("Bundle", "error loading webpage");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.v("Bundle", "Back pressed");
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.v("Bundle", "transaction canceled");
        notifyFailedBooking();
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }

    public void notifyFailedBooking() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, Notifications.class);
        notificationIntent.putExtra("Type", "booking failed");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }
}
