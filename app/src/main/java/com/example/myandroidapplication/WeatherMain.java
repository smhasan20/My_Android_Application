package com.example.myandroidapplication;
import com.google.gson.annotations.SerializedName;
public class WeatherMain {
    @SerializedName("temp")
    private double temp;

    @SerializedName("humidity")
    private int humidity;

    public double getTemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }
}
