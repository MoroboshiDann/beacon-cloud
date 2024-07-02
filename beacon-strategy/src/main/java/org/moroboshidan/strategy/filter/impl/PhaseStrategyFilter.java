package org.moroboshidan.strategy.filter.impl;

import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.common.util.OperatorUtil;
import org.moroboshidan.strategy.client.BeaconCacheClient;
import org.moroboshidan.strategy.filter.StrategyFilter;
import org.moroboshidan.strategy.util.MobileOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 号段补全：获取手机号的运营商以及对应的归属地
 *
 * @author zjw
 * @description
 */
@Service(value = "phase")
@Slf4j
public class PhaseStrategyFilter implements StrategyFilter {
    /**
     * 切分手机号前7位
     */
    private final int MOBILE_START = 0;
    private final int MOBILE_END = 7;
    /**
     * 校验的长度
     */
    private final int LENGTH = 2;
    /**
     * 分割区域和运营商的标识
     */
    private final String SEPARATE = ",";
    /**
     * 未知的情况
     */
    private final String UNKNOWN = "未知 未知,未知";

    @Autowired
    private BeaconCacheClient cacheClient;

    @Autowired
    private MobileOperatorUtil mobileOperatorUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-号段补齐】   补全ing…………");
        // 1、根据手机号前7位，查询手机号信息
        String mobile = submit.getMobile().substring(MOBILE_START, MOBILE_END);
        String mobileInfo = cacheClient.getString(CacheConstant.PHASE + mobile);

        getMobileInfo:
        if (StringUtils.isEmpty(mobileInfo)) {
            // 2、查询不到，需要调用三方接口，查询手机号对应信息
            mobileInfo = mobileOperatorUtil.getMobileInfoBy360(mobile);
            if (!StringUtils.isEmpty(mobileInfo)) {
                // 3、调用三方查到信息后，发送消息到MQ，并且同步到MySQL和Redis
                rabbitTemplate.convertAndSend(RabbitMQConstant.MOBILE_AREA_OPERATOR, submit.getMobile());
                break getMobileInfo;
            }
            mobileInfo = UNKNOWN;
        }

        // 4、无论是Redis还是三方接口查询到之后，封装到StandardSubmit对象中
        String[] areaAndOperator = mobileInfo.split(SEPARATE);
        if (areaAndOperator.length == LENGTH) {
            submit.setArea(areaAndOperator[0]);
            submit.setOperatorId(OperatorUtil.getOperatorIdByOperatorName(areaAndOperator[1]));
        }
    }
}
