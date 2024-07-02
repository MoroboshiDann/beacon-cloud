package org.moroboshidan.strategy.filter.impl;

import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.strategy.client.BeaconCacheClient;
import org.moroboshidan.strategy.filter.StrategyFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 携号转网策略！！！！
 *
 * @author zjw
 * @description
 */
@Service(value = "transfer")
@Slf4j
public class TransferStrategyFilter implements StrategyFilter {

    // 代表携号转网了！
    private final Boolean TRANSFER = true;

    @Autowired
    private BeaconCacheClient cacheClient;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-携号转网策略】   ing…………");
        // 1、获取用户手机号
        String mobile = submit.getMobile();

        // 2、直接基于Redis查询携号转网信息
        String value = cacheClient.getString(CacheConstant.TRANSFER + mobile);

        // 3、如果存在携号转网，设置运营商信息
        if (!StringUtils.isEmpty(value)) {
            // 代表携号转网了
            submit.setOperatorId(Integer.valueOf(value));
            submit.setIsTransfer(TRANSFER);
            log.info("【策略模块-携号转网策略】   当前手机号携号转网了！");
            return;
        }
        log.info("【策略模块-携号转网策略】   嘛事没有！");
    }
}
