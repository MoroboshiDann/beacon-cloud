package org.moroboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.client.BeaconCacheClient;
import org.moroboshidan.common.constant.ApiConstant;
import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.ApiException;
import org.moroboshidan.filter.CheckFilter;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moroboshidan
 * @description  校验客户剩余的金额是否充足
 */
@Service(value = "fee")
@Slf4j
public class FeeCheckFilter implements CheckFilter {
    @Autowired
    private BeaconCacheClient cacheClient;

    /**
     * 只要短信内容的文字长度小于等于70个字，按照一条计算
     */
    private final int MAX_LENGTH = 70;

    /**
     * 如果短信内容的文字长度超过70，67字/条计算
     */
    private final int LOOP_LENGTH = 67;

    private final String BALANCE = "balance";
    @Override
    public void check(StandardSubmit submit) {
        log.info("beacon-api, check fee amount of customer, checking.....");
        // 1. 从submit中获取短信内容
        int length = submit.getText().length();
        // 2. 判断短信的长度
        if(length <= MAX_LENGTH){
            // 当前短信内容是一条
            submit.setFee(ApiConstant.SINGLE_FEE);
        }else{
            int strip = length % LOOP_LENGTH == 0 ? length / LOOP_LENGTH : length / LOOP_LENGTH + 1;
            submit.setFee(ApiConstant.SINGLE_FEE * strip);
        }
        // 3. 从缓存中读取客户的剩余金额
        Long balance = ((Integer) cacheClient.hget(CacheConstant.CLIENT_BALANCE + submit.getClientId(), BALANCE)).longValue();
        // 4. 判断当前费用是否能够发送本条信息
        if(balance >= submit.getFee()){
            log.info("beacon-api, check fee amount of customer, rest amount enough");
            return;
        }
        // 5. 金额不足
        log.info("beacon-api, check fee amount of customer, rest amount not enough");
        throw new ApiException(ExceptionEnums.BALANCE_NOT_ENOUGH);
    }
}
