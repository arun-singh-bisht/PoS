package com.posfone.promote.posfone.model;


public class StateModel  {

    public String localId;
    public String country_id;
    public String name;
    public String geo_lat;
    public String geo_lng;

    public StateModel() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeo_lat() {
        return geo_lat;
    }

    public void setGeo_lat(String geo_lat) {
        this.geo_lat = geo_lat;
    }

    public String getGeo_lng() {
        return geo_lng;
    }

    public void setGeo_lng(String geo_lng) {
        this.geo_lng = geo_lng;
    }
}
