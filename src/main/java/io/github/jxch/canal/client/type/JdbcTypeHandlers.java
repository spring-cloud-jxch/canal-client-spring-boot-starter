package io.github.jxch.canal.client.type;

import io.github.jxch.canal.client.util.CanalSpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.List;

@Component
public class JdbcTypeHandlers {

    @Autowired(required = false)
    private List<JdbcTypeHandler> typeHandlers = Collections.emptyList();

    public Object convert2javaType(JDBCType jdbcType, String value) {
        for (JdbcTypeHandler typeHandler : typeHandlers) {
            try {
                if (typeHandler.support(jdbcType, value)) {
                    return typeHandler.convert(jdbcType, value);
                }
            } catch (Exception ignored) {
            }
        }
        return value;
    }

    public static Object convert(JDBCType jdbcType, String value) {
        return Hodler.INSTANCE.convert2javaType(jdbcType, value);
    }

    public static class Hodler {
        public static final JdbcTypeHandlers INSTANCE = CanalSpringUtil.getBean(JdbcTypeHandlers.class);
    }

}
