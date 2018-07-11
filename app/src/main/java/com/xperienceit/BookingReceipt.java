package com.xperienceit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookingReceipt extends AppCompatActivity {
    String ORDER_CONFIRMAPI = "https://xperienceit.in/back/order_confirmapi.php";
    private ArrayList<HashMap<String, String>> customizationList;
    private String date, time, name, email, mobile, amount;
    private String orderId, txnAmount, txnDate;
    private XperienceObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_receipt);
        Bundle bundle = getIntent().getBundleExtra("response");
        orderId = bundle.getString("ORDERID");
        txnAmount = bundle.getString("TXNAMOUNT");
        txnDate = bundle.getString("TXNDATE");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        amount = getIntent().getStringExtra("amount");
        object = getIntent().getParcelableExtra("details");

        customizationList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("customizations");
        notifySuccessfulBooking();
        displayDetails();

        final JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                //json.put(key, JSONObject.wrap(bundle.get(key)));
                json.put(key, bundle.get(key));
                Log.v("JSON WRAP", json.toString());

            } catch (JSONException e) {
                //Handle exception here
                Log.v("Json Exception", e.toString());
            }
        }

        //Toast.makeText(getApplicationContext(),jsonObj.toString(),Toast.LENGTH_LONG).show();
        Log.v("String Request", json.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ORDER_CONFIRMAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("RESPONSE ", response);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("response", json.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void notifySuccessfulBooking() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, Notifications.class);
        notificationIntent.putExtra("Type", "booking successful");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public void displayDetails() {
        TextView invoiceNumber = findViewById(R.id.invoice_number);
        TextView invoiceDate = findViewById(R.id.invoice_date);
        TextView amountPaid = findViewById(R.id.amount_paid);

        invoiceNumber.setText(orderId);
        invoiceDate.setText(txnDate);
        amountPaid.setText(txnAmount);

        TextView billTo = findViewById(R.id.customer_name);
        TextView customerMobile = findViewById(R.id.contact_number);
        TextView customerEmail = findViewById(R.id.customer_email);

        billTo.setText(name);
        customerMobile.setText(mobile);
        customerEmail.setText(email);

        TextView xperienceName = findViewById(R.id.xperience_name);
        TextView xperienceDate = findViewById(R.id.date);
        TextView xperienceTime = findViewById(R.id.time);
        TextView venue = findViewById(R.id.venue);

        xperienceName.setText(object.getName());
        xperienceTime.setText(time);
        xperienceDate.setText(date);
        venue.setText(object.getAddress());


        TableLayout packageDetails = findViewById(R.id.package_details);
        TextView packageName = packageDetails.findViewById(R.id.package_name);
        TextView price = packageDetails.findViewById(R.id.price);
        TextView totalPrice = packageDetails.findViewById(R.id.total_price);

        packageName.setText(object.getName());
        price.setText("Rs.".concat(object.getPrice()));

        float total = Integer.parseInt(object.getPrice());
        int i = 1;
        if (customizationList != null) {

            for (HashMap<String, String> temp : customizationList) {
                if (temp.get("switch").equals("true")) {

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.table_row, null);
                    TextView customisationName = row.findViewById(R.id.item_name);
                    TextView customisationPrice = row.findViewById(R.id.item_price);
                    customisationName.setText(temp.get("name"));
                    customisationPrice.setText("Rs.".concat(temp.get("price")));
                    packageDetails.addView(row, i);
                    i++;

                    total += Integer.parseInt(temp.get("price"));


                }

            }

        }
        float tax = (float) 0.18 * total;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TableRow row = (TableRow) inflater.inflate(R.layout.table_row, null);
        TextView taxName = row.findViewById(R.id.item_name);
        TextView taxPrice = row.findViewById(R.id.item_price);
        taxName.setText("GST 18%");
        taxPrice.setText("Rs.".concat(String.format("%.2f", tax)));

        packageDetails.addView(row, i);


        totalPrice.setText("Rs.".concat(String.format("%.2f", total + tax)));
        Button explore = findViewById(R.id.explore);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
