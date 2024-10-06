package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CurrencyActivity extends AppCompatActivity {
    private String baseCurrency = "EUR";
    private String convertedToCurrency = "USD";
    private float conversionRate = 0f;
    private EditText etFirstConversion;
    private EditText etSecondConversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        etFirstConversion = findViewById(R.id.et_firstConversion);
        etSecondConversion = findViewById(R.id.et_secondConversion);
        spinnerSetup();
        textChangedStuff();
    }
    private void textChangedStuff() {
        etFirstConversion.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    getApiResult();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Type a value", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Main", "Before Text Changed");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Main", "OnTextChanged");
            }
        });
    }

    private void getApiResult() {
        if (etFirstConversion != null && !etFirstConversion.getText().toString().isEmpty() && !etFirstConversion.getText().toString().isBlank()) {

            // Update the API URL to include the API key
            String API = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;

            if (baseCurrency.equals(convertedToCurrency)) {
                Toast.makeText(getApplicationContext(), "Please pick a currency to convert", Toast.LENGTH_SHORT).show();
            } else {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        // Fetch data from the API
                        InputStream inputStream = new URL(API).openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();
                        String apiResult = stringBuilder.toString();

                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(apiResult);
                        conversionRate = Float.parseFloat(jsonObject.getJSONObject("rates").getString(convertedToCurrency));

                        Log.d("Main", String.valueOf(conversionRate));
                        Log.d("Main", apiResult);

                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            String text = String.valueOf(Float.parseFloat(etFirstConversion.getText().toString()) * conversionRate);
                            etSecondConversion.setText(text);
                        });

                    } catch (Exception e) {
                        Log.e("Main", e.toString());
                    }
                });
            }
        }
    }

    private void spinnerSetup() {
        Spinner spinner = findViewById(R.id.spinner_firstConversion);
        Spinner spinner2 = findViewById(R.id.spinner_secondConversion);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.currencies2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No implementation needed
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                baseCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No implementation needed
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertedToCurrency = parent.getItemAtPosition(position).toString();
                getApiResult();
            }
        });
    }
}