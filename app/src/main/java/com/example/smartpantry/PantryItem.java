package com.example.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantryItem {
    private final int id;
    private final String name;
    private final double quantity;
    private final String unit;
    private final String expiryDate;

    public PantryItem(int id, String name, double quantity,String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public int id(){

        return id;
    }
    public String name(){

        return name;
    }
    public double quantity(){

        return quantity;
    }
    public String unit(){

        return unit;
    }
    public String expiryDate(){
        return expiryDate;
    }

    public Date getExpiryDate() {
        if (expiryDate == null)
            return null;
    try{
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expiryDate);}
    catch (
            ParseException e) {
        return null;
    }

    }

    public int getDaysToExpiry() {
        Date expiry = getExpiryDate();
        if (expiry == null)
            return Integer.MAX_VALUE;
        long diff = expiry.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean isExpiringSoon(int threshold) {
        return getDaysToExpiry() < threshold;
    }
}
