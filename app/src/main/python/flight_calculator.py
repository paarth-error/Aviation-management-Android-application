import pandas as pd
from datetime import datetime, timedelta
import json
import os # Import os to check file existence

def gmt_to_datetime(date_str, time_val):
    try:
        date = pd.to_datetime(date_str, dayfirst=True, errors='coerce')
        time_str = str(int(time_val)).zfill(4)
        time = datetime.strptime(time_str, "%H%M").time()
        dt = datetime.combine(date.date(), time)
        return dt + timedelta(hours=5, minutes=30) # Add IST offset
    except Exception as e:
        print(f"Chaquopy Python Error in gmt_to_datetime for date_str='{date_str}', time_val='{time_val}': {e}")
        return pd.NaT

def process_flight_hours(csv_path):
    print(f"Chaquopy Python: Attempting to process CSV at: {csv_path}")
    if not os.path.exists(csv_path):
        print(f"Chaquopy Python Error: CSV file not found at {csv_path}")
        return json.dumps({"error": f"CSV file not found at {csv_path}"})

    try:
        # Determine file type based on extension
        file_extension = os.path.splitext(csv_path)[1].lower()
        df = None
        if file_extension == '.csv':
            df = pd.read_csv(csv_path)
        elif file_extension == '.xlsx' or file_extension == '.xls':
            # This requires 'openpyxl' to be installed in Chaquopy build.gradle
            df = pd.read_excel(csv_path)
        else:
            return json.dumps({"error": f"Unsupported file type: {file_extension}. Only .csv and .xlsx/.xls are supported."})


        # Ensure columns are treated as strings before stripping
        df['Reg No.'] = df['Reg No.'].fillna('').astype(str).str.strip()
        df['Dep Location'] = df['Dep Location'].fillna('').astype(str).str.strip()
        df['Dest Location'] = df['Dest Location'].fillna('').astype(str).str.strip()
        df['Arr Flight No.'] = df['Arr Flight No.'].fillna('').astype(str).str.strip()
        df['Dep Flight No.'] = df['Dep Flight No.'].fillna('').astype(str).str.strip()

        # Apply gmt_to_datetime and handle potential errors
        df['Arr IST'] = df.apply(lambda row: gmt_to_datetime(row.get('Arr Date'), row.get('Arr GMT')), axis=1)
        df['Dep IST'] = df.apply(lambda row: gmt_to_datetime(row.get('Dep Date'), row.get('Dep GMT')), axis=1)

        # Filter out rows where either Arr IST or Dep IST is NaT
        df_valid_times = df.dropna(subset=['Arr IST', 'Dep IST'])

        if df_valid_times.empty:
            print("Chaquopy Python: No valid flight entries after time conversion.")
            return json.dumps({"message": "No valid flight entries found for calculation."})

        # Calculate Air Hours only for valid time entries
        df_valid_times['Air Hours'] = (df_valid_times['Dep IST'] - df_valid_times['Arr IST']).dt.total_seconds() / 3600
        df_valid_times['Flight Date'] = pd.to_datetime(df_valid_times['Arr IST']).dt.date

        # Group by Flight Date and Reg No.
        daily_airtime = df_valid_times.groupby(['Flight Date', 'Reg No.'])['Air Hours'].sum().reset_index()

        def air_status(hours):
            if pd.isna(hours):
                return 'Missing'
            elif hours < 10:
                return 'Red'
            elif 10 <= hours <= 14:
                return 'Yellow'
            else:
                return 'Green'

        daily_airtime['Status'] = daily_airtime['Air Hours'].apply(air_status)

        # Convert 'Flight Date' to string for JSON serialization
        daily_airtime['Flight Date'] = daily_airtime['Flight Date'].astype(str)

        print("Chaquopy Python: Successfully processed flight hours.")
        return daily_airtime.to_json(orient='records')
    except Exception as e:
        print(f"Chaquopy Python Error in process_flight_hours: {e}")
        # Return a JSON object with an error message
        return json.dumps({"error": f"Error processing CSV: {e}"})