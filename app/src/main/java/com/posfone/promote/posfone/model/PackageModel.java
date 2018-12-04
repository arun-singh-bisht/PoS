package com.posfone.promote.posfone.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Arun.Singh on 8/23/2018.
 */

public class PackageModel implements Parcelable {

    public String package_id;
    public String package_name;
    public String recurring_total;
    public String onetime_amount_flat;
    public String parameters_Subscription_Charge;
    public String gateway_name;
    public String gateway_transaction_fee_partner;
    public String gateway_processing_fee_partner;
    public String recurring_amount_flat_Subscription_Charge;
    public String selectedTwillioNumber;

    public String PackageName;
    public String SubscriptionCharge;
    public String OneTimeCharge;
    public String NumberCharges;
    public String TotalAmount;
    public String Vat;


    public PackageModel(Parcel in) {
        package_id = in.readString();
        package_name = in.readString();
        recurring_total = in.readString();
        onetime_amount_flat = in.readString();
        parameters_Subscription_Charge = in.readString();
        gateway_name = in.readString();
        gateway_transaction_fee_partner = in.readString();
        gateway_processing_fee_partner = in.readString();
        recurring_amount_flat_Subscription_Charge = in.readString();
        selectedTwillioNumber = in.readString();
    }

    public PackageModel() {
    }

    public static final Creator<PackageModel> CREATOR = new Creator<PackageModel>() {
        @Override
        public PackageModel createFromParcel(Parcel in) {
            return new PackageModel(in);
        }

        @Override
        public PackageModel[] newArray(int size) {
            return new PackageModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(package_id);
        parcel.writeString(package_name);
        parcel.writeString(recurring_total);
        parcel.writeString(onetime_amount_flat);
        parcel.writeString(parameters_Subscription_Charge);
        parcel.writeString(gateway_name);
        parcel.writeString(gateway_transaction_fee_partner);
        parcel.writeString(gateway_processing_fee_partner);
        parcel.writeString(recurring_amount_flat_Subscription_Charge);
        parcel.writeString(selectedTwillioNumber);
    }
}
