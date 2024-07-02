package org.moroboshidan.filter;

import org.moroboshidan.common.model.StandardSubmit;

public interface CheckFilter {
    void check(StandardSubmit submit);
}
