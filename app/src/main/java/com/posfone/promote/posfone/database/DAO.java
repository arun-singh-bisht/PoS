package com.posfone.promote.posfone.database;

import com.activeandroid.query.Select;
import com.posfone.promote.posfone.model.CountryModel;

import java.util.List;

public class DAO {

    public static List<CountryModel> getAllCountry() {
        return new Select()
                .from(CountryModel.class)
                .orderBy("Name ASC")
                .execute();
    }

    public static List<CountryModel> getAllCountry(String containsCharater) {
        return new Select()
                .from(CountryModel.class)
                .where("Name LIKE ?", new String[]{'%' + containsCharater + '%'})
                .orderBy("Name ASC")
                .execute();
    }
}
