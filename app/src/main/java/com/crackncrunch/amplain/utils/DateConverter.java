package com.crackncrunch.amplain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
            ConstantsManager.SERVER_DATE_FORMAT, Locale.US);

    public static Date stringToDate(String string) throws ParseException {
        return string == null ? new Date() : timeFormat.parse(string);
    }

    public static String dateToString(Date date) {
        return timeFormat.format(date);
    }
}
