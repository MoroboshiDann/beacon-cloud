package org.moroboshidan.strategy.filter.impl;

import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.StrategyException;
import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.strategy.client.BeaconCacheClient;
import org.moroboshidan.strategy.filter.StrategyFilter;
import org.moroboshidan.strategy.util.ErrorSendMsgUtil;
import org.moroboshidan.strategy.util.HutoolDFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 敏感词校验
 *
 * @author zjw
 * @description
 */
@Service(value = "hutoolDFADirtyWord")
@Slf4j
public class DirtyWordHutoolDFAStrategyFilter implements StrategyFilter {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BeaconCacheClient cacheClient;

    @Autowired
    private ErrorSendMsgUtil sendMsgUtil;

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-敏感词校验】   校验ing…………");
        // 1、 获取短信内容
        String text = submit.getText();

        // 2、 调用DFA查看敏感词
        List<String> dirtyWords = HutoolDFAUtil.getDirtyWord(text);

        // 4、 根据返回的set集合，判断是否包含敏感词
        if (dirtyWords != null && dirtyWords.size() > 0) {
            // 5、 如果有敏感词，抛出异常 / 其他操作。。
            log.info("【策略模块-敏感词校验】   短信内容包含敏感词信息， dirtyWords = {}", dirtyWords);
            // ================================发送写日志================================
            submit.setErrorMsg(ExceptionEnums.HAVE_DIRTY_WORD.getMsg() + "dirtyWords = " + dirtyWords.toString());
            sendMsgUtil.sendWriteLog(submit);
            // ================================发送状态报告的消息前，需要将report对象数据封装================================
            sendMsgUtil.sendPushReport(submit);
            // // ================================抛出异常================================
            throw new StrategyException(ExceptionEnums.HAVE_DIRTY_WORD);
        }

        log.info("【策略模块-敏感词校验】   校验通过，没有敏感词信息");
    }


}
