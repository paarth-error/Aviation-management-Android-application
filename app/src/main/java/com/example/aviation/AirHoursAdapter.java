package com.example.aviation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class AirHoursAdapter extends RecyclerView.Adapter<AirHoursAdapter.AirHoursViewHolder> {

    private List<AirHoursData> airHoursList;
    private DecimalFormat decimalFormat;

    public AirHoursAdapter(List<AirHoursData> airHoursList) {
        this.airHoursList = airHoursList;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public AirHoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_air_hour, parent, false);
        return new AirHoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AirHoursViewHolder holder, int position) {
        AirHoursData data = airHoursList.get(position);

        holder.textFlightDate.setText(data.getFlightDate());
        holder.textRegNo.setText(data.getRegNo());
        holder.textAirHours.setText(decimalFormat.format(data.getAirHours()) + " hrs");
        holder.textStatus.setText(data.getStatus());

        // Set status color based on status
        Context context = holder.itemView.getContext();
        int statusColor;

        switch (data.getStatus().toLowerCase()) {
            case "red":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                break;
            case "yellow":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
                break;
            case "green":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
                break;
            default:
                statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                break;
        }

        holder.textStatus.setTextColor(statusColor);

        // Set alternating row colors for better visibility
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.background_light));
        }
    }

    @Override
    public int getItemCount() {
        return airHoursList.size();
    }

    public static class AirHoursViewHolder extends RecyclerView.ViewHolder {
        TextView textFlightDate, textRegNo, textAirHours, textStatus;

        public AirHoursViewHolder(@NonNull View itemView) {
            super(itemView);
            textFlightDate = itemView.findViewById(R.id.textFlightDate);
            textRegNo = itemView.findViewById(R.id.textRegNo);
            textAirHours = itemView.findViewById(R.id.textAirHours);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}