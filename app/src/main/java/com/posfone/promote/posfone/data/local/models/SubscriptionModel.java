package com.posfone.promote.posfone.data.local.models;

public class SubscriptionModel {
            String  id;
            String subscription_id;
            String transaction_id;
            String invoice_id;
            String start_date;
            String end_date;
            String entry_date;
            String is_manual;
            String renewed;
            String created_on;
            String is_active;
            String package_name;
            String invoice_amount;
    String invoice_body;
    String status;
    String currency_code;
    String currency_rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getIs_manual() {
        return is_manual;
    }

    public void setIs_manual(String is_manual) {
        this.is_manual = is_manual;
    }

    public String getRenewed() {
        return renewed;
    }

    public void setRenewed(String renewed) {
        this.renewed = renewed;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getInvoice_body() {
        return invoice_body;
    }

    public void setInvoice_body(String invoice_body) {
        this.invoice_body = invoice_body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_rate() {
        return currency_rate;
    }

    public void setCurrency_rate(String currency_rate) {
        this.currency_rate = currency_rate;
    }


}
