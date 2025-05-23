package io.github.jxch.canal.client.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.JDBCType;

public interface CanalOrmSetter {

    boolean support(Class<?> clazz, Field field, Object value, JDBCType jdbcType, String valueOriginal);

    void set(Class<?> clazz, Field field, Method setter, Object target, Object value, JDBCType jdbcType, String valueOriginal);

}
