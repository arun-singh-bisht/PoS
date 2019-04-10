package com.posfone.promote.posfone.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.posfone.promote.posfone.data.local.models.StateModel;

import java.util.List;

public class GetStateResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("statelist")
    public List<StateModel> statelist;

    @SerializedName("message")
    public String message;


    public GetStateResponse(String status, List<StateModel> statelist) {
        this.status = status;
        this.statelist = statelist;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StateModel> getStatelist() {
        return statelist;
    }

    public void setStatelist(List<StateModel> statelist) {
        this.statelist = statelist;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
