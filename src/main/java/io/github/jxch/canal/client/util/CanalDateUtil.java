package io.github.jxch.canal.client.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CanalDateUtil {

    public static LocalDate parseLocalDate(String value) {
        String[] patterns = {
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyy.MM.dd",
                "yyyyMMdd",
                "MM/dd/yyyy",
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "dd.MM.yyyy"
        };
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }
        // 尝试ISO标准格式
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }
        throw new IllegalArgumentException("无法识别的日期格式: " + value);
    }

    public static LocalTime parseLocalTime(String value) {
        String[] patterns = {
                "HH:mm:ss.SSS",
                "HH:mm:ss",
                "HH:mm",
                "HHmmss",
                "HHmm"
        };
        for (String pattern : patterns) {
            try {
                return LocalTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }
        // 尝试 ISO 标准格式
        try {
            return LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException ignored) {
        }
        throw new IllegalArgumentException("无法识别的时间格式: " + value);
    }

    public static LocalDateTime parseLocalDateTime(String value) {
        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd HH:mm:ss.SSS",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm",
                "yyyy.MM.dd HH:mm:ss",
                "yyyyMMdd HHmmss",
                "yyyyMMddHHmmss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm",
                "yyyyMMdd'T'HHmmss",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",   // 带时区
                "yyyy-MM-dd'T'HH:mm:ssXXX"        // 带时区
                // 你可以继续补充
        };
        for (String pattern : patterns) {
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }
        // ISO_LOCAL_DATE_TIME（yyyy-MM-ddTHH:mm:ss）
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }
        // 如果是只有日期，补零时分秒
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
        }
        throw new IllegalArgumentException("无法识别的日期时间格式: " + value);
    }

    /**
     * 检查对象是否为时间类型
     */
    public static boolean isDateTimeType(Object obj) {
        return obj instanceof java.util.Date ||
                obj instanceof java.sql.Timestamp ||
                obj instanceof java.time.Instant ||
                obj instanceof java.time.LocalDateTime ||
                obj instanceof java.time.LocalDate;
    }

    /**
     * 从时间对象提取时间戳（毫秒）
     */
    public static long extractTimestamp(Object obj) {
        if (obj instanceof java.util.Date) {
            return ((java.util.Date) obj).getTime();
        }
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).getTime();
        }
        if (obj instanceof java.time.Instant) {
            return ((java.time.Instant) obj).toEpochMilli();
        }
        if (obj instanceof java.time.LocalDateTime) {
            java.time.LocalDateTime ldt = (java.time.LocalDateTime) obj;
            java.time.ZoneId zone = java.time.ZoneId.systemDefault();
            return ldt.atZone(zone).toInstant().toEpochMilli();
        }
        if (obj instanceof java.time.LocalDate) {
            java.time.LocalDate ld = (java.time.LocalDate) obj;
            java.time.ZoneId zone = java.time.ZoneId.systemDefault();
            return ld.atStartOfDay(zone).toInstant().toEpochMilli();
        }

        throw new IllegalArgumentException("Cannot extract timestamp from " + obj.getClass());
    }
}
