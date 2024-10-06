package com.example.myandroidapplication;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey
    );
}

