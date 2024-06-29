package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/**
 * @author moroboshidan
 * @description  校验手机号的格式合法性
 */
@Service(value = "mobile")
@Slf4j
public class MobileCheckFilter implements CheckFilter {


    @Override
    public void check(Object obj) {
        log.info("beacon-api, check the phone number, checking.....");
    }
}
