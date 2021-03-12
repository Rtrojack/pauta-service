package br.com.trojack.pauta.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN);

    public static String format(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATER.format(zonedDateTime);
    }
}