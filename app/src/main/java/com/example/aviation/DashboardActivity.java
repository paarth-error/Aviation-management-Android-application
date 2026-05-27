// ====== DashboardActivity.java ======
package com.example.aviation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private MaterialCardView airHoursCard;
    private MaterialCardView linkageAnalyzeCard;
    private MaterialCardView billingStatusCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        initializeViews();


        handleIncomingData();


        setClickListeners();
    }

    private void initializeViews() {
        airHoursCard = findViewById(R.id.airHoursCard);
        linkageAnalyzeCard = findViewById(R.id.linkageAnalyzeCard);
        billingStatusCard = findViewById(R.id.billingStatusCard);
    }

    private void handleIncomingData() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean uploadSuccess = intent.getBooleanExtra("upload_success", false);
            boolean showSuccessMessage = intent.getBooleanExtra("show_success_message", false);

            if (uploadSuccess && showSuccessMessage) {
                // Show success message
                Toast.makeText(this, "Data uploaded successfully! You can now access all features.", Toast.LENGTH_LONG).show();

                // Optional: Highlight cards or change their appearance
                highlightCardsAfterUpload();
            }
        }
    }

    private void highlightCardsAfterUpload() {
        // Optional: Add visual indication that data is ready for all features
        if (airHoursCard != null) {
            airHoursCard.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .withEndAction(() ->
                            airHoursCard.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(200)
                                    .start()
                    );
        }

        if (billingStatusCard != null) {
            // Add slight delay for visual effect
            billingStatusCard.postDelayed(() -> {
                billingStatusCard.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(200)
                        .withEndAction(() ->
                                billingStatusCard.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(200)
                                        .start()
                        );
            }, 100);
        }

        if (linkageAnalyzeCard != null) {
            // Add slight delay for visual effect
            linkageAnalyzeCard.postDelayed(() -> {
                linkageAnalyzeCard.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(200)
                        .withEndAction(() ->
                                linkageAnalyzeCard.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(200)
                                        .start()
                        );
            }, 200);
        }
    }

    private void setClickListeners() {
        // Air Hours Card Click
        airHoursCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add subtle click animation
                animateCardClick(v);

                // Check if data is available before navigating
                if (isDataAvailable()) {
                    Intent intent = new Intent(DashboardActivity.this, AirHoursActivity.class);
                    startActivity(intent);
                } else {
                    // Show dialog asking user to upload data first
                    showNoDataDialog();
                }
            }
        });

        // Linkage Analyze Card Click
        /*linkageAnalyzeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add subtle click animation
                animateCardClick(v);

                // Check if data is available for linkage analysis
                if (isDataAvailable()) {
                    // TODO: Uncomment when LinkageAnalyzeActivity is implemented
                    // Intent intent = new Intent(DashboardActivity.this, LinkageAnalyzeActivity.class);
                    // startActivity(intent);
                    Toast.makeText(DashboardActivity.this, "Linkage Analyze feature coming soon!", Toast.LENGTH_SHORT).show();
                } else {
                    showNoDataDialog();
                }
            }
        });*/

        // Billing Status Card Click
        billingStatusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add subtle click animation
                animateCardClick(v);

                // Check if data is available for billing status
                if (isBillingDataAvailable()) {
                    Intent intent = new Intent(DashboardActivity.this, BillingStatusActivity.class);
                    startActivity(intent);
                } else {
                    // Show dialog asking user to upload data first
                    showNoDataDialog();
                }
            }
        });
    }

    private void animateCardClick(View card) {
        card.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        card.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    }
                });
    }

    private boolean isDataAvailable() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Check for air hours data (main data source)
        String airHoursJson = prefs.getString("last_air_hours_json", null);
        if (airHoursJson != null && !airHoursJson.trim().isEmpty()) {
            return true;
        }

        // Alternative: Check if any file was uploaded recently
        String fileName = prefs.getString("last_uploaded_file_name", null);
        long timestamp = prefs.getLong("last_upload_timestamp", 0);

        return fileName != null && timestamp > 0;
    }

    private boolean isBillingDataAvailable() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Check specifically for billing data
        String billingJson = prefs.getString("last_billing_json", null);
        if (billingJson != null && !billingJson.trim().isEmpty()) {
            return true;
        }

        // Fallback to general data check
        return isDataAvailable();
    }

    private void showNoDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Data Available")
                .setMessage("Please upload a CSV/Excel file first to view the results.")
                .setPositiveButton("Upload File", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Navigate to upload activity
                        Intent intent = new Intent(DashboardActivity.this, UploadDataActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update card states when returning to dashboard
        updateCardStates();
    }

    private void updateCardStates() {
        boolean hasAirHoursData = isDataAvailable();
        boolean hasBillingData = isBillingDataAvailable();

        if (hasAirHoursData || hasBillingData) {
            // Enable all cards when data is available
            airHoursCard.setEnabled(true);
            linkageAnalyzeCard.setEnabled(true);
            billingStatusCard.setEnabled(true);

            // Optional: Add visual indicator for data availability
            setCardDataAvailableState(airHoursCard, hasAirHoursData);
            setCardDataAvailableState(linkageAnalyzeCard, hasAirHoursData);
            setCardDataAvailableState(billingStatusCard, hasBillingData);

            // Show last upload info
            showLastUploadInfo();
        } else {
            // Keep cards enabled but they will show dialog when clicked
            airHoursCard.setEnabled(true);
            linkageAnalyzeCard.setEnabled(true);
            billingStatusCard.setEnabled(true);

            // Reset card states
            setCardDataAvailableState(airHoursCard, false);
            setCardDataAvailableState(linkageAnalyzeCard, false);
            setCardDataAvailableState(billingStatusCard, false);
        }
    }

    private void setCardDataAvailableState(MaterialCardView card, boolean hasData) {
        if (card != null) {
            // Adjust card appearance based on data availability
            if (hasData) {
                card.setCardElevation(8f);
                card.setAlpha(1.0f);
            } else {
                card.setCardElevation(4f);
                card.setAlpha(0.8f);
            }
        }
    }

    private void showLastUploadInfo() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String fileName = prefs.getString("last_uploaded_file_name", null);
        long timestamp = prefs.getLong("last_upload_timestamp", 0);

        if (fileName != null && timestamp > 0) {
            // Log the last upload info
            android.util.Log.d("DashboardActivity", "Last uploaded file: " + fileName + " at " + new java.util.Date(timestamp));
        }
    }

    // Method to clear stored data
    public void clearStoredData() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("last_air_hours_json");
        editor.remove("last_billing_json");
        editor.remove("last_uploaded_file_name");
        editor.remove("last_uploaded_file_path");
        editor.remove("last_upload_timestamp");
        editor.apply();

        Toast.makeText(this, "Data cleared successfully", Toast.LENGTH_SHORT).show();
        updateCardStates();
    }

    // Optional: Method to add ripple effect programmatically
    private void setupCardRippleEffect() {
        // This is already handled by MaterialCardView's foreground attribute in XML
        // but you can customize it here if needed
    }
}