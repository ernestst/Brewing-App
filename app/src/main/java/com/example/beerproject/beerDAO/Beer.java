package com.example.beerproject.beerDAO;


import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;

@Entity(tableName = "beer")
public class Beer {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    @TypeConverters(DataConverter.class)
    private ArrayList<Ingredient> ingredientList;
    private Float brewingTime;
    private Float brewingTemperature;
    private String beerStyle;
    @Nullable
    private String photoPath;
    @Nullable
    private Float tasteRate;
    @Nullable
    private Float apperanceRate;
    @Nullable
    private Float bitternessRate;
    @Nullable
    private Float aromaRate;
    @Nullable
    private String comment;


    public Beer(String name, ArrayList<Ingredient> ingredientList, Float brewingTime, Float brewingTemperature, String beerStyle){
        this.name =name;
        this.ingredientList = ingredientList;
        this.brewingTime = brewingTime;
        this.brewingTemperature = brewingTemperature;
        this.beerStyle = beerStyle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public Float getBrewingTime() {
        return brewingTime;
    }

    public void setBrewingTime(Float brewingTime) {
        this.brewingTime = brewingTime;
    }

    public Float getBrewingTemperature() {
        return brewingTemperature;
    }

    public void setBrewingTemperature(Float brewingTemperature) {
        this.brewingTemperature = brewingTemperature;
    }

    @Nullable
    public Float getTasteRate() {
        return tasteRate;
    }

    public void setTasteRate(@Nullable Float tasteRate) {
        this.tasteRate = tasteRate;
    }

    @Nullable
    public Float getApperanceRate() {
        return apperanceRate;
    }

    public void setApperanceRate(@Nullable Float apperanceRate) {
        this.apperanceRate = apperanceRate;
    }

    @Nullable
    public Float getBitternessRate() {
        return bitternessRate;
    }

    public void setBitternessRate(@Nullable Float bitternessRate) {
        this.bitternessRate = bitternessRate;
    }

    @Nullable
    public Float getAromaRate() {
        return aromaRate;
    }

    public void setAromaRate(@Nullable Float aromaRate) {
        this.aromaRate = aromaRate;
    }

    @Nullable
    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(@Nullable String photoPath) {
        this.photoPath = photoPath;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comments) {
        this.comment = comments;
    }

    public String getBeerStyle() {
        return beerStyle;
    }

    public void setBeerStyle(String beerStyle) {
        this.beerStyle = beerStyle;
    }
}
