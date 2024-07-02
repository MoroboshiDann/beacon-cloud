package org.morosboshidan.push.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.common.model.StandardReport;
import org.moroboshidan.common.util.JSONUtil;
import org.morosboshidan.push.config.RabbitMQConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Component
@Slf4j
public class PushReportListener {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 重试的时间间隔。
    private int[] delayTime = {0,15000,30000,60000,300000};

    private final String SUCCESS = "SUCCESS";

    @RabbitListener(queues = RabbitMQConstant.SMS_PUSH_REPORT)
    public void listen(StandardReport standardReport, Channel channel, Message message) throws IOException {
        // 1. 获取客户端的回调地址
        String callbackUrl = standardReport.getCallbackUrl();
        if (StringUtils.isEmpty(callbackUrl)) {
            log.info("推送模块-推送状态报告, 客户没有指定回调地址, callbackUrl={}", callbackUrl);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        // 2. 发送状态报告
        boolean flag = pushReport(standardReport);
        // 3. 发送失败重试，重试5次
        isResend(standardReport, flag);
        // 4. 消费完毕，手动ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听延迟交换机路由过来的消息
     * @param report
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConfig.DELAYED_QUEUE)
    public void delayedConsume(StandardReport report, Channel channel,Message message) throws IOException {
        // 1、发送状态报告
        boolean flag = pushReport(report);

        // 2、判断状态报告发送情况
        isResend(report, flag);

        // 手动ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    private boolean pushReport(StandardReport standardReport) {
        boolean flag = false;
        // 1. 设置发送的参数
        String body = JSONUtil.obj2JSON(standardReport);
        // 2. 声明resttemplate模板
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            log.info("推送模块-推送状态报告, 第{}次推送开始", standardReport.getResendCount());
            String result = restTemplate.postForObject("http://" + standardReport.getCallbackUrl(), new HttpEntity<>(body, headers), String.class);
            flag = SUCCESS.equals(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断状态报告是否推送成功，失败的话需要发送重试消息
     * @param report
     * @param flag
     */
    private void isResend(StandardReport report, boolean flag) {
        if(!flag){
            log.info("【推送模块-推送状态报告】 第{}次推送状态报告失败！report = {}",report.getResendCount() + 1,report);
            report.setResendCount(report.getResendCount() + 1);
            if(report.getResendCount() >= 5){
                return;
            }
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE, "", report, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    // 设置延迟时间
                    message.getMessageProperties().setDelay(delayTime[report.getResendCount()]);
                    return message;
                }
            });
        }else{
            log.info("【推送模块-推送状态报告】 第一次推送状态报告成功！report = {}",report);
        }
    }
}
