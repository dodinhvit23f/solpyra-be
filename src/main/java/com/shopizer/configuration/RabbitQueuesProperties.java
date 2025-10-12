package com.shopizer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "rabbitmq.queues")
public class RabbitQueuesProperties {

  private QueueProperties addOrder;
  private QueueProperties addOrderRetry;

    @Getter
    @Setter
    public static class QueueProperties {
        private String name;
        private String exchange;
        private String routingKey;
        private int ttl;
    }
}