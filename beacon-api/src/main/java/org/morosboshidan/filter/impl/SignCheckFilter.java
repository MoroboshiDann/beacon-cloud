package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/**
 * @author moroboshidan
 * @description 校验短信的签名
 */
@Service(value = "sign")
@Slf4j
public class SignCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("beacon-api, check the sign of message, checking.....");
    }
}
