package org.morosboshidan.search.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.moroboshidan.common.constant.RabbitMQConstant;
import org.moroboshidan.common.enums.ExceptionEnums;
import org.moroboshidan.common.exception.SearchException;
import org.moroboshidan.common.model.StandardReport;
import org.morosboshidan.search.service.SearchService;
import org.morosboshidan.search.util.SearchUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ElasticSearchServiceImpl implements SearchService {
    /**
     * 添加成功的result
     */
    private final String CREATED = "created";

    /**
     * 修改成功的result
     */
    private final String UPDATED = "updated";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
     * @description: 根据参数查询ES，获取日志信息
     * @param params
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @author: MoroboshiDan
     * @time: 2024/7/2 19:54
     */
    @Override
    public Map<String, Object> findSmsByParams(Map<String, Object> params) throws IOException {
        // 1、声明SearchRequest(后期需要根据传递的时间指定查询哪些索引，如果没传，可以指定默认查询前三个月)
        SearchRequest request = new SearchRequest(SearchUtil.getCurrYearIndex(), "");
        // 2、封装查询条件
        // 2.1 参数全部取出来
        Object fromObj = params.get("from");
        Object sizeObj = params.get("size");
        Object contentObj = params.get("content");
        Object mobileObj = params.get("mobile");
        Object startTimeObj = params.get("starttime");
        Object stopTimeObj = params.get("stoptime");
        Object clientIDObj = params.get("clientID");

        // 2.2 clientID需要单独操作一下。
        List<Long> clientIDList = null;
        if (clientIDObj instanceof List) {
            // 传递的是个集合
            clientIDList = (List) clientIDObj;
        } else if (!ObjectUtils.isEmpty(clientIDObj)) {
            clientIDList = Collections.singletonList(Long.parseLong(clientIDObj + ""));
        }
        // 2.3 条件封装
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // ========================封装查询条件到boolQuery========================================
        // 2.3.1、关键字
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

        // 2.3.2、手机号
        if (!ObjectUtils.isEmpty(mobileObj)) {
            boolQuery.must(QueryBuilders.prefixQuery("mobile", (String) mobileObj));
        }

        // 2.3.3、开始时间
        if (!ObjectUtils.isEmpty(startTimeObj)) {
            boolQuery.must(QueryBuilders.rangeQuery("sendTime").gte(startTimeObj));
        }

        // 2.3.4、结束时间
        if (!ObjectUtils.isEmpty(stopTimeObj)) {
            boolQuery.must(QueryBuilders.rangeQuery("sendTime").lte(stopTimeObj));
        }

        // 2.3.5、客户id
        if (clientIDList != null) {
            boolQuery.must(QueryBuilders.termsQuery("clientId", clientIDList.toArray(new Long[]{})));
        }

        // 2.3.6 分页查询
        sourceBuilder.from(Integer.parseInt(fromObj + ""));
        sourceBuilder.size(Integer.parseInt(sizeObj + ""));


        // ========================封装查询条件到boolQuery========================================
        sourceBuilder.query(boolQuery);
        request.source(sourceBuilder);

        // 3、执行查询
        SearchResponse resp = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 4、封装数据
        long total = resp.getHits().getTotalHits().value;
        List<Map> rows = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> row = hit.getSourceAsMap();
            List sendTime = (List) row.get("sendTime");
            String sendTimeStr = listToDateString(sendTime);
            row.put("sendTimeStr", sendTimeStr);
            row.put("corpname", row.get("sign"));
            // 高亮结果的处理
            HighlightField highlightField = hit.getHighlightFields().get("text");
            if (highlightField != null) {
                String textHighLight = highlightField.getFragments()[0].toString();
                row.put("text", textHighLight);
            }
            rows.add(row);
        }
        // 5、返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", rows);
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

    /*
     * @description: 给指定的ES索引下，添加日志信息
     * @param index
     * @param id
     * @param json
     * @return: void
     * @author: MoroboshiDan
     * @time: 2024/7/2 19:55
     */
    @Override
    public void index(String index, String id, String json) throws IOException {
        // 1、构建插入数据的Request
        IndexRequest request = new IndexRequest();

        // 2、给request对象封装索引信息，文档id，以及文档内容
        request.index(index);
        request.id(id);
        request.source(json, XContentType.JSON);

        // 3、将request信息发送给ES服务
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        // 4、校验添加是否成功
        String result = response.getResult().getLowercase();
        if (!CREATED.equals(result)) {
            // 添加失败！！
            log.error("【搜索模块-写入数据失败】 index = {},id = {},json = {},result = {}", index, id, json, result);
            throw new SearchException(ExceptionEnums.SEARCH_INDEX_ERROR);
        }
        log.info("【搜索模块-写入数据成功】 索引添加成功index = {},id = {},json = {},result = {}", index, id, json, result);
    }

    /**
     * @param index
     * @param id
     * @param doc
     * @description: 更新ES日志，修改短信发送状态
     * @return: void
     * @author: MoroboshiDan
     * @time: 2024/7/3 15:15
     */
    @Override
    public void update(String index, String id, Map<String, Object> doc) throws IOException {
        // 1、基于exists方法，查询当前文档是否存在
        boolean exists = exists(index, id);
        if (!exists) {
            // 当前文档不存在
            StandardReport report = SearchUtil.get();
            if (report.getReUpdate()) {
                // 第二次获取投递的消息，到这已经是延迟20s了。
                log.error("【搜索模块-修改日志】 修改日志失败，report = {}", report);
            } else {
                // 第一次投递，可以再次将消息仍会MQ中
                // 开始第二次消息的投递了
                report.setReUpdate(true);
                rabbitTemplate.convertAndSend(RabbitMQConstant.SMS_GATEWAY_NORMAL_QUEUE, report);
            }
            SearchUtil.remove();
            return;
        }
        // 2、到这，可以确认文档是存在的，直接做修改操作
        UpdateRequest request = new UpdateRequest();

        request.index(index);
        request.id(id);
        request.doc(doc);

        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        String result = update.getResult().getLowercase();
        if (!UPDATED.equals(result)) {
            // 添加失败！！
            log.error("【搜索模块-修改日志失败】 index = {},id = {},doc = {}", index, id, doc);
            throw new SearchException(ExceptionEnums.SEARCH_UPDATE_ERROR);
        }
        log.info("【搜索模块-修改日志成功】 文档修改成功index = {},id = {},doc = {}", index, id, doc);

    }

    /**
     * @param index
     * @param id
     * @description: 判断当前索引下，是否包含该数据
     * @return: boolean
     * @author: MoroboshiDan
     * @time: 2024/7/3 15:19
     */
    @Override
    public boolean exists(String index, String id) throws IOException {
        // 构建GetRequest，查看索引是否存在
        GetRequest request = new GetRequest();

        // 指定索引信息，还有文档id
        request.index(index);
        request.id(id);

        // 基于restHighLevelClient将查询指定id的文档是否存在的请求投递过去。
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);

        // 直接返回信息
        return exists;
    }
}
