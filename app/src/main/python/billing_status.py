import pandas as pd
import json

def process_billing_status_from_csv(file_path):
    try:
        df = pd.read_csv(file_path)

        # Clean and normalize the data
        df['Arr Bill Status'] = df['Arr Bill Status'].fillna('').astype(str).str.strip().str.lower()
        df['Dep Bill Status'] = df['Dep Bill Status'].fillna('').astype(str).str.strip().str.lower()
        df['UDF Bill Status'] = df['UDF Bill Status'].fillna('').astype(str).str.strip().str.lower()
        df['Operator Name'] = df['Operator Name'].fillna('').astype(str).str.strip()
        df['Unique Id'] = df['Unique Id'].fillna('').astype(str).str.strip()
        df['Reg No.'] = df['Reg No.'].fillna('').astype(str).str.strip()

        # Billing status logic
        def get_billing_status(row):
            arr = row['Arr Bill Status']
            dep = row['Dep Bill Status']
            udf = row['UDF Bill Status']

            if all(s in ['billed', 'paid', 'done'] for s in [arr, dep, udf]):
                return 'Fully Billed'
            elif any(s in ['billed', 'paid', 'done'] for s in [arr, dep, udf]):
                return 'Partially Billed'
            elif all(s == '' for s in [arr, dep, udf]):
                return 'Missing Billing Info'
            else:
                return 'Not Billed'

        # Apply billing status
        df['Billing Status'] = df.apply(get_billing_status, axis=1)

        # Prepare list of dictionaries for output
        result = []
        for _, row in df.iterrows():
            result.append({
                "Unique Id": row['Unique Id'],
                "Reg No.": row['Reg No.'],
                "Operator Name": row['Operator Name'],
                "Arr Bill Status": row['Arr Bill Status'],
                "Dep Bill Status": row['Dep Bill Status'],
                "UDF Bill Status": row['UDF Bill Status'],
                "Billing Status": row['Billing Status']
            })

        return json.dumps(result)

    except Exception as e:
        return json.dumps({"error": str(e)})
