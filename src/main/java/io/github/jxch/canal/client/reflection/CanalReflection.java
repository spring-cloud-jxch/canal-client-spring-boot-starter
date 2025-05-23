package io.github.jxch.canal.client.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class CanalReflection {
    private static final Logger log = LoggerFactory.getLogger(CanalReflection.class);

    private static void collectInterfaces(Class<?> intf, Set<Class<?>> interfaces) {
        if (interfaces.add(intf)) {
            // 获取接口可能扩展的其他接口
            for (Class<?> superInterface : intf.getInterfaces()) {
                collectInterfaces(superInterface, interfaces);
            }
        }
    }

    public static Type findParameterizedType(Type clazz, Class<?> targetType) {
        if (clazz instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) clazz;
            // 如果直接匹配目标接口
            if (targetType.equals(pt.getRawType())) {
                return pt;
            }
            // 检查该参数化类型的原始类型的接口
            return findParameterizedType(pt.getRawType(), targetType);
        } else if (clazz instanceof Class) {
            Class<?> currentClass = (Class<?>) clazz;

            // 检查当前类所有直接实现的接口
            for (Type intf : currentClass.getGenericInterfaces()) {
                Type result = findParameterizedType(intf, targetType);
                if (result != null) {
                    return result;
                }
            }
            // 检查父类
            Type superClass = currentClass.getGenericSuperclass();
            if (superClass != null) {
                return findParameterizedType(superClass, targetType);
            }
        }
        return null;
    }

    public static Class<?> getGenericType(Class<?> targetClazz, Class<?> interfaceClazz, int index) {
        Type result = findParameterizedType(targetClazz, interfaceClazz);
        if (result != null) {
            ParameterizedType pt = (ParameterizedType) result;
            Type sType = pt.getActualTypeArguments()[index];
            if (sType instanceof Class) {
                return (Class<?>) sType;
            } else if (sType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) sType).getRawType();
            }
        }
        throw new IllegalStateException("无法获取泛型类型 S 的 Class 对象");
    }

}
