package io.github.jxch.canal.client.type;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.List;

@Component
public class JdbcTypeHandlers implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JdbcTypeHandlers.applicationContext = applicationContext;
    }

    public static class Hodler {
        public static final JdbcTypeHandlers INSTANCE = applicationContext.getBean(JdbcTypeHandlers.class);
    }

}
