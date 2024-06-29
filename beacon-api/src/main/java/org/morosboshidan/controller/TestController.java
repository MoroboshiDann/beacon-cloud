package org.morosboshidan.controller;

import org.morosboshidan.filter.CheckFilterContext;
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
        checkFilterContext.check(new Object());
        return "ok";
    }
}
