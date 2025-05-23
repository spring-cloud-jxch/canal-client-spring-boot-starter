package io.github.jxch.canal.client.orm;

import io.github.jxch.canal.client.model.SimpleColumns;
import io.github.jxch.canal.client.name.CanalColumn;
import io.github.jxch.canal.client.util.CanalSpringUtil;
import io.github.jxch.canal.client.util.CanalStrings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class CanalOrm  {

    @Autowired(required = false)
    private List<CanalOrmSetter> canalOrmSetters = Collections.emptyList();

    public <T> List<T> orm(SimpleColumns simpleColumns, Class<T> clazz) {
        try {
            Method[] methods = clazz.getMethods();
            Field[] fields = clazz.getDeclaredFields();

            List<T> models = new ArrayList<>();
            for (int i = 0; i < simpleColumns.getValues().size(); i++) {
                T instance = clazz.newInstance();

                for (Field field : fields) {
                    String fieldName = field.getName();
                    String name;
                    CanalColumn column = field.getAnnotation(CanalColumn.class);
                    if (Objects.nonNull(column)) {
                        if (column.ignore()) {
                            continue;
                        }
                        name = column.name();
                    } else {
                        if (simpleColumns.hasName(fieldName)) {
                            name = fieldName;
                        } else if (simpleColumns.hasName(CanalStrings.camelToSnake(fieldName))) {
                            name = CanalStrings.camelToSnake(fieldName);
                        } else if (simpleColumns.hasName(CanalStrings.snakeToCamel(fieldName))) {
                            name = CanalStrings.snakeToCamel(fieldName);
                        } else {
                            continue;
                        }
                    }
                    if (simpleColumns.hasName(name)) {
                        String setterName = "set" + CanalStrings.capitalize(fieldName);

                        Method setter = null;
                        Class<?> setterParameterType = null;
                        for (Method method : methods) {
                            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                                setter = method;
                                setterParameterType = method.getParameterTypes()[0];
                            }
                        }

                        if (Objects.nonNull(setter)) {
                            Object value = simpleColumns.getConvertValueByName(name, i);
                            String valueOriginal = simpleColumns.getValuesByName(name).get(i);
                            JDBCType jdbcType = simpleColumns.getJDBCTypeByName(name);
                            boolean set = false;

                            for (CanalOrmSetter ormSetter : canalOrmSetters) {
                                if (ormSetter.support(clazz, field, value, jdbcType, valueOriginal)) {
                                    try {
                                        ormSetter.set(clazz, field, setter, instance, value, jdbcType, valueOriginal);
                                        set = true;
                                        break;
                                    } catch (Exception ignored) {
                                    }
                                }
                            }

                            if (!set) {
                                setter.invoke(instance, simpleColumns.getConvertValueByName(name, i, setterParameterType));
                            }
                        }
                    }
                }

                models.add(instance);
            }

            return models;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> convert(SimpleColumns simpleColumns, Class<T> clazz) {
        return Holder.CANAL_ORM.orm(simpleColumns, clazz);
    }

    public static class Holder {
        public static final CanalOrm CANAL_ORM = CanalSpringUtil.getBean(CanalOrm.class);
    }

}
