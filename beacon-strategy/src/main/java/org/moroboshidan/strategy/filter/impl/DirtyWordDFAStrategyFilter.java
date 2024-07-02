package org.moroboshidan.strategy.filter.impl;

import org.moroboshidan.common.model.StandardSubmit;
import org.moroboshidan.strategy.filter.StrategyFilter;
import org.moroboshidan.strategy.util.DFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 敏感词校验
 *
 * @author zjw
 * @description
 */
@Service(value = "dfaDirtyWord")
@Slf4j
public class DirtyWordDFAStrategyFilter implements StrategyFilter {

    @Override
    public void strategy(StandardSubmit submit) {
        log.info("【策略模块-敏感词校验】   校验ing…………");
        // 1、 获取短信内容
        String text = submit.getText();

        // 2、 调用DFA查看敏感词
        Set<String> dirtyWords = DFAUtil.getDirtyWord(text);

        // 4、 根据返回的set集合，判断是否包含敏感词
        if (dirtyWords != null && dirtyWords.size() > 0) {
            // 5、 如果有敏感词，抛出异常 / 其他操作。。
            log.info("【策略模块-敏感词校验】   短信内容包含敏感词信息， dirtyWords = {}", dirtyWords);
            // 还需要做其他处理
        }
    }
}
