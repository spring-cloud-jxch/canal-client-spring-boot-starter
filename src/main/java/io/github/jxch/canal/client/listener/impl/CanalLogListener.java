package io.github.jxch.canal.client.listener.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.github.jxch.canal.client.config.CanalProperties;
import io.github.jxch.canal.client.listener.CanalListener;
import io.github.jxch.canal.client.model.CanalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order
@Component
public class CanalLogListener implements CanalListener {
    private static final Logger log = LoggerFactory.getLogger(CanalLogListener.class);
    private final CanalProperties canalProperties;

    public CanalLogListener(CanalProperties canalProperties) {
        this.canalProperties = canalProperties;
    }

    @Override
    public boolean support(CanalModel canalModel) {
        return canalProperties.isLogEnabled();
    }

    @Override
    public void onEvent(CanalModel canalModel) {
        String afterLog = canalProperties.isLogPretty() ? JSON.toJSONString(canalModel.getAfterSimpleColumns(), JSONWriter.Feature.PrettyFormat) : JSON.toJSONString(canalModel.getAfterSimpleColumns());
        String beforeLog = canalProperties.isLogPretty() ? JSON.toJSONString(canalModel.getBeforeSimpleColumns(), JSONWriter.Feature.PrettyFormat) : JSON.toJSONString(canalModel.getBeforeSimpleColumns());

        log.info("{}.{} : {} -> after: {} before: {}", canalModel.getSchema(), canalModel.getTable(), canalModel.getEventType(), afterLog, beforeLog);
    }

}
