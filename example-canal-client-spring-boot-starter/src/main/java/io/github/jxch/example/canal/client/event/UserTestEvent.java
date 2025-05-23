package io.github.jxch.example.canal.client.event;

import com.alibaba.fastjson2.JSON;
import io.github.jxch.canal.client.listener.CanalEvent;
import io.github.jxch.canal.client.listener.CanalTable;
import io.github.jxch.example.canal.client.model.UserTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@CanalTable(schema = "testdb", table = "user_test")
public class UserTestEvent implements CanalEvent<UserTest> {
    @Override
    public void onInsert(List<UserTest> afterList) {
        log.info("event [onInsert] -> after: {}", JSON.toJSONString(afterList));
    }

    @Override
    public void onUpdate(List<UserTest> beforeList, List<UserTest> afterList) {
        log.info("event [onUpdate] -> before: {}. after: {}", JSON.toJSONString(beforeList), JSON.toJSONString(afterList));
    }

    @Override
    public void onDelete(List<UserTest> beforeList) {
        log.info("event [onDelete] -> before: {}", JSON.toJSONString(beforeList));
    }
}
