package org.moroboshidan.strategy.mq;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.common.exception.StrategyException;
import org.moroboshidan.strategy.filter.StrategyFilterContext;
import org.springframework.amqp.core.Message;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;

@Component
@Slf4j
public class PreSendListener {
    @Autowired
    private StrategyFilterContext strategyFilterContext;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConstant.SMS_PRE_SEND)
    public void listen(StandardSubmit submit, Message message, Channel channel) throws IOException {
        log.info("策略模块-接收消息, 接收到接口模块发送来的消息:{}", submit);
        // 处理业务
        System.out.println("start: " + System.currentTimeMillis());
        try {
            // 责任链过滤器
            strategyFilterContext.strategy(submit);
            log.info("策略模块-消费完毕, 手动ack");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (StrategyException e) {
            log.info("策略模块-消费失败, 校验未通过, msg={}", e.getMessage());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } finally {
            System.out.println("end: " + System.currentTimeMillis());
        }
    }
}
