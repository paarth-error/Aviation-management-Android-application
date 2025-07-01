package com.example.aviation;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class FlightHoursActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_hours);

        container = findViewById(R.id.flightHoursContainer);

        String jsonData = getIntent().getStringExtra("flight_hours_json");

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String date = obj.getString("Flight Date");
                String regNo = obj.getString("Reg No.");
                double hours = obj.getDouble("Air Hours");
                String status = obj.getString("Status");

                TextView tv = new TextView(this);
                tv.setText(regNo + " - " + String.format("%.1f", hours) + " hrs");
                tv.setTextSize(18);
                tv.setPadding(30, 20, 30, 20);
                tv.setTextColor(Color.WHITE);

                switch (status) {
                    case "Red": tv.setBackgroundColor(Color.parseColor("#FF4D4D")); break;
                    case "Yellow": tv.setBackgroundColor(Color.parseColor("#FFD700")); break;
                    case "Green": tv.setBackgroundColor(Color.parseColor("#4CAF50")); break;
                }

                container.addView(tv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}