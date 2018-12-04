package com.posfone.promote.posfone.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table(name = "Country")
public class CountryModel extends BaseModel {


    @Column(name = "LocalId")
    @SerializedName("id")
    public String localId;

    @Column(name = "iso")
    @SerializedName("iso")
    public  String iso;

    @Column(name = "name")
    @SerializedName("name")
    public  String name;

    @Column(name = "phonecode")
    @SerializedName("phonecode")
    public  String phonecode;

    @Column(name = "flag")
    @SerializedName("flag")
    public String flag;


    public CountryModel() {
    }

    public CountryModel(String localId, String iso, String name, String phonecode, String flag) {
        this.localId = localId;
        this.iso = iso;
        this.name = name;
        this.phonecode = phonecode;
        this.flag = flag;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }


    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }


}
