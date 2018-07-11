package com.xperienceit;

public class Helper {

    public static float getPrice(String price, String discount) {
        float floatPrice = Float.parseFloat(price);
        float floatDiscount = Float.parseFloat(discount);
        floatPrice = floatPrice - (floatPrice * floatDiscount / 100);
        floatPrice = floatPrice + (floatPrice * (float) 0.18);
        return floatPrice;
    }

    public static float getPriceWithoutGst(String price, String discount) {
        float floatPrice = Float.parseFloat(price);
        float floatDiscount = Float.parseFloat(discount);
        floatPrice = floatPrice - (floatPrice * floatDiscount / 100);
        return floatPrice;
    }
}
