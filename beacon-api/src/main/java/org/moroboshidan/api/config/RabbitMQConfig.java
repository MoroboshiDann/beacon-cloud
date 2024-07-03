package org.moroboshidan.api.config;

import org.moroboshidan.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

/**
 * 构建消息队列和交换机信息
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 接口模块将消息发送到的队列
     * 该队列在策略模块中？
     * @return
     */
    @Bean
    public Queue preSendQueue() {
        return QueueBuilder.durable(RabbitMQConstant.SMS_PRE_SEND).build();
    }
}
