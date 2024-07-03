package org.moroboshidan.api.filter;

import org.moroboshidan.common.model.StandardSubmit;

public interface CheckFilter {
    void check(StandardSubmit submit);
}
