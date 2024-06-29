package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/*
 * @description: 校验客户端的api key是否合法
 * @author: MoroboshiDan
 * @time: 2024/6/28 20:18
 */
@Service(value = "apikey")
@Slf4j
public class ApiKeyCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("beacon-api, check api key of request, checking.....");
    }
}
