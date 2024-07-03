package org.morosboshidan.search.service;

import java.io.IOException;
import java.util.Map;

public interface SearchService {
    Map<String, Object> findSmsByParams(Map<String, Object> params) throws IOException;

    void index(String index, String id, String json) throws IOException;

    void update(String index, String id, Map<String, Object> doc) throws IOException;

    boolean exists(String index, String id) throws IOException;
}
