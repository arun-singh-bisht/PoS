package com.posfone.promote.posfone.data.remote.models;

public class BundlesModel {

    private String bundlename;
    private String bundletime;
    private String bundletimemin;
    private String baseprice;
    private String created_on;

    public BundlesModel() {
    }

    public String getBundlename() {
        return bundlename;
    }

    public void setBundlename(String bundlename) {
        this.bundlename = bundlename;
    }

    public String getBundletime() {
        return bundletime;
    }

    public void setBundletime(String bundletime) {
        this.bundletime = bundletime;
    }

    public String getBundletimemin() {
        return bundletimemin;
    }

    public void setBundletimemin(String bundletimemin) {
        this.bundletimemin = bundletimemin;
    }

    public String getBaseprice() {
        return baseprice;
    }

    public void setBaseprice(String baseprice) {
        this.baseprice = baseprice;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
