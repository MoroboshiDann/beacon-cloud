package org.morosboshidan.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.morosboshidan.filter.CheckFilter;
import org.springframework.stereotype.Service;

/**
 * @author moroboshidan
 * @description  校验短信的模板
 */
@Service(value = "template")
@Slf4j
public class TemplateCheckFilter implements CheckFilter {
    @Override
    public void check(Object obj) {
        log.info("beacon-api, check the template of message, checking.....");
    }
}
