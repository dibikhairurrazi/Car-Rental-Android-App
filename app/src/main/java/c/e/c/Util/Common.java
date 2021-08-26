package c.e.c.Util;

import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static String getFormattedPrice(double amount) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return numberFormat.format(amount);
    }

    public static long getDayDifference(Calendar start, Calendar end){
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant())+1;
    }
}
