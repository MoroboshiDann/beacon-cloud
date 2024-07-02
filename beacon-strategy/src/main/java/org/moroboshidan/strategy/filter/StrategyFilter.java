package org.moroboshidan.strategy.filter;

import org.moroboshidan.common.model.StandardSubmit;

public interface StrategyFilter {
    void strategy(StandardSubmit submit);
}
