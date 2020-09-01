package com.template.enums;

public enum PurchaseOrderStatus {
    Submitted,
    Confirmed,
    Denied,
    Approved,
    Rejected,
    PaidByLender,
    ShipmentStarted, // need to upload supply bills
    Received, // need to upload GRN document
    Closed,
}
