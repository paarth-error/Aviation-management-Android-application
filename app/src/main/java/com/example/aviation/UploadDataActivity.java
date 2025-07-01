package com.example.aviation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UploadDataActivity extends AppCompatActivity {

    private Button uploadButton;
    private static final int FILE_PICKER_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize the Upload button
        uploadButton = findViewById(R.id.uploadButton);

        // Set click listener for the Upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for storage permission
                if (checkStoragePermission()) {
                    openFilePicker();
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private boolean checkStoragePermission() {
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE for file picker
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return true; // File picker works without explicit storage permission on Android 13+
        }
        // For Android 11+ (API 30+), check if we have permission or if scoped storage is being used
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return true; // Scoped storage allows file picker without explicit permission
        }
        // For older Android versions, check READ_EXTERNAL_STORAGE permission
        else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        // Only request permission for older Android versions
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show explanation dialog
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            // For newer versions, directly open file picker
            openFilePicker();
        }
    }

    private void showPermissionExplanationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("This app needs storage permission to access and upload your CSV/Excel files.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(this, "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void openFilePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");

            // Allow multiple MIME types for CSV and Excel files
            String[] mimeTypes = {
                    "text/csv",
                    "text/comma-separated-values",
                    "application/csv",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Add these flags for better compatibility
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(intent, "Select CSV or Excel file"),
                        FILE_PICKER_REQUEST_CODE);
            } else {
                // Fallback to document picker
                openDocumentPicker();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error opening file picker: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            // Try alternative method
            openDocumentPicker();
        }
    }

    private void openDocumentPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            String[] mimeTypes = {
                    "text/csv",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a file manager app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Storage permission is required to select files",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    // Handle the selected file
                    handleSelectedFile(selectedFileUri);
                }
            }
        }
    }

    private void handleSelectedFile(Uri fileUri) {
        // Get the file name
        String fileName = getFileName(fileUri);

        // Show success message
        Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_LONG).show();

        // Process the uploaded file and navigate to dashboard
        processUploadedFile(fileUri, fileName);
    }

    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        if (uri.getScheme().equals("content")) {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return fileName;
    }

    private void processUploadedFile(Uri fileUri, String fileName) {
        // Show uploading message
        Toast.makeText(this, "Processing " + fileName + "...", Toast.LENGTH_SHORT).show();

        // Simulate file processing with a delay (replace with actual processing)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Implement your actual file processing logic here
                // This is where you would:
                // 1. Read the CSV/Excel file
                // 2. Parse the data
                // 3. Validate the data
                // 4. Send it to your server or process it locally

                // For now, simulate successful processing
                onFileProcessingComplete(true, fileName);
            }
        }, 2000); // 2 second delay to simulate processing
    }

    private void onFileProcessingComplete(boolean success, String fileName) {
        if (success) {
            // Show success message
            Toast.makeText(this, "✅ " + fileName + " uploaded successfully!",
                    Toast.LENGTH_LONG).show();

            // Navigate to dashboard after successful upload
            navigateToDashboard();
        } else {
            // Show error message
            Toast.makeText(this, "❌ Failed to upload " + fileName + ". Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToDashboard() {
        // Add a small delay to let the user see the success message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(UploadDataActivity.this, DashboardActivity.class);

                // Optional: Pass data to dashboard if needed
                intent.putExtra("upload_success", true);
                intent.putExtra("timestamp", System.currentTimeMillis());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                // Finish current activity so user can't go back to upload screen
                finish();
            }
        }, 1500); // 1.5 second delay to show success message
    }

}