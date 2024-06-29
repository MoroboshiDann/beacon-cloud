package org.morosboshidan.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.morosboshidan.enums.SmsCodeEnum;
import org.morosboshidan.form.SingleSendForm;
import org.morosboshidan.model.StandardSubmit;
import org.morosboshidan.util.R;
import org.morosboshidan.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
            R.error(SmsCodeEnum.PARAMETER_INVALIDATE.getCode(), SmsCodeEnum.PARAMETER_INVALIDATE.getMessage());
        }
        // 获取客户端IP
        String ip = getRealIP(request);
        log.info("beacon-api, customer's ip: {}", ip);
        // 2. 构建StandardSubmit封装各种校验Filter
        StandardSubmit standardSubmit = new StandardSubmit();
        BeanUtils.copyProperties(singleSendForm, standardSubmit);
        // 3. 发送到MQ，交给策略模块
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
