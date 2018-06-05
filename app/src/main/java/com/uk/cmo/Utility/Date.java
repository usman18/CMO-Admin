package com.uk.cmo.Utility;

/**
 * Created by usman on 21-04-2018.
 */

public class Date {

    public static String ToString(long millis){

        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
    return dateFormat.format(new java.util.Date(millis));
    }
}
