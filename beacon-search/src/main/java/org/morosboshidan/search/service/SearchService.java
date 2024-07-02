package org.morosboshidan.search.service;

import java.io.IOException;
import java.util.Map;

public interface SearchService {
    Map<String, Object> findSmsByParams(Map<String, Object> params) throws IOException;

    void index(String index, String id, String json) throws IOException;
}
