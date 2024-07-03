package org.moroboshidan.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.api.enums.SmsCodeEnum;
import org.moroboshidan.api.filter.CheckFilterContext;
import org.moroboshidan.api.form.SingleSendForm;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.api.util.R;
import org.moroboshidan.common.util.SnowFlakeUtil;
import org.moroboshidan.api.vo.ResultVO;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/sms")
@Slf4j
@RefreshScope // 方便headers变量能够动态刷新
public class SmsController {
    // 服务端获取客户端真实IP地址时，使用到的请求头字段名称
    @Value("${headers}")
    private String headers;

    private final String UNKNOWN = "unknown";
    private final String X_FORWARDED_FOR = "x-forwarded-for";

    @Autowired
    private CheckFilterContext checkFilterContext;

    @Autowired
    private SnowFlakeUtil snowFlakeUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
     * @description: 发送单条短信
     * @param singleSendForm
     * @param bindingResult
     * @param request
     * @return: org.morosboshidan.vo.ResultVO
     * @author: MoroboshiDan
     * @time: 2024/6/29 16:13
     */
    @PostMapping(value = "/single_send", produces = "application/json;charset=utf-8")
    public ResultVO singleSend(@RequestBody @Validated SingleSendForm singleSendForm, BindingResult bindingResult, HttpServletRequest request) {
        // 1. 参数校验
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            log.info("beacon-api detected one parameter invalidate: {}", errorMessage);
            return R.error(SmsCodeEnum.PARAMETER_INVALIDATE.getCode(), SmsCodeEnum.PARAMETER_INVALIDATE.getMessage());
        }
        // 获取客户端IP
        String ip = getRealIP(request);
        log.info("beacon-api, customer's ip: {}", ip);
        // 2. 构建StandardSubmit封装各种校验Filter
        StandardSubmit submit = new StandardSubmit();
        BeanUtils.copyProperties(singleSendForm, submit);
        submit.setRealIP(ip);
        // 调用策略模式的责任链
        checkFilterContext.check(submit);
        // 基于雪花算法生成唯一id，并添加到StandardSubmit对象中，并设置发送时间
        submit.setSequenceId(snowFlakeUtil.nextId());
        submit.setSendTime(LocalDateTime.now());
        // 3. 发送到MQ，交给策略模块
        rabbitTemplate.convertAndSend(RabbitMQConstant.SMS_PRE_SEND, submit, new CorrelationData(submit.getSequenceId().toString()));
        return R.success();
    }

    /*
     * @description: 获取客户端真实IP地址
     * @param request
     * @return: java.lang.String
     * @author: MoroboshiDan
     * @time: 2024/6/29 15:20
     */
    private String getRealIP(HttpServletRequest request) {
        String ip = null;
        for (String header : headers.split(",")) {
            // 健壮性校验
            if (!StringUtils.isEmpty(header)) {
                ip = request.getHeader(header);
                if (!StringUtils.isEmpty(ip) || ! !UNKNOWN.equalsIgnoreCase(ip)) {
                    if (X_FORWARDED_FOR.equalsIgnoreCase(ip) && ip.indexOf(",") > 0) {
                        ip = ip.substring(0, ip.indexOf(","));
                    }
                    return ip;
                }
            }
        }
        return request.getRemoteAddr();
    }
}
