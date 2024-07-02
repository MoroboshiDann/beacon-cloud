package org.morosboshidan.search.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.common.util.JSONUtil;
import org.morosboshidan.search.service.SearchService;
import org.morosboshidan.search.util.SearchUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.moroboshidan.common.constant.RabbitMQConstant;

import java.io.IOException;

@Component
@Slf4j
public class SmsWriteLogListener {
    @Autowired
    private SearchService searchService;

    @RabbitListener(queues = RabbitMQConstant.SMS_WRITE_LOG)
    public void consume(StandardSubmit submit, Channel channel, Message message) throws IOException {
        log.info("搜索模块-接收到存储日志的信息，submit = {}", submit);
        searchService.index(SearchUtil.INDEX + SearchUtil.getYear(), submit.getSequenceId().toString(), JSONUtil.obj2JSON(submit));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
