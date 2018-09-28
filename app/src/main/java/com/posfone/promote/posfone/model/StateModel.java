package com.posfone.promote.posfone.model;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // Filter Lsit of Twillio Object Array Lsit and find elements containng some character
    private static Predicate<StateModel> stringContains(final String s) {
        return new Predicate<StateModel>() {

            public boolean apply(StateModel stateModel) {
                return stateModel.name.toLowerCase().contains(s.toLowerCase());
            }
        };
    }
    public static List<StateModel> getFilterdList(List<StateModel> stateModelList, String s)
    {
        Collection<StateModel> dataPointsCalledJohn =
                Collections2.filter(stateModelList, stringContains(s));
        List<StateModel> userList = new ArrayList<StateModel>(dataPointsCalledJohn );

        return userList;
    }
}
