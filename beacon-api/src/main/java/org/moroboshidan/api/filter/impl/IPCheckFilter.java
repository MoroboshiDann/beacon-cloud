package org.moroboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.api.client.BeaconCacheClient;
import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.ApiException;
import org.moroboshidan.api.filter.CheckFilter;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/*
 * @description: 校验请求的ip地址是否为白名单
 * @author: MoroboshiDan
 * @time: 2024/6/28 20:18
 */
@Service(value = "ip")
@Slf4j
public class IPCheckFilter implements CheckFilter {
    @Autowired
    private BeaconCacheClient cacheClient;

    private final String IP_ADDRESS = "ipAddress";

    @Override
    public void check(StandardSubmit submit) {
        log.info("beacon-api check IP address, checking......");
        log.info("beacon-api real IP address: {}", submit.getRealIP());
        // 1. 根据CacheClient根据客户的apikey以及ipAddress去查询客户的IP白名单
        List<String> ipWhitelist = (List<String>) cacheClient.hget(CacheConstant.CLIENT_BUSINESS + submit.getApikey(), IP_ADDRESS);
        submit.setIp(ipWhitelist);

        // 2. 如果IP白名单为null，直接放行，或者包含，修改逻辑判断。
        // 如果客户未设置IP白名单，认为客户认为什么IP都可以访问
        // 如果设置了IP白名单，才需要去做一个校验
        if(CollectionUtils.isEmpty(ipWhitelist)|| ipWhitelist.contains(submit.getRealIP())){
            log.info("beacon-api check IP address, client IP legal");
            return;
        }

        //3. IP白名单不为空，并且客户端请求不在IP报名单内
        log.info("beacon-api check IP address, client IP not in IP whitelist");
        throw new ApiException(ExceptionEnums.IP_NOT_WHITE);
    }
}
