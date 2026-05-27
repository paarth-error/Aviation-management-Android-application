package com.example.aviation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AirHoursActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AirHoursAdapter adapter;
    private List<AirHoursData> airHoursList = new ArrayList<>();
    private List<AirHoursData> filteredList = new ArrayList<>();
    private EditText searchEditText;
    private TextView dateTextView;
    private ImageButton backButton;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_hours);

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerViewAirHours); // ✅ FIXED
        searchEditText = findViewById(R.id.et_search_flights);
        dateTextView = findViewById(R.id.tv_date);
        backButton = findViewById(R.id.btn_back);
        titleTextView = findViewById(R.id.tv_title);

        // Set up RecyclerView
        adapter = new AirHoursAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Title
        titleTextView.setText("Daily Aircraft Air Hours");

        // Load saved data (if available)
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String airHoursJson = prefs.getString("last_air_hours_json", null);
        if (airHoursJson != null) {
            parseAndDisplayResults(airHoursJson);
        } else {
            Toast.makeText(this, "No data available. Please upload a file first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseAndDisplayResults(String jsonResult) {
        try {
            JSONArray resultsArray = new JSONArray(jsonResult);
            airHoursList.clear();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject flightData = resultsArray.getJSONObject(i);
                String flightDate = flightData.getString("Flight Date");
                String regNo = flightData.getString("Reg No.");
                double airHours = flightData.getDouble("Air Hours");
                String status = flightData.getString("Status");

                airHoursList.add(new AirHoursData(flightDate, regNo, airHours, status));
            }

            if (!airHoursList.isEmpty()) {
                dateTextView.setText(airHoursList.get(0).getFlightDate());
            }

            filterList(searchEditText.getText().toString());

        } catch (Exception e) {
            Toast.makeText(this, "Error parsing results: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(airHoursList);
        } else {
            for (AirHoursData data : airHoursList) {
                if (data.getRegNo().toLowerCase().contains(query.toLowerCase()) ||
                        data.getFlightDate().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(data);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}