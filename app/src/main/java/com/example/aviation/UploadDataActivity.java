package com.example.aviation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadDataActivity extends AppCompatActivity {
    private static final String TAG = "UploadDataActivity";

    private Button uploadButton;
    private ProgressBar progressBar;
    private static final int FILE_PICKER_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;

    // --- SharedPreferences Constants ---
    private static final String PREFS_FILE_NAME = "app_prefs";
    private static final String LAST_UPLOADED_CSV_PATH_KEY = "last_uploaded_csv_path";

    // Executor for background tasks
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_upload);
            Log.d(TAG, "Layout set successfully");

            // Initialize the Upload button
            uploadButton = findViewById(R.id.uploadButton);
            if (uploadButton == null) {
                Log.e(TAG, "uploadButton not found in layout!");
                Toast.makeText(this, "Upload button not found", Toast.LENGTH_LONG).show();
                return;
            }

            // Initialize progress bar - make it optional in case it doesn't exist


            // Initialize Chaquopy
            initializePython();

            // Set click listener for the Upload button
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Upload button clicked");
                    try {
                        // Check for storage permission
                        if (checkStoragePermission()) {
                            openFilePicker();
                        } else {
                            requestStoragePermission();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in upload button click: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(UploadDataActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error initializing upload screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializePython() {
        try {
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
                Log.d(TAG, "Python initialized successfully");
            } else {
                Log.d(TAG, "Python already initialized");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Python: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error initializing Python: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkStoragePermission() {
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE for file picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        // For Android 11+ (API 30+), check if we have permission or if scoped storage is being used
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return true;
        }
        // For older Android versions, check READ_EXTERNAL_STORAGE permission
        else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        // Only request permission for older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
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
        new AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("This app needs storage permission to access and upload your CSV/Excel files.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    ActivityCompat.requestPermissions(UploadDataActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(UploadDataActivity.this, "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(intent, "Select CSV or Excel file"),
                        FILE_PICKER_REQUEST_CODE);
            } else {
                openDocumentPicker();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error opening file picker: " + ex.getMessage());
            Toast.makeText(this, "Error opening file picker: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
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
            Log.e(TAG, "No file manager app found: " + ex.getMessage());
            Toast.makeText(this, "Please install a file manager app capable of opening documents.", Toast.LENGTH_LONG).show();
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

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    handleSelectedFile(selectedFileUri);
                }
            }
        }
    }

    private void handleSelectedFile(Uri fileUri) {
        String fileName = getFileName(fileUri);
        Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_LONG).show();

        // Show progress bar and disable button
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        uploadButton.setEnabled(false);

        processUploadedFile(fileUri, fileName);
    }

    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath() != null ? uri.getPath() : "Unknown").getName();
        }
        return fileName;
    }

    private void processUploadedFile(Uri fileUri, String originalFileName) {
        Toast.makeText(this, "Processing " + originalFileName + "...", Toast.LENGTH_SHORT).show();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Determine file extension from the original file name
                    String fileExtension = "";
                    int dotIndex = originalFileName.lastIndexOf('.');
                    if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                        fileExtension = originalFileName.substring(dotIndex).toLowerCase();
                    }

                    // Save to app's internal files directory with its original extension
                    final String targetFileName = "uploaded_data" + fileExtension;
                    final File outputFile = new File(getFilesDir(), targetFileName);

                    try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
                         FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        if (inputStream != null) {
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, read);
                            }
                        } else {
                            throw new IOException("Could not open input stream from URI.");
                        }
                    }

                    final String savedPath = outputFile.getAbsolutePath();
                    saveLastUploadedCsvPath(savedPath);

                    // Call Python script for air hours calculation
                    Python py = Python.getInstance();
                    PyObject airHoursModule = py.getModule("flight_calculator");
                    Log.d(TAG, "Calling Python function process_flight_hours with path: " + savedPath);
                    PyObject pyResult = airHoursModule.callAttr("process_flight_hours", savedPath);

                    final String airHoursResultJson = pyResult.toString();
                    Log.d(TAG, "Python air hours result JSON: " + airHoursResultJson);

                    // Call Python script for billing status calculation from separate module
                    // Replace "your_billing_module" with the actual name of your Python file
                    PyObject billingModule = py.getModule("billing_status");
                    Log.d(TAG, "Calling Python function process_billing_status with path: " + savedPath);
                    PyObject billingResult = billingModule.callAttr("process_billing_status_from_csv", savedPath);
                    final String billingResultJson = billingResult.toString();
                    Log.d(TAG, "Python billing result JSON: " + billingResultJson);

                    // Save both results to SharedPreferences
                    SharedPreferences sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("last_air_hours_json", airHoursResultJson);
                    editor.putString("last_billing_json", billingResultJson);
                    editor.putString("last_uploaded_file_name", originalFileName);
                    editor.putString("last_uploaded_file_path", savedPath);
                    editor.putLong("last_upload_timestamp", System.currentTimeMillis());
                    editor.apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (airHoursResultJson == null || airHoursResultJson.trim().isEmpty()) {
                                Log.e(TAG, "airHoursResultJson is null or empty!");
                                onFileProcessingComplete(false, originalFileName, "No result from Python script.");
                            } else {
                                handlePythonResult(airHoursResultJson, originalFileName);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error during file processing: " + e.getMessage());
                    final String errorMessage = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFileProcessingComplete(false, originalFileName, "Error: " + errorMessage);
                        }
                    });
                }
            }
        });
    }

    private void handlePythonResult(String jsonResult, String originalFileName) {
        try {
            if (jsonResult.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonResult);
                if (jsonObject.has("error")) {
                    String errorMessage = jsonObject.getString("error");
                    onFileProcessingComplete(false, originalFileName, errorMessage);
                } else if (jsonObject.has("message")) {
                    String message = jsonObject.getString("message");
                    onFileProcessingComplete(true, originalFileName, message);
                }
            } else if (jsonResult.trim().startsWith("[")) {
                JSONArray resultsArray = new JSONArray(jsonResult);
                if (resultsArray.length() > 0) {
                    StringBuilder airHoursSummary = new StringBuilder();
                    airHoursSummary.append("Flight Air Hours Summary:\n\n");

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject flightData = resultsArray.getJSONObject(i);
                        String flightDate = flightData.getString("Flight Date");
                        String regNo = flightData.getString("Reg No.");
                        double airHours = flightData.getDouble("Air Hours");
                        String status = flightData.getString("Status");

                        airHoursSummary.append("Date: ").append(flightDate).append("\n");
                        airHoursSummary.append("Reg No.: ").append(regNo).append("\n");
                        airHoursSummary.append("Air Hours: ").append(String.format("%.2f", airHours)).append(" hours\n");
                        airHoursSummary.append("Status: ").append(status).append("\n\n");
                    }
                    onFileProcessingComplete(true, originalFileName, airHoursSummary.toString());
                } else {
                    onFileProcessingComplete(true, originalFileName, "No flight data found or processed.");
                }
            } else {
                onFileProcessingComplete(false, originalFileName, "Unknown result format from Python.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing Python JSON result: " + e.getMessage());
            onFileProcessingComplete(false, originalFileName, "Error parsing analysis results: " + e.getMessage());
        }
    }

    private void onFileProcessingComplete(boolean success, String fileName, String message) {
        // Hide progress bar and re-enable button
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        uploadButton.setEnabled(true);

        if (success) {
            Toast.makeText(this, "✅ " + fileName + " processed successfully!", Toast.LENGTH_LONG).show();

            // Show success dialog and navigate to dashboard
            new AlertDialog.Builder(this)
                    .setTitle("Upload Successful")
                    .setMessage("File processed successfully! You can now view the results from the Dashboard.")
                    .setPositiveButton("Go to Dashboard", (dialog, which) -> {
                        navigateToDashboard();
                    })
                    .setNegativeButton("Stay Here", (dialog, which) -> {
                        // Just dismiss the dialog
                    })
                    .show();
        } else {
            Toast.makeText(this, "❌ Failed to process " + fileName + ". " + message, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToDashboard() {
        try {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("upload_success", true);
            intent.putExtra("show_success_message", true);
            startActivity(intent);
            Log.d(TAG, "Navigation to Dashboard successful!");
        } catch (Exception e) {
            Log.e(TAG, "Navigation to Dashboard failed: " + e.getMessage());
            Toast.makeText(this, "Error navigating to Dashboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLastUploadedCsvPath(String path) {
        SharedPreferences sharedPrefs = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(LAST_UPLOADED_CSV_PATH_KEY, path);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}