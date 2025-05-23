package io.github.jxch.canal.client.listener;

import io.github.jxch.canal.client.reflection.CanalReflection;

import java.util.List;

public interface CanalEvent<T> {

    void onInsert(List<T> afterList);

    void onUpdate(List<T> beforeList, List<T> afterList);

    void onDelete(List<T> beforeList);

    default Class<?> getType() {
        return CanalReflection.getGenericType(this.getClass(), CanalEvent.class, 0);
    }

}
