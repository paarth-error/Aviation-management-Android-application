package com.example.aviation;

public class BillingStatusData {
    private String flightNumber;
    private String regno;
    private String operator;

    private String arrStatus;
    private String depStatus;
    private String udfStatus;
    private String billingStatus;

    public BillingStatusData(String flightNumber, String aircraftInfo, String operator,
                              String arrStatus, String depStatus,
                             String udfStatus, String billingStatus) {
        this.flightNumber = flightNumber;
        this.regno = aircraftInfo;
        this.operator = operator;
        this.arrStatus = arrStatus;
        this.depStatus = depStatus;
        this.udfStatus = udfStatus;
        this.billingStatus = billingStatus;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getRegno() {
        return regno;
    }

    public String getOperator() {
        return operator;
    }


    public String getArrStatus() {
        return arrStatus;
    }

    public String getDepStatus() {
        return depStatus;
    }

    public String getUdfStatus() {
        return udfStatus;
    }

    public String getBillingStatus() {
        return billingStatus;
    }
}