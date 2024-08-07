package utilities;

import controllers.Util;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Integer getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static long remainingTime(Date date, int duration) {
        if(date == null) return 0;
        LocalDateTime endDate = LocalDateTime.ofInstant(DateUtils.addHours(date, duration).toInstant(), ZoneId.systemDefault());
        LocalDateTime currentDate = LocalDateTime.now();
        long numberOfHours = Duration.between(currentDate, endDate).toHours();
        if(numberOfHours < 0)
            return 0;
        return numberOfHours;
    }

    public static String currentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // current time
        return sdf.format(c.getTime());
    }

    public static Date currentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        Date date=null;
        try {
            date = sdf.parse(sdf.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");
        Date date = null;
        try {
            if(Util.isNotEmpty(dateString)) {
                date = sdf.parse(dateString);
            } else {
                return null;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToDate(String dateString, String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if(Util.isNotEmpty(dateString)) {
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static Date weekStart() {
        Date weekStart = Date.from(LocalDate.now().minusDays(7).with(DayOfWeek.SUNDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return weekStart;
    }

    public static Date weekEnd() {
        Date weekEnd = Date.from(LocalDate.now().with(DayOfWeek.SATURDAY).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return weekEnd;
    }
}

