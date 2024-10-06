package com.example.myandroidapplication;
import com.google.gson.annotations.SerializedName;


public class WeatherResponse {

    @SerializedName("main")
    private WeatherMain main;

    public WeatherMain getMain() {
        return main;
    }
}
