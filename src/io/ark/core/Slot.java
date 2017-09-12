/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ark.core;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Mastadon
 */
public class Slot {

    private static DateTime beginEpoch;

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        beginEpoch = f.parseDateTime("2017-03-21 13:00:00");
    }

    public static int getTime(DateTime datetime){
    if(datetime == null)
      datetime = new DateTime();

    return new Long((datetime.getMillis() - beginEpoch.getMillis())/1000).intValue();

  }
}
