package io.github.jxch.canal.client.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CanalSpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return CanalSpringUtil.applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return CanalSpringUtil.applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CanalSpringUtil.applicationContext = applicationContext;
    }

}
