package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import java.util.Calendar;
import java.util.Date;

public class DateFilterUtil {

    private DateFilterUtil() {
        //Nothing to do
    }

    public static Date getToDate(Date fechaHasta) {
        if (fechaHasta != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaHasta);

            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                fechaHasta = cal.getTime();
            } else {
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                fechaHasta = cal.getTime();
            }
        }
        return fechaHasta;
    }

}
