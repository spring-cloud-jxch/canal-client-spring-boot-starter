package io.github.jxch.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import io.github.jxch.canal.client.config.CanalProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;

import java.io.Closeable;
import java.net.InetSocketAddress;

public class CanalRunner implements CommandLineRunner, Closeable {
    private final CanalProperties canalProperties;
    private final CanalDistributor canalDistributor;
    private volatile CanalConnector connector;
    private volatile boolean running = true;

    public CanalRunner(CanalProperties canalProperties, CanalDistributor canalDistributor) {
        this.canalProperties = canalProperties;
        this.canalDistributor = canalDistributor;
    }

    @Async
    @Override
    public void run(String... args) {
        if (!canalProperties.isEnabled()) {
            return;
        }
        this.connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalProperties.getHost(), canalProperties.getPort()),
                canalProperties.getDestinations(), canalProperties.getUsername(), canalProperties.getPassword());

        try {
            connector.connect();
            connector.subscribe(canalProperties.getSubscribe());
            connector.rollback();
            while (true && isRunning()) {
                Message message = connector.getWithoutAck(100);// 获取数据, 不确认
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId != -1 && size > 0) {
                    message.getEntries().parallelStream().forEach(canalDistributor::distribute);
                }
                connector.ack(batchId); // 提交确认
            }
        } finally {
            connector.disconnect();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
        connector.disconnect();
    }

    @Override
    public void close() {
        stop();
    }

}
