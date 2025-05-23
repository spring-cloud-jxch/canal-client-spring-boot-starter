package io.github.jxch.canal.client.type;

import io.github.jxch.canal.client.util.CanalDateUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.Base64;
import java.util.Objects;


@Order
@Component
public class DefaultJdbcTypeHandler implements JdbcTypeHandler {

    @Override
    public boolean support(JDBCType jdbcType, String value) {
        return true;
    }

    @Override
    public Object convert(JDBCType jdbcType, String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            switch (jdbcType) {
                // 字符类型
                case CHAR:
                case VARCHAR:
                case LONGVARCHAR:
                case CLOB:
                case NCHAR:
                case NVARCHAR:
                case LONGNVARCHAR:
                case NCLOB:
                    return value;

                // 整数类型
                case TINYINT:
                    return Byte.valueOf(value);
                case SMALLINT:
                    return Short.valueOf(value);
                case INTEGER:
                    return Integer.valueOf(value);
                case BIGINT:
                    return Long.valueOf(value);

                // 浮点类型
                case FLOAT:
                    return Float.valueOf(value);
                case REAL:
                case DOUBLE:
                    return Double.valueOf(value);

                // 数值类型
                case DECIMAL:
                case NUMERIC:
                    return new BigDecimal(value);

                // 布尔类型
                case BIT:
                case BOOLEAN:
                    // 支持 "1"/"0"、"true"/"false"、"yes"/"no"
                    if ("1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) {
                        return true;
                    }
                    if ("0".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) {
                        return false;
                    }
                    return Boolean.valueOf(value);

                // 日期时间类型
                case DATE:
                    return CanalDateUtil.parseLocalDate(value);

                case TIME:
                    return CanalDateUtil.parseLocalTime(value);

                case TIMESTAMP:
                case TIMESTAMP_WITH_TIMEZONE:
                    return CanalDateUtil.parseLocalDateTime(value);

                // 二进制类型
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY:
                case BLOB:
                    // 支持Base64和16进制字符串
                    return parseBinary(value);

                // JSON类型（部分数据库扩展）
                case SQLXML:
                    return value; // 也可以直接返回String

                // 枚举类型
                case DISTINCT: // 视实际业务而定
                    return value;

                // 其它
                case ARRAY:
                    // 这里简单用逗号分隔转为字符串数组
                    return value.split(",");

                case NULL:
                    return null;

                default:
                    // 未知类型一律返回字符串
                    return value;
            }
        } catch (Exception ex) {
            throw new JdbcTypeHandlerConvertException("无法将值 [" + value + "] 转换为 " + jdbcType + " 对应的Java类型", ex);
        }
    }

    public static byte[] parseBinary(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        value = value.trim();

        // 1. 判断Base64（简单判断：只包含Base64合法字符且长度为4的倍数）
        String base64Pattern = "^[A-Za-z0-9+/\\-_=]+$";
        if (value.length() % 4 == 0 && value.matches(base64Pattern)) {
            try {
                return Base64.getDecoder().decode(value);
            } catch (IllegalArgumentException e) {
                // 尝试URL风格Base64
                try {
                    return Base64.getUrlDecoder().decode(value);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // 2. 判断16进制（可带0x/0X前缀，允许大小写）
        String hex = value;
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        if (hex.matches("^[0-9a-fA-F]+$") && hex.length() % 2 == 0) {
            return hexStringToByteArray(hex);
        }

        // 3. 判断纯数字字符串（转为ascii字节）
        if (value.matches("^\\d+$")) {
            return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

        // 4. 其它情况，按UTF-8编码转byte[]
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s+", ""); // 去除所有空格
        if (s.length() % 2 != 0) {
            // 补0在前面
            s = "0" + s;
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i+1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException(
                        "非法的十六进制字符: \"" + s.charAt(i) + s.charAt(i+1) + "\" at position " + i);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }
}
