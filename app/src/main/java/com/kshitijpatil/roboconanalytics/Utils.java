package com.kshitijpatil.roboconanalytics;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String getTimeDiff(long notifyTime) {
        //SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date notifyDate = new Date(notifyTime);

        Calendar c = Calendar.getInstance();
        Date curDate = c.getTime();
        long different = curDate.getTime() - notifyDate.getTime();
        //long diffSecs= TimeUnit.MILLISECONDS.toSeconds(diffMillis);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        String timeNotification = new String();
        if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0)
            timeNotification = "Moments Ago";
        else if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes != 0){
            if(elapsedMinutes==1)
                timeNotification = elapsedMinutes + " minute ago";
            else
                timeNotification = elapsedMinutes + " minutes ago";
        }

        else if (elapsedDays == 0 && elapsedHours != 0){
            if(elapsedHours==1)
                timeNotification = elapsedHours + " hour ago";
            else
                timeNotification = elapsedHours + " hours ago";
        }

        else if (elapsedDays != 0){
            if(elapsedDays==1)
                timeNotification = elapsedDays + " day ago";
            else
                timeNotification = elapsedDays + " days ago";
        }


        return timeNotification;
    }
}
