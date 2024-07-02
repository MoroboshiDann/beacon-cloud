package org.morosboshidan.search.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.moroboshidan.common.constant.RabbitMQConstant;

@Component
@Slf4j
public class SmsWriteLogListener {
    @RabbitListener(queues = RabbitMQConstant.SMS_WRITE_LOG)
    public void consume(StandardSubmit submit, Channel channel, Message message) {
        log.info("搜索模块-接收到存储日志的信息，submit = {}", submit);

    }
}
