package io.github.jxch.canal.client.orm;

import io.github.jxch.canal.client.util.CanalDateUtil;
import io.github.jxch.canal.client.util.CanalSpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Order
@Component
public class DefaultCanalOrmSetter implements CanalOrmSetter {
    private static final Logger log = LoggerFactory.getLogger(DefaultCanalOrmSetter.class);

    @Override
    public boolean support(Class<?> clazz, Field field, Object value, JDBCType jdbcType, String valueOriginal) {
        return true;
    }

    @Override
    public void set(Class<?> clazz, Field field, Method setter, Object target, Object value, JDBCType jdbcType, String valueOriginal) {
        CanalSetter canalSetter = field.getAnnotation(CanalSetter.class);
        try {
            if (Objects.nonNull(canalSetter)) {
                CanalOrmSetter setterCustom = CanalSpringUtil.getBean(canalSetter.setter());
                if (setterCustom.support(clazz, field, value, jdbcType, valueOriginal)) {
                    if (!setterCustom.getClass().equals(this.getClass())) {
                        setterCustom.set(clazz, field, setter, target, value, jdbcType, valueOriginal);
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        try {
            setter.invoke(target, cast(value, field.getType()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Class<T> clazz) {
        try {
            if (obj == null) {
                return null;
            }
            // 直接类型匹配
            if (clazz.isInstance(obj)) {
                return (T) obj;
            }

            try {
                return clazz.cast(obj);
            } catch (Exception ignored) {
            }

            // 数字类型处理
            if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
                // 时间对象转时间戳
                if ((clazz == Long.class || clazz == long.class) && CanalDateUtil.isDateTimeType(obj)) {
                    return (T) Long.valueOf(CanalDateUtil.extractTimestamp(obj));
                }
                String str = obj.toString();
                if (clazz == Integer.class || clazz == int.class) {
                    return (T) Integer.valueOf(str);
                } else if (clazz == Long.class || clazz == long.class) {
                    return (T) Long.valueOf(str);
                } else if (clazz == Double.class || clazz == double.class) {
                    return (T) Double.valueOf(str);
                } else if (clazz == Float.class || clazz == float.class) {
                    return (T) Float.valueOf(str);
                } else if (clazz == Short.class || clazz == short.class) {
                    return (T) Short.valueOf(str);
                } else if (clazz == Byte.class || clazz == byte.class) {
                    return (T) Byte.valueOf(str);
                } else if (clazz.getName().equals("java.math.BigDecimal")) {
                    return (T) new java.math.BigDecimal(str);
                }
            }

            if (CanalDateUtil.isDateTimeType(obj)) {
                long timestamp = CanalDateUtil.extractTimestamp(obj);
                try {
                    if (clazz == Date.class) {
                        return clazz.cast(new Date(timestamp));
                    } else if (clazz == Instant.class) {
                        return clazz.cast(Instant.ofEpochMilli(timestamp));
                    } else if (clazz == LocalDateTime.class) {
                        return clazz.cast(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
                    }
                } catch (Exception ignored) {
                }
            }

            // Boolean 类型处理
            if (clazz == Boolean.class || clazz == boolean.class) {
                String str = obj.toString().toLowerCase();
                return (T) Boolean.valueOf("true".equals(str) || "1".equals(str));
            }

            // String 类型处理
            if (clazz == String.class) {
                return (T) obj.toString();
            }

            // 其它情况尝试强制类型转换
            try {
                return clazz.cast(obj);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Cannot cast " + obj.getClass() + " to " + clazz, e);
            }
        } catch (Exception e) {
            log.error("Error casting object: {}", obj, e);
            throw new RuntimeException(e);
        }
    }

}
