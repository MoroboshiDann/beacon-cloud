package org.moroboshidan.controller;

import org.moroboshidan.filter.CheckFilterContext;
import org.moroboshidan.common.model.StandardSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    public CheckFilterContext checkFilterContext;

    @GetMapping("/test-filter")
    public String testFilter() {
        checkFilterContext.check(new StandardSubmit());
        return "ok";
    }
}
