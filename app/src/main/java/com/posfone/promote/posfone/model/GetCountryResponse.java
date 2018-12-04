package com.posfone.promote.posfone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCountryResponse {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CountryModel> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryModel> countries) {
        this.countries = countries;
    }

    @SerializedName("status")
    public String status;

    @SerializedName("countries")
    public List<CountryModel> countries;

    public GetCountryResponse(String status, List<CountryModel> countries) {
        this.status = status;
        this.countries = countries;
    }
}
