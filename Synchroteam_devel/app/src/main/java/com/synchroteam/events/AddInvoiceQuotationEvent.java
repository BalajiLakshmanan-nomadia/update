package com.synchroteam.events;

public class AddInvoiceQuotationEvent {
    public String inventoryId;
    public boolean isSynch;


    public AddInvoiceQuotationEvent(String inventoryId, boolean isSynch) {
        this.inventoryId = inventoryId;
        this.isSynch = isSynch;
    }
}
