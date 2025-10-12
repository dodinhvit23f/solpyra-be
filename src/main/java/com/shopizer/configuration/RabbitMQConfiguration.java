package com.shopizer.configuration;


import com.shopizer.configuration.RabbitQueuesProperties.QueueProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfiguration {

  final RabbitQueuesProperties rabbitQueuesProperties;

  @Bean
  public Queue addOrderQueue() {
    QueueProperties props = rabbitQueuesProperties.getAddOrder();
    return QueueBuilder.durable(props.getName())
        .deadLetterExchange("")
        .deadLetterRoutingKey(rabbitQueuesProperties.getAddOrder().getName())
        .lazy() // Optional, good for low-memory server
        .build();
  }

  @Bean
  public Queue retryAddOrderQueue() {
    QueueProperties props = rabbitQueuesProperties.getAddOrderRetry();
    return QueueBuilder.durable(props.getName())
        .deadLetterExchange("")
        .deadLetterRoutingKey(rabbitQueuesProperties.getAddOrder().getName())
        .ttl(props.getTtl())
        .lazy() // Optional, good for low-memory server
        .build();
  }

  @Bean
  public TopicExchange addOrderExchange() {
    QueueProperties props = rabbitQueuesProperties.getAddOrder();
    return new TopicExchange(props.getExchange());
  }

  @Bean
  public Binding addOrderBinding(Queue addOrderQueue, TopicExchange addOrderExchange) {
    var props = rabbitQueuesProperties.getAddOrder();
    return BindingBuilder
        .bind(addOrderQueue)
        .to(addOrderExchange)
        .with(props.getRoutingKey());
  }

  @Bean
  public Binding retryAddOrderBinding(Queue retryAddOrderQueue, TopicExchange addOrderExchange) {
    return BindingBuilder.bind(retryAddOrderQueue)
        .to(addOrderExchange)
        .with(rabbitQueuesProperties.getAddOrderRetry().getRoutingKey());
  }


}