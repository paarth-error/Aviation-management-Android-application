package com.example.aviation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button airHoursButton;
    private Button linkageAnalyzeButton;
    private Button billingStatusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize buttons
        initializeViews();

        // Set click listeners
        //setClickListeners();
    }

    private void initializeViews() {
        airHoursButton = findViewById(R.id.airHoursButton);
        linkageAnalyzeButton = findViewById(R.id.linkageAnalyzeButton);
        billingStatusButton = findViewById(R.id.billingStatusButton);
    }}