package io.github.jxch.canal.client;

import io.github.jxch.canal.client.config.CanalProperties;
import io.github.jxch.canal.client.listener.CanalListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan
@EnableConfigurationProperties(CanalProperties.class)
@ConditionalOnProperty(prefix = "spring.canal", name = "enabled", matchIfMissing = true)
public class CanalAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CanalDistributor canalDistributor(List<CanalListener> canalListeners) {
        return new CanalDistributor(canalListeners);
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalRunner canalRunner(CanalProperties canalProperties, CanalDistributor canalDistributor)  {
        return new CanalRunner(canalProperties, canalDistributor);
    }

}
