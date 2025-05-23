package io.github.jxch.example.canal.client.listener;

import com.alibaba.fastjson2.JSON;
import io.github.jxch.canal.client.listener.CanalListener;
import io.github.jxch.canal.client.model.CanalModel;
import io.github.jxch.example.canal.client.model.UserTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ModelConvertListener implements CanalListener {

    @Override
    public boolean support(CanalModel canalModel) {
        return true;
    }

    @Override
    public void onEvent(CanalModel canalModel) {
        log.info("onEvent:{}", JSON.toJSONString(canalModel.getAfterModels(UserTest.class)));
    }

}
