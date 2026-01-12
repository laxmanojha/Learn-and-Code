package frontend.newsaggregation.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return LocalDateTime.now().format(formatter);
    }

    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        return LocalDateTime.now().format(formatter);
    }
}
