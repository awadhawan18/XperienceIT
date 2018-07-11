package com.xperienceit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class CheckoutPage extends AppCompatActivity {
    User user;
    String availibilityCheck;
    DatePickerDialog.OnDateSetListener datePickerListener;
    private int day, month, year;
    private ArrayList<HashMap<String, String>> customizationList;
    private HashMap<String, ArrayList<String>> unavailableSlots;
    private ArrayList<String> availableSlots;
    private ArrayAdapter<String> dropDownAdapter;
    private ArrayList<String> slotsList;
    private EditText edittext;
    private EditText name, email, mobile;
    private Button payment;
    private Calendar calendar;
    private Spinner dropDownSpinner;
    private XperienceObject object;
    private String totalAmount, date, CALENDAR_URL = "https://xperienceit.in/back/getResponse.php?table=packagecalendar&where=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);

        object = getIntent().getParcelableExtra("details");
        customizationList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("customizations");
        //Toast.makeText(getApplicationContext(),customizationList.toString(),Toast.LENGTH_LONG).show();
        unavailableSlots = new HashMap<>();
        availableSlots = new ArrayList<>();
        getPackageCalendar(CALENDAR_URL.concat("p_id=" + object.getId()));

        TableLayout packageDetails = findViewById(R.id.package_details);
        //CardView packageDetails = findViewById(R.id.package_details);
        TextView packageName = packageDetails.findViewById(R.id.package_name);
        TextView price = packageDetails.findViewById(R.id.price);
        TextView totalPrice = packageDetails.findViewById(R.id.total_price);


        name = findViewById(R.id.customer_name);
        email = findViewById(R.id.customer_email);
        mobile = findViewById(R.id.customer_phone);
        dropDownSpinner = findViewById(R.id.slots);
        payment = findViewById(R.id.proceed);
        calendar = Calendar.getInstance();
        splitSlots(object.getSlots());
        user = new User(CheckoutPage.this);
        name.setText(user.getName().toUpperCase());
        email.setText(user.getEmail());

        dropDownSpinner.setEnabled(false);

        packageName.setText(object.getName());
        price.setText("Rs.".concat(String.format("%.2f", Helper.getPriceWithoutGst(object.getPrice(), object.getDiscount()))));

        float total = Helper.getPriceWithoutGst(object.getPrice(), object.getDiscount());
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

                    /*packageName.append("\n+"+temp.get("name"));
                    price.append("\n+"+temp.get("price"));
                    total+=Integer.parseInt(temp.get("price"));*/

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

        totalPrice.setText("Rs." + String.format("%.2f", total + tax));
        totalAmount = String.valueOf(total);

        edittext = findViewById(R.id.date);
        datePickerListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int yr, int monthOfYear,
                                  int dayOfMonth) {
                year = yr;
                month = monthOfYear;
                day = dayOfMonth;
                availableSlots.clear();
                updateLabel();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                calendar.set(year, month, day);
                date = sdf.format(calendar.getTime());

                Log.v("Date", date);
                dropDownSpinner.setEnabled(true);
                dropDownSpinner.setClickable(true);
                if (!unavailableSlots.isEmpty()) {
                    ArrayList<String> temp = unavailableSlots.get(date);
                    if (temp != null) {
                        for (String i : slotsList) {
                            if (!temp.contains(i)) {
                                availableSlots.add(i);
                            }
                        }
                    } else {
                        availableSlots = slotsList;
                    }

                } else {
                    availableSlots.addAll(slotsList);
                }

                getDropDownAdapter();
                dropDownSpinner.setAdapter(dropDownAdapter);
                dropDownSpinner.setSelection(availableSlots.size() - 1);

            }
        };


        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog dialog = new DatePickerDialog(CheckoutPage.this, datePickerListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Name or Login", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email or Login", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mobile.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edittext.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Select a Date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(dropDownSpinner.getSelectedItem().toString())) {
                    Toast.makeText(getApplicationContext(), "Please Select a Time Slot", Toast.LENGTH_SHORT).show();
                } else {
                    //Booking booking=new Booking();
                    //availibilityCheck=booking.checkAvailability();
                    //if(availibilityCheck.equalsIgnoreCase("Available")){
                    //  Toast.makeText(getApplicationContext(),availibilityCheck,Toast.LENGTH_SHORT).show();
                    booking();
                    //}else{
                    //  Toast.makeText(getApplicationContext(),"Sorry Booking Date Not Available",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


    public void getPackageCalendar(String url) {

        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject calendarObject = response.getJSONObject(i);
                                //Log.v("json object", String.valueOf(customizationObject));
                                int avail = Integer.parseInt(calendarObject.getString("avail"));

                                if (avail == 0) {
                                    String date = calendarObject.getString("date").trim();
                                    String slot = calendarObject.getString("json").trim();
                                    //Toast.makeText(getApplicationContext(),date,Toast.LENGTH_LONG).show();
                                    //Toast.makeText(getApplicationContext(),slot,Toast.LENGTH_LONG).show();

                                    ArrayList<String> tempList = null;
                                    if (unavailableSlots.containsKey(date)) {
                                        tempList = unavailableSlots.get(date);
                                        if (tempList == null) {
                                            tempList = new ArrayList<>();
                                        }
                                        tempList.add(slot);
                                    } else {
                                        tempList = new ArrayList<>();
                                        tempList.add(slot);
                                    }
                                    unavailableSlots.put(date, tempList);


                                }

                                //Log.v("categories", String.valueOf(categories));
                            }
                            Log.v("slots", unavailableSlots.toString());


                        } catch (final JSONException e) {

                        } finally {
                            dropDownSpinner.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("cant get slots", error.toString());
                        Toast.makeText(getApplicationContext(), "Sorry Couldn't fetch data", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(jsObjRequest);

    }

    private void updateLabel() {

        edittext.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(day).append("/").append(month + 1).append("/").append(year).append(" "));
    }

    private void setSpinnerAdapter() {

    }

    private void splitSlots(String str) {
        if (!str.isEmpty()) {
            slotsList = new ArrayList<>(Arrays.asList(str.split("\\s*,\\s*")));
            slotsList.add("SELECT TIME SLOT");
        }


        //Toast.makeText(getApplicationContext(),slotsList.toString(),Toast.LENGTH_SHORT).show();
    }

    private void getDropDownAdapter() {
        dropDownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, availableSlots) {
            @Override
            public int getCount() {
                return availableSlots.size() - 1;
            }
        };
    }

    private void booking() {
        Intent intent = new Intent(getApplicationContext(), Booking.class);
        intent.putExtra("price", totalAmount);
        intent.putExtra("customizations", customizationList);
        intent.putExtra("details", object);
        intent.putExtra("userName", name.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("phone", mobile.getText().toString());
        intent.putExtra("date", date.toString());
        intent.putExtra("time", dropDownSpinner.getSelectedItem().toString());
        startActivity(intent);
    }
}
