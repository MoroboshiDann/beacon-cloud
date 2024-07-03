package org.morosboshidan.search.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.common.model.StandardReport;
import org.morosboshidan.search.service.SearchService;
import org.morosboshidan.search.util.SearchUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SmsUpdateLogListener {


    @Autowired
    private SearchService searchService;

    @RabbitListener(queues = {RabbitMQConstant.SMS_GATEWAY_DEAD_QUEUE})
    public void consume(StandardReport report, Channel channel, Message message) throws IOException {
        log.info("【搜素模块-修改日志】 接收到修改日志的消息 report = {}", report);
        // 将report对象存储ThreadLocal中，方便在搜索模块中获取
        SearchUtil.set(report);
        // 调用搜索模块完成的修改操作
        Map<String,Object> doc = new HashMap<>();
        doc.put("reportState",report.getReportState());
        searchService.update(SearchUtil.INDEX + SearchUtil.getYear(),report.getSequenceId().toString(),doc);
        // ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
