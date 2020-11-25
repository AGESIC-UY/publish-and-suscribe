package uy.gub.agesic.pdi.pys.backoffice.utiles.soporte;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;

import java.util.Date;

public class DateUtil {

	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static Integer currentYear() {
        return new DateTime().getYear();
    }

	public static Date parse(String dateText, String datePattern) throws BackofficeException {
		if (dateText == null || "".equals(dateText)) {
			return null;
		}
		
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
			DateTime date = formatter.parseDateTime(dateText);

			return date.toDate();
		} catch (Exception ex) {
			logger.error("Error parseando fecha", ex);
			throw new BackofficeException("Error parseando la fecha: " + dateText);
		}
	}

	public static String format(Date date, String datePattern) throws BackofficeException {
		if (date == null) {
			return "";
		}
		
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
			return new DateTime(date).toString(formatter);
		} catch (Exception ex) {
			logger.error("Error formateando fecha", ex);
			throw new BackofficeException("Error formateando fecha" + date.toString());
		}
	}

	private DateUtil() {
    }
	
}
