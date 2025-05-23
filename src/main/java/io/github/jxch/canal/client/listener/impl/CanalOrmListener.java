package io.github.jxch.canal.client.listener.impl;

import io.github.jxch.canal.client.listener.CanalEvent;
import io.github.jxch.canal.client.listener.CanalListener;
import io.github.jxch.canal.client.listener.CanalTable;
import io.github.jxch.canal.client.model.CanalModel;
import io.github.jxch.canal.client.util.CanalSpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CanalOrmListener implements CanalListener {
    @Autowired(required = false)
    private List<CanalEvent> canalEvents = Collections.emptyList();

    @Override
    public boolean support(CanalModel canalModel) {
        return true;
    }

    @Override
    public void onEvent(CanalModel canalModel) {
        canalEvents.forEach(event -> Hodler.INSTANCE.push(event, canalModel));
    }

    @Async
    public void push(CanalEvent canalEvent, CanalModel canalModel) {
        if (AnnotatedElementUtils.hasAnnotation(canalEvent.getClass(), CanalTable.class)) {
            CanalTable canalTable = AnnotatedElementUtils.findMergedAnnotation(canalEvent.getClass(), CanalTable.class);
            if (canalTable != null && canalModel.getSchema().equals(canalTable.schema()) && canalModel.getTable().equals(canalTable.table())) {
                Class<?> type = canalEvent.getType();
                switch (canalModel.getEventType()) {
                    case INSERT:
                        canalEvent.onInsert(canalModel.getAfterModels(type));
                        break;
                    case UPDATE:
                        canalEvent.onUpdate(canalModel.getBeforeModels(type), canalModel.getAfterModels(type));
                        break;
                    case DELETE:
                        canalEvent.onDelete(canalModel.getBeforeModels(type));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static class Hodler {
        public static final CanalOrmListener INSTANCE = CanalSpringUtil.getBean(CanalOrmListener.class);
    }

}
