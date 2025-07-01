import pandas as pd
from datetime import datetime, timedelta

def gmt_to_datetime(date_str, time_val):
    try:
        date = pd.to_datetime(date_str, dayfirst=True, errors='coerce')
        time_str = str(int(time_val)).zfill(4)
        time = datetime.strptime(time_str, "%H%M").time()
        dt = datetime.combine(date.date(), time)
        return dt + timedelta(hours=5, minutes=30)
    except:
        return pd.NaT

def process_flight_hours(csv_path):
    df = pd.read_csv(csv_path)

    df['Reg No.'] = df['Reg No.'].astype(str).str.strip()
    df['Dep Location'] = df['Dep Location'].astype(str).str.strip()
    df['Dest Location'] = df['Dest Location'].astype(str).str.strip()
    df['Arr Flight No.'] = df['Arr Flight No.'].astype(str).str.strip()
    df['Dep Flight No.'] = df['Dep Flight No.'].astype(str).str.strip()

    df['Arr IST'] = df.apply(lambda row: gmt_to_datetime(row['Arr Date'], row['Arr GMT']), axis=1)
    df['Dep IST'] = df.apply(lambda row: gmt_to_datetime(row['Dep Date'], row['Dep GMT']), axis=1)

    df['Air Hours'] = (df['Dep IST'] - df['Arr IST']).dt.total_seconds() / 3600
    df['Flight Date'] = pd.to_datetime(df['Arr IST']).dt.date

    daily_airtime = df.groupby(['Flight Date', 'Reg No.'])['Air Hours'].sum().reset_index()

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

    return daily_airtime.to_json(orient='records')
