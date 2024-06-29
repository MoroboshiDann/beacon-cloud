package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/*
 * @description: 校验请求的ip地址是否为白名单
 * @author: MoroboshiDan
 * @time: 2024/6/28 20:18
 */
@Service(value = "ip")
@Slf4j
public class IPCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("beacon-api check IP address, checking......");
    }
}
