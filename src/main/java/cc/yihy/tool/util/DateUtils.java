package cc.yihy.tool.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jianyun.zhao
 * Create at 2018/11/22
 */
public class DateUtils {

    private static Map<String, DateTimeFormatter> formatterMap = new ConcurrentHashMap<>(8);

    public static String dateToString(long date, String pattern) {
        if (date == 0) {
            return "";
        }
        return dateToString(Instant.ofEpochMilli(date), pattern);
    }

    public static String dateToString(TemporalAccessor temporal, String pattern) {
        DateTimeFormatter dateTimeFormatter = formatterMap.computeIfAbsent(pattern, (p) -> DateTimeFormatter.ofPattern(p)
                .withLocale(Locale.CHINA)
                .withZone(ZoneId.systemDefault())
        );
        return dateTimeFormatter.format(temporal);
    }

    public static String dateToUtcString(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }
}
