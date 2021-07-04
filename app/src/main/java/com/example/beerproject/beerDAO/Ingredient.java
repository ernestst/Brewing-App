package com.example.beerproject.beerDAO;


import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private Float amount;
    private Float minute;

    public Ingredient(String name, Float amount, Float minute){
        this.name = name;
        this.amount = amount;
        this.minute = minute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getMinute() {
        return minute;
    }

    public void setMinute(Float minute) {
        this.minute = minute;
    }

    @Override
    public String toString(){
        return name + " " + amount + " " + minute;
    }
}
