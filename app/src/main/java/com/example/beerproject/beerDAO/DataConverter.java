package com.example.beerproject.beerDAO;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataConverter {

    @TypeConverter
    public String fromIngredientList(ArrayList<Ingredient> ingredientList) {
        if (ingredientList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        String json = gson.toJson(ingredientList, type);
        return json;
    }

    @TypeConverter
    public ArrayList<Ingredient> toIngredientList(String ingredientListString) {
        if (ingredientListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        ArrayList<Ingredient> ingredientList = gson.fromJson(ingredientListString, type);
        return ingredientList;
    }
}