package io.github.jxch.canal.client.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalSetter {

    Class<? extends CanalOrmSetter> setter() default DefaultCanalOrmSetter.class;

}
