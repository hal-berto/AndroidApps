package it.androidapp.secretsanta.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateConverterUtil {

    private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public static String dateToString(Date d){
        try {
            return df.format(d);
        } catch(Exception e){
            return "";
        }
    }

    public static Date stringToDate(String dateString){
        try {
            return df.parse(dateString);
        } catch(Exception e){
            return null;
        }
    }
}
