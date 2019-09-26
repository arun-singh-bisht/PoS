package com.posfone.promote.posfone.data.remote.models;

import com.posfone.promote.posfone.data.local.models.Charge;

import java.util.ArrayList;
import java.util.List;

public class RenewSubscriptionModel {
    String package_gateway;
    String call_bundle;
    String package_contract;
    String package_pay729_number;
    public ArrayList<Charge> charge;
    String due_sub_date;
    String total_sub_date;
    String total_monthly_cost;
    String total_sub_cost_till_date;
    String vat;
    String total_chargeable_amount;

    public ArrayList<Charge> getCharge() {
        return charge;
    }

    public void setCharge(ArrayList<Charge> charge) {
        this.charge = charge;
    }

    public String getDue_sub_date() {
        return due_sub_date;
    }

    public void setDue_sub_date(String due_sub_date) {
        this.due_sub_date = due_sub_date;
    }

    public String getTotal_sub_date() {
        return total_sub_date;
    }

    public void setTotal_sub_date(String total_sub_date) {
        this.total_sub_date = total_sub_date;
    }

    public String getTotal_monthly_cost() {
        return total_monthly_cost;
    }

    public void setTotal_monthly_cost(String total_monthly_cost) {
        this.total_monthly_cost = total_monthly_cost;
    }

    public String getTotal_sub_cost_till_date() {
        return total_sub_cost_till_date;
    }

    public void setTotal_sub_cost_till_date(String total_sub_cost_till_date) {
        this.total_sub_cost_till_date = total_sub_cost_till_date;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getTotal_chargeable_amount() {
        return total_chargeable_amount;
    }

    public void setTotal_chargeable_amount(String total_chargeable_amount) {
        this.total_chargeable_amount = total_chargeable_amount;
    }

    public String getCall_bundle() {
        return call_bundle;
    }

    public void setCall_bundle(String call_bundle) {
        this.call_bundle = call_bundle;
    }

    public String getPackage_contract() {
        return package_contract;
    }

    public void setPackage_contract(String package_contract) {
        this.package_contract = package_contract;
    }

    public String getPackage_pay729_number() {
        return package_pay729_number;
    }

    public void setPackage_pay729_number(String package_pay729_number) {
        this.package_pay729_number = package_pay729_number;
    }


    public String getPackage_gateway() {
        return package_gateway;
    }

    public void setPackage_gateway(String package_gateway) {
        this.package_gateway = package_gateway;
    }


}




