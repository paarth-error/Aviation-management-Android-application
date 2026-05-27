package com.example.aviation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BillingStatusAdapter extends RecyclerView.Adapter<BillingStatusAdapter.ViewHolder> {

    private List<BillingStatusData> billingList;

    public BillingStatusAdapter(List<BillingStatusData> billingList) {
        this.billingList = billingList;
    }

    @NonNull
    @Override
    public BillingStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight_billing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingStatusAdapter.ViewHolder holder, int position) {
        BillingStatusData data = billingList.get(position);

        holder.tvFlightNumber.setText("Flight: " + data.getFlightNumber());
        holder.tvRegno.setText("Aircraft: " + data.getRegno());
        holder.tvOperator.setText("Operator: " + data.getOperator());
        holder.tvArrStatus.setText(data.getArrStatus());
        holder.tvDepStatus.setText(data.getDepStatus());
        holder.tvUdfStatus.setText(data.getUdfStatus());
        holder.tvBillingStatus.setText(data.getBillingStatus().toUpperCase());

        // Optionally, you can change background colors based on billing status here
    }

    @Override
    public int getItemCount() {
        return billingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlightNumber, tvRegno, tvOperator;
        TextView tvArrStatus, tvDepStatus, tvUdfStatus, tvBillingStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlightNumber = itemView.findViewById(R.id.tv_flight_number);
            tvRegno = itemView.findViewById(R.id.tv_regno);
            tvOperator = itemView.findViewById(R.id.tv_operator_name);

            tvArrStatus = itemView.findViewById(R.id.tv_arr_status);
            tvDepStatus = itemView.findViewById(R.id.tv_dep_status);
            tvUdfStatus = itemView.findViewById(R.id.tv_udf_status);
            tvBillingStatus = itemView.findViewById(R.id.tv_billing_status);
        }
    }
}