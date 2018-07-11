package com.xperienceit;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class XperienceObject implements Parcelable {
    public static final Parcelable.Creator<XperienceObject> CREATOR = new Parcelable.Creator<XperienceObject>() {

        @Override
        public XperienceObject createFromParcel(Parcel source) {
            return new XperienceObject(source);  //using parcelable constructor
        }

        @Override
        public XperienceObject[] newArray(int size) {
            return new XperienceObject[size];
        }
    };
    final private String IMAGE_URL = "https://xperienceit.in/uploads/images/";
    public String searchCode;
    private String name;
    private String description;
    private String smallDescription;
    private String specialFeatures;
    private String termsConditions;
    private String refundPolicy;
    private String cancelPolicy;
    private String city;
    private String address;
    private String include;
    private String exclude;
    private String price;
    private String discount;
    private String photoName;
    private String customizations;
    private String persons;
    private String id;
    private String status;
    private String slots;
    private double latitude = 0;
    private double longitude = 0;
    private ArrayList<String> photoUrl;


    public XperienceObject(JSONObject jsonObject) {

        try {
            name = jsonObject.getString("package_name");
            description = jsonObject.getString("description");
            smallDescription = jsonObject.getString("small_description");
            city = jsonObject.getString("city");
            address = jsonObject.getString("address");
            price = jsonObject.getString("selling_price");
            discount = jsonObject.getString("discount_percentage");
            photoName = jsonObject.getString("photo");
            include = jsonObject.getString("include");
            exclude = jsonObject.getString("exclude");
            customizations = jsonObject.getString("customizations");
            persons = jsonObject.getString("package_people");
            id = jsonObject.getString("id");
            slots = jsonObject.getString("slots");
            searchCode = jsonObject.getString("search_code");
            status = jsonObject.getString("status");
            specialFeatures = jsonObject.getString("special_features");
            termsConditions = jsonObject.getString("terms_condition");
            cancelPolicy = jsonObject.getString("cancel_policy");
            refundPolicy = jsonObject.getString("refund_policy");
            if (!jsonObject.getString("latitude").isEmpty()) {
                latitude = Double.parseDouble(jsonObject.getString("latitude"));
                longitude = Double.parseDouble(jsonObject.getString("longitude"));
                Log.v("lat", String.valueOf(latitude));
                Log.v("lon", String.valueOf(longitude));
            }


        } catch (JSONException e) {
            Log.e("Xperience Object", e.toString());
        }
        photoUrl = getPhotoNames(photoName);
    }

    public XperienceObject(Parcel parcel) {
        String[] data = new String[19];
        double[] coordinates = new double[2];
        ArrayList<String> photoList = new ArrayList<>();
        parcel.readStringArray(data);
        parcel.readStringList(photoList);
        parcel.readDoubleArray(coordinates);
        this.name = data[0];
        this.description = data[1];
        this.city = data[2];
        this.price = data[3];
        this.smallDescription = data[4];
        this.include = data[5];
        this.exclude = data[6];
        this.address = data[7];
        this.customizations = data[8];
        this.persons = data[9];
        this.id = data[10];
        this.slots = data[11];
        this.searchCode = data[12];
        this.status = data[13];
        this.specialFeatures = data[14];
        this.termsConditions = data[15];
        this.cancelPolicy = data[16];
        this.refundPolicy = data[17];
        this.discount = data[18];

        this.latitude = coordinates[0];
        this.longitude = coordinates[1];

        this.photoUrl = photoList;

    }

    public ArrayList<String> getPhotoNames(String str) {
        String newString = str.replaceAll("[\\[\\]^\"|\"$]", "");
        ArrayList<String> allPhotoNames = new ArrayList<>();
        String photoName;
        int startIndex = 0;
        int commaIndex = newString.indexOf(',');
        while (commaIndex > 0) {
            photoName = newString.substring(startIndex, commaIndex);
            allPhotoNames.add(makePhotoUrl(photoName));
            newString = newString.substring(commaIndex + 1, newString.length());
            commaIndex = newString.indexOf(',');
        }
        allPhotoNames.add(makePhotoUrl(newString));
        return allPhotoNames;
    }

    public String makePhotoUrl(String str) {
        return IMAGE_URL + str;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getSmallDescription() {
        return smallDescription;
    }

    public String getInclude() {
        return include;
    }

    public String getExclude() {
        return exclude;
    }

    public String getPrice() {
        return price;
    }

    public String getCustomisations() {
        return customizations;
    }

    public String getPersons() {
        return persons;
    }

    public String getId() {
        return id;
    }

    public String getSlots() {
        return slots;
    }

    public String getSearchCode() {
        return searchCode;
    }

    public ArrayList<String> getPhotoUrl() {
        return photoUrl;
    }

    public String getDiscount() {
        return discount;
    }

    public String getStatusCode() {
        return status;
    }

    public String getSpecialFeatures() {
        return specialFeatures;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public String getCancelPolicy() {
        return cancelPolicy;
    }

    public String getRefundPolicy() {
        return refundPolicy;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.name, this.description, this.city, this.price, this.smallDescription,
                this.include, this.exclude, this.address, this.customizations, this.persons,
                this.id, this.slots, this.searchCode, this.status, this.specialFeatures, this.termsConditions,
                this.cancelPolicy, this.refundPolicy, this.discount
        });

        parcel.writeStringList(this.photoUrl);

        parcel.writeDoubleArray(new double[]{this.latitude, this.longitude});
    }

}
