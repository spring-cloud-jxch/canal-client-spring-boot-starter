package io.github.jxch.canal.client.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanalReflection {
    private static final Logger log = LoggerFactory.getLogger(CanalReflection.class);

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
