package org.moroboshidan.api.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.ApiException;
import org.moroboshidan.api.filter.CheckFilter;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.api.util.PhoneFormatCheckUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author moroboshidan
 * @description  校验手机号的格式合法性
 */
@Service(value = "mobile")
@Slf4j
public class MobileCheckFilter implements CheckFilter {
    /**
     * @description: 检验信息收取人的号码是否合法
     * @param submit
     * @return: void
     * @author: MoroboshiDan
     * @time: 2024/6/30 9:57
     */
    @Override
    public void check(StandardSubmit submit) {
        log.info("beacon-api, check the phone number, checking.....");
        String mobile = submit.getMobile();
        if(!StringUtils.isEmpty(mobile) && PhoneFormatCheckUtil.isChinaPhone(mobile)){
            // 如果校验进来，代表手机号么得问题
            log.info("beacon-api, check the phone number, phone number legal, mobile = {}",mobile);
            return;
        }
        log.info("beacon-api, check the phone number, phone number illegal, mobile = {}",mobile);
        throw new ApiException(ExceptionEnums.ERROR_MOBILE);
    }
}
