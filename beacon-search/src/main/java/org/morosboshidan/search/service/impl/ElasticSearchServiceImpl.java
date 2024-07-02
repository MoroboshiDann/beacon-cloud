package org.morosboshidan.search.service.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.morosboshidan.search.service.SearchService;
import org.morosboshidan.search.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticSearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Map<String, Object> findSmsByParams(Map<String, Object> params) throws IOException {
        //1、声明SearchRequest(后期需要根据传递的时间指定查询哪些索引，如果没传，可以指定默认查询前三个月)
        SearchRequest request = new SearchRequest(SearchUtil.getCurrYearIndex(), "");

        //2、封装查询条件
        //2.1 参数全部取出来
        Object fromObj = params.get("from");
        Object sizeObj = params.get("size");
        Object contentObj = params.get("content");
        Object mobileObj = params.get("mobile");
        Object startTimeObj = params.get("starttime");
        Object stopTimeObj = params.get("stoptime");
        Object clientIDObj = params.get("clientID");

        //2.2 clientID需要单独操作一下。
        List<Long> clientIDList = null;
        if (clientIDObj instanceof List) {
            // 传递的是个集合
            clientIDList = (List) clientIDObj;
        } else if (!ObjectUtils.isEmpty(clientIDObj)) {
            clientIDList = Collections.singletonList(Long.parseLong(clientIDObj + ""));
        }
        //2.3 条件封装
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // ========================封装查询条件到boolQuery========================================
        //2.3.1、关键字
        if (!ObjectUtils.isEmpty(contentObj)) {
            boolQuery.must(QueryBuilders.matchQuery("text", contentObj));
            // 高亮。设置给sourceBuilder
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("text");
            highlightBuilder.preTags("<span style='color: red'>");
            highlightBuilder.postTags("</span>");
            highlightBuilder.fragmentSize(100);
            sourceBuilder.highlighter(highlightBuilder);
        }

        //2.3.2、手机号
        if (!ObjectUtils.isEmpty(mobileObj)) {
            boolQuery.must(QueryBuilders.prefixQuery("mobile", (String) mobileObj));
        }

        //2.3.3、开始时间
        if(!ObjectUtils.isEmpty(startTimeObj)){
            boolQuery.must(QueryBuilders.rangeQuery("sendTime").gte(startTimeObj));
        }

        //2.3.4、结束时间
        if(!ObjectUtils.isEmpty(stopTimeObj)){
            boolQuery.must(QueryBuilders.rangeQuery("sendTime").lte(stopTimeObj));
        }

        //2.3.5、客户id
        if(clientIDList != null){
            boolQuery.must(QueryBuilders.termsQuery("clientId",clientIDList.toArray(new Long[]{})));
        }

        //2.3.6 分页查询
        sourceBuilder.from(Integer.parseInt(fromObj + ""));
        sourceBuilder.size(Integer.parseInt(sizeObj + ""));


        // ========================封装查询条件到boolQuery========================================
        sourceBuilder.query(boolQuery);
        request.source(sourceBuilder);

        //3、执行查询
        SearchResponse resp = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //4、封装数据
        long total = resp.getHits().getTotalHits().value;
        List<Map> rows = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> row = hit.getSourceAsMap();
            List sendTime = (List) row.get("sendTime");
            String sendTimeStr = listToDateString(sendTime);
            row.put("sendTimeStr",sendTimeStr);
            row.put("corpname",row.get("sign"));
            // 高亮结果的处理
            HighlightField highlightField = hit.getHighlightFields().get("text");
            if(highlightField != null){
                String textHighLight = highlightField.getFragments()[0].toString();
                row.put("text",textHighLight);
            }
            rows.add(row);
        }
        //5、返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("total",total);
        result.put("rows",rows);
        return result;
    }

    private String listToDateString(List sendTime) {
        String year = sendTime.get(0) + "";
        Integer monthInt = (Integer) sendTime.get(1);
        Integer dayInt = (Integer) sendTime.get(2);
        Integer hourInt = (Integer) sendTime.get(3);
        Integer minuteInt = (Integer) sendTime.get(4);
        Integer secondInt = (Integer) sendTime.get(5);

        String month = monthInt / 10 == 0 ? "0" + monthInt : monthInt + "";
        String day = dayInt / 10 == 0 ? "0" + dayInt : dayInt + "";
        String hour = hourInt / 10 == 0 ? "0" + hourInt : hourInt + "";
        String minute = minuteInt / 10 == 0 ? "0" + minuteInt : minuteInt + "";
        String second = secondInt / 10 == 0 ? "0" + secondInt : secondInt + "";
        return year + "-" + month + "-" + day + " " + hour + ":" + month + ":" + second;
    }
    
}
