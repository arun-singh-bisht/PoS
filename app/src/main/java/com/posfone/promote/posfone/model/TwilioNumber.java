package com.posfone.promote.posfone.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TwilioNumber {

    public String phone_number;
    public String friendly_number;
    public String type;
    public boolean voice;
    public boolean SMS;
    public boolean MMS;



    // Filter Lsit of Twillio Object Array Lsit and find elements containng some character
    private static Predicate<TwilioNumber> stringContains(final String s) {
        return new Predicate<TwilioNumber>() {

            public boolean apply(TwilioNumber twilioNumber) {
                return twilioNumber.phone_number.contains(s);
            }
        };
    }
    public static List<TwilioNumber> getFilterdList(List<TwilioNumber> twilioNumberList, String s)
    {
        Collection<TwilioNumber> dataPointsCalledJohn =
                Collections2.filter(twilioNumberList, stringContains(s));
        List<TwilioNumber> userList = new ArrayList<TwilioNumber>(dataPointsCalledJohn );

        return userList;
    }

}
