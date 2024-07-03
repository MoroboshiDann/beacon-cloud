package org.moroboshidan.api.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.api.client.BeaconCacheClient;
import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.ApiException;
import org.moroboshidan.api.filter.CheckFilter;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
 * @description: 校验客户端的api key是否合法
 * @author: MoroboshiDan
 * @time: 2024/6/28 20:18
 */
@Service(value = "apikey")
@Slf4j
public class ApiKeyCheckFilter implements CheckFilter {
    @Autowired
    private BeaconCacheClient beaconCacheClient;

    @Override
    public void check(StandardSubmit submit) {
        log.info("beacon-api, check api key of request, checking.....");
        // 1. 从缓存中查取客户端信息
        Map clientBusiness = beaconCacheClient.hGetAll(CacheConstant.CLIENT_BUSINESS + submit.getApikey());
        // 2. 如果结果为空，直接抛出异常，因为，如果缓存未命中，缓存模块负责读取数据库
        if (clientBusiness == null || clientBusiness.isEmpty()) {
            log.info("beacon-api, illegal api key={}, no client business found", submit.getApikey());
            throw new ApiException(ExceptionEnums.ILLEGAL_APIKEY);
        }
        //3. 正常封装数据
        submit.setClientId(Long.parseLong(clientBusiness.get("id") + ""));
        log.info("beacon-api, client info found, clientBusiness = {}",clientBusiness);
    }
}
