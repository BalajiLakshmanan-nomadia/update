package com.synchroteam.beans;

import java.io.Serializable;

/**
 * This class holds the values for the single invoice or quotation
 *
 * @author Trident
 */
public class Invoice_Quotation_Beans implements Serializable {

    private String id;
    private int customerId;
    private int numberOfIQ;
    private String jobId;
    private int flag;
    private String dateOfCreation;
    private int clientId;
    private int siteId;
    private float totalWithoutTax;
    private float tax;
    private float totalWithTax;
    private boolean isStrictInvoice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public float getTotalWithoutTax() {
        return totalWithoutTax;
    }

    public void setTotalWithoutTax(float totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getTotalWithTax() {
        return totalWithTax;
    }

    public void setTotalWithTax(float totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    public int getNumberOfIQ() {
        return numberOfIQ;
    }

    public void setNumberOfIQ(int numberOfIQ) {
        this.numberOfIQ = numberOfIQ;
    }

    public boolean isStrictInvoice() {
        return isStrictInvoice;
    }

    public void setStrictInvoice(boolean strictInvoice) {
        isStrictInvoice = strictInvoice;
    }
}
