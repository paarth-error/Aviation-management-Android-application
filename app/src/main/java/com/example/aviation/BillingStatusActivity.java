package com.example.aviation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillingStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillingStatusAdapter adapter;
    private List<BillingStatusData> billingList;
    private List<BillingStatusData> filteredList;

    private LinearLayout loadingState;
    private TextInputEditText searchFlightNumber;
    private MaterialButton btnAll, btnBilled, btnUnbilled;
    private TextView tvBilledCount, tvUnbilledCount;

    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billingstatus);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearchFilter();

        loadBillingStatus();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_flights);
        loadingState = findViewById(R.id.loading_state);
        searchFlightNumber = findViewById(R.id.search_flight_number);
        btnAll = findViewById(R.id.btn_all);
        btnBilled = findViewById(R.id.btn_billed);
        btnUnbilled = findViewById(R.id.btn_unbilled);
        tvBilledCount = findViewById(R.id.tv_billed_count);
        tvUnbilledCount = findViewById(R.id.tv_unbilled_count);

        billingList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new BillingStatusAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            updateFilterButtons();
            filterData();
        });

        btnBilled.setOnClickListener(v -> {
            currentFilter = "FULLY_BILLED";
            updateFilterButtons();
            filterData();
        });

        btnUnbilled.setOnClickListener(v -> {
            currentFilter = "NOT_BILLED";
            updateFilterButtons();
            filterData();
        });
    }

    private void setupSearchFilter() {
        searchFlightNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void updateFilterButtons() {
        btnAll.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnBilled.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnUnbilled.setBackgroundTintList(getColorStateList(android.R.color.transparent));

        switch (currentFilter) {
            case "ALL":
                btnAll.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
                break;
            case "FULLY_BILLED":
                btnBilled.setBackgroundTintList(getColorStateList(android.R.color.holo_green_light));
                break;
            case "NOT_BILLED":
                btnUnbilled.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
                break;
        }
    }

    private void filterData() {
        filteredList.clear();
        String searchText = searchFlightNumber.getText().toString().toLowerCase().trim();

        for (BillingStatusData item : billingList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    item.getFlightNumber().toLowerCase().contains(searchText);

            boolean matchesFilter = currentFilter.equals("ALL") ||
                    (currentFilter.equals("FULLY_BILLED") && item.getBillingStatus().equalsIgnoreCase("Fully Billed")) ||
                    (currentFilter.equals("NOT_BILLED") && item.getBillingStatus().equalsIgnoreCase("Not Billed"));

            if (matchesSearch && matchesFilter) {
                filteredList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateStatistics() {
        int billedCount = 0;
        int unbilledCount = 0;

        for (BillingStatusData item : billingList) {
            if (item.getBillingStatus().equalsIgnoreCase("Fully Billed")) {
                billedCount++;
            } else if (item.getBillingStatus().equalsIgnoreCase("Not Billed")) {
                unbilledCount++;
            }
        }

        tvBilledCount.setText(String.valueOf(billedCount));
        tvUnbilledCount.setText(String.valueOf(unbilledCount));
    }

    private void loadBillingStatus() {
        loadingState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        billingList.clear();
        filteredList.clear();

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String billingJson = prefs.getString("last_billing_json", null);

        if (billingJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(billingJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    billingList.add(new BillingStatusData(
                            obj.optString("Unique Id"),
                            obj.optString("Reg No."),
                            obj.optString("Operator Name"),
                            obj.optString("Arr Bill Status"),
                            obj.optString("Dep Bill Status"),
                            obj.optString("UDF Bill Status"),
                            obj.optString("Billing Status")
                    ));
                }

                updateStatistics();
                filterData();

            } catch (Exception e) {
                Toast.makeText(this, "Error loading billing data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No billing data found. Please upload CSV first.", Toast.LENGTH_SHORT).show();
        }

        loadingState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}