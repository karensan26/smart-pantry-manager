package com.example.smartpantry;

public class RecipeIngredient {
    private final String name;
    private final double quantity;
    private final String unit;


    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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
}
