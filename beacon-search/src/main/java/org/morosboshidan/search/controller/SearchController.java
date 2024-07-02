package org.morosboshidan.search.controller;

import org.morosboshidan.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/sms/list")
    public Map<String, Object> findSmsByParams(@RequestBody Map<String, Object> params) throws IOException {
        // 1. 调用elasticsearch，完成搜索
        return searchService.findSmsByParams(params);
    }
}
