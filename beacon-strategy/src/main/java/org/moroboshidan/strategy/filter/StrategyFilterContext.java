package org.moroboshidan.strategy.filter;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.strategy.client.BeaconCacheClient;
import org.moroboshidan.common.constant.CacheConstant;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class StrategyFilterContext {
    @Autowired
    private Map<String, StrategyFilter> strategyFilters; // 从SpringIoC容器中拿出来Filter实现类对象集合
    @Autowired
    private BeaconCacheClient cacheClient;

    private final String CLIENT_FILTERS = "clientFilters";

    public void strategy(StandardSubmit submit) {
        // 1. 从redis中获取客户的校验信息，策略模块的校验信息不支持客户自己定制，而是公司给每个客户规定了校验的内容
        // 校验过滤器的名称就存储在客户数据库client_filters字段中
        String filters = cacheClient.hget(CacheConstant.CLIENT_BUSINESS + submit.getApikey(), CLIENT_FILTERS);

        String[] filterArray = null;
        // 2. 根据定制好的过滤器名称，调用过滤器
        if (filters != null && (filterArray = filters.split(",")).length > 0) {
            for (String filter : filterArray) {
                StrategyFilter strategyFilter = strategyFilters.get(filter);
                if (strategyFilter != null) {
                    strategyFilter.strategy(submit);
                }
            }
        }
    }
}
