package com.example.smartpantry;

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
}
