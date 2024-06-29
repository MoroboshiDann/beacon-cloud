package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/**
 * @author moroboshidan
 * @description  校验客户剩余的金额是否充足
 */
@Service(value = "fee")
@Slf4j
public class FeeCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("beacon-api, check fee amount of customer, checking.....");
    }
}
