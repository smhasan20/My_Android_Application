package com.example.myandroidapplication;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {
    private static final String API_KEY = "71bf49f8bb37a0dd83c31e76ab20ebca";
    private static final String CITY_NAME = "Dhaka";

    private TextView temperatureTextView;
    private TextView humidityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);

        Call<WeatherResponse> call = apiService.getWeather(CITY_NAME, API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        WeatherMain weatherMain = weatherResponse.getMain();
                        double temperature = weatherMain.getTemp();
                        int humidity = weatherMain.getHumidity();

                        String temperatureText = String.format("Temperature: %.1f°C", temperature);
                        temperatureTextView.setText(temperatureText);

                        String humidityText = String.format("Humidity: %d%%", humidity);
                        humidityTextView.setText(humidityText);
                    }
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle network errors
            }
        });
    }
}
