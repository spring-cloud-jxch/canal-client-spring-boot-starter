package io.github.jxch.canal.client;

import com.alibaba.otter.canal.protocol.CanalEntry;
import io.github.jxch.canal.client.listener.CanalListener;
import io.github.jxch.canal.client.model.CanalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public class CanalDistributor implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(CanalDistributor.class);
    private static ApplicationContext applicationContext;
    private final List<CanalListener> canalListeners;

    public CanalDistributor(List<CanalListener> canalListeners) {
        this.canalListeners = canalListeners;
    }

    @Async
    public void distribute(CanalEntry.Entry entry) {
        if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
            return;
        }

        CanalModel canalModel = new CanalModel(entry);
        for (CanalListener canalListener : canalListeners) {
            Holder.CANAL_DISTRIBUTOR.listen(canalListener, canalModel);
        }
    }

    @Async
    public void listen(CanalListener canalListener, CanalModel canalModel) {
        if (canalListener.support(canalModel)) {
            try {
                canalListener.onEvent(canalModel);
            } catch (Exception e) {
                log.error("CanalListener error", e);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CanalDistributor.applicationContext = applicationContext;
    }

    public static class Holder {
        public static final CanalDistributor CANAL_DISTRIBUTOR = applicationContext.getBean(CanalDistributor.class);
    }

}
