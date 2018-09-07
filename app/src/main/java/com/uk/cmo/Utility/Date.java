package com.uk.cmo.Utility;

/**
 * Created by usman on 21-04-2018.
 */

public class Date {

    public static final long DAY_IN_MILLIS = 60 * 60 * 24 * 1000;

    public static final long HOUR_IN_MILLIS = 60 * 60 * 1000;

    public static final long MIN_IN_MILLIS = 60 * 1000;

    public static String ToString(long millis){

        long difference = System.currentTimeMillis() - millis;

        if (difference >= DAY_IN_MILLIS){
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            return dateFormat.format(new java.util.Date(millis));
        }else if (difference >= HOUR_IN_MILLIS){

            long number_of_hrs = difference / HOUR_IN_MILLIS;
            return number_of_hrs + " h ago";

        }else if (difference >= MIN_IN_MILLIS){

            long number_of_min = difference / MIN_IN_MILLIS;
            return number_of_min + " min ago";

        }else if (difference >= 1000){

            long number_of_sec = difference / 1000;
            return number_of_sec + " sec ago";

        }else {
            return "--";
        }

    }

    public static String getDate(long millis) {
        java.text.DateFormat dateformat = java.text.DateFormat.getDateInstance();
        return dateformat.format(new java.util.Date(millis));

    }

}
