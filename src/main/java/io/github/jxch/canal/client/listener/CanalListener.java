package io.github.jxch.canal.client.listener;

import io.github.jxch.canal.client.model.CanalModel;

public interface CanalListener {

    boolean support(CanalModel canalModel);

    void onEvent(CanalModel canalModel);

}
