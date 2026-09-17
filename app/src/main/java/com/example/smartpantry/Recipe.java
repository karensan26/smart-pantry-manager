package com.example.smartpantry;

public class Recipe {
    private final int id;
    private final String name;
    private final String method;

    public Recipe(int id, String name, String method){
        this.id = id;
        this.name = name;
        this.method = method;

    }

    public int id(){
        return id;
    }
    public String name(){
        return name;
    }
    public String method(){
        return method;
    }


}
