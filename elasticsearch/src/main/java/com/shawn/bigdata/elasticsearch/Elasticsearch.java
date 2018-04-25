/**  
 * Project Name:codegengerator  
 * File Name:Elasticsearch.java  
 * Package Name:com.shawn.bigdata.elasticsearch  
 * Date:2017年10月11日上午11:23:24  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**  
 * ClassName:Elasticsearch <br/>  
 * Function: TODO ADD FUNCTION. <br/>   
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年10月11日 上午11:23:24 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class Elasticsearch {

    public TransportClient transportClient;
    
    public PreBuiltXPackTransportClient xpackTransportClient;
    
    @Before
    public void getXPackClient() throws UnknownHostException{
        Settings settings = Settings.builder().put("cluster.name", "es5")
                .put("xpack.security.user", "elastic:changeme").build();
        xpackTransportClient = new PreBuiltXPackTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        xpackTransportClient.addTransportAddress(inetSocketTransportAddress);
    }
    
    @Before
    public void getClient() throws UnknownHostException{
        Settings settings = Settings.builder().put("cluster.name", "es5").build();
        transportClient = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        transportClient.addTransportAddress(inetSocketTransportAddress);
    }
    
    @Test
    public void test1(){
        System.out.println(transportClient.toString());
    }
    
    @Test
    public void test2() throws UnknownHostException{
        Settings settings = Settings.builder().put("cluster.name", "es5").build();
        PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress1 = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        InetSocketTransportAddress inetSocketTransportAddress2 = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.106"), 9300);
        InetSocketTransportAddress inetSocketTransportAddress3 = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.107"), 9300);
        //可加入多个地址，最少加入一个地址
        preBuiltTransportClient.addTransportAddresses(inetSocketTransportAddress1, inetSocketTransportAddress2, inetSocketTransportAddress3);
        System.out.println(preBuiltTransportClient.toString());
        preBuiltTransportClient.close();
    }
    
    @Test
    public void test3() throws UnknownHostException{
        Settings settings = Settings.builder()
                .put("cluster.name", "es5")
                .put("client.transport.sniff", true) //开启集群嗅探功能，只需指定集群中一个节点信息，即可获得集群中所有节点信息
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        client.addTransportAddress(inetSocketTransportAddress);
        
        //获取已连接的节点信息
        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        for(DiscoveryNode node : connectedNodes){
            System.out.println(node.getHostName()+","+ node.getHostAddress());
        }
        client.close();
    }
    
    //(4种格式json,map,bean,es XContentFactory)通过XContentFactory方式创建索引
    @Test
    public void test4() throws IOException{
        Settings settings = Settings.builder()
                .put("cluster.name", "es5")
                .put("client.transport.sniff", true) //开启集群嗅探功能，只需指定集群中一个节点信息，即可获得集群中所有节点信息
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        client.addTransportAddress(inetSocketTransportAddress);
        
        //获取已连接的节点信息
        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        for(DiscoveryNode node : connectedNodes){
            System.out.println(node.getHostName()+","+ node.getHostAddress());
        }
        
        String index = "indexone";
        String type = "typeone";

        //创建json方式1
        //Map<String, Object> map = new HashMap<String, Object>();
        //map.put("user", "shawn");
        //map.put("postDate", new Date());
        //map.put("message", "create index haha...");
        //ObjectMapper objectMapper = new ObjectMapper();
        //String json = objectMapper.writeValueAsString(map);
        //创建json方式2,XContentFactory
        
        IndexResponse indexResponse = client.prepareIndex(index, type, "1")
            .setSource(XContentFactory
                    .jsonBuilder()
                    .startObject()
                    .field("user", "shawn")
                    .field("postDate", new Date())
                    .field("message", "create index index_one").endObject()).get();
        
        String _index = indexResponse.getIndex();
        String _type = indexResponse.getType();
        long _version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
        System.out.println("_index:"+_index);
        System.out.println("_type:"+_type);
        System.out.println("_version:"+_version);
        System.out.println("status:"+status);
        client.close();
    }
    
    //(4种格式json,map,bean,es XContentFactory)通过json方式创建索引
    @Test
    public void test5() throws IOException{
        Settings settings = Settings.builder()
                .put("cluster.name", "es5")
                .put("client.transport.sniff", true) //开启集群嗅探功能，只需指定集群中一个节点信息，即可获得集群中所有节点信息
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        client.addTransportAddress(inetSocketTransportAddress);
        
        //获取已连接的节点信息
        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        for(DiscoveryNode node : connectedNodes){
            System.out.println(node.getHostName()+","+ node.getHostAddress());
        }
        
        String index = "indexone";
        String type = "typeone";

        //创建json方式1
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "shawn1");
        map.put("postDate", new Date());
        map.put("message", "create index haha...");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        
        IndexResponse indexResponse = client.prepareIndex(index, type, "2")
            .setSource(json, XContentType.JSON).get();
        
        String _index = indexResponse.getIndex();
        String _type = indexResponse.getType();
        long _version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
        System.out.println("_index:"+_index);
        System.out.println("_type:"+_type);
        System.out.println("_version:"+_version);
        System.out.println("status:"+status);
        client.close();
    }
    
    //(4种格式json,map,bean,es XContentFactory)通过map方式创建索引
    @Test
    public void test6() throws IOException{
        Settings settings = Settings.builder()
                .put("cluster.name", "es5")
                .put("client.transport.sniff", true) //开启集群嗅探功能，只需指定集群中一个节点信息，即可获得集群中所有节点信息
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        client.addTransportAddress(inetSocketTransportAddress);
        
        //获取已连接的节点信息
        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        for(DiscoveryNode node : connectedNodes){
            System.out.println(node.getHostName()+","+ node.getHostAddress());
        }
        
        String index = "indexone";
        String type = "typeone";

        //创建map方式
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "shawn3");
        map.put("postDate", new Date());
        map.put("message", "create index haha...");
        //ObjectMapper objectMapper = new ObjectMapper();
        //String json = objectMapper.writeValueAsString(map);
        
        IndexResponse indexResponse = client.prepareIndex(index, type, "6")
            .setSource(map, XContentType.JSON).get();
        
        String _index = indexResponse.getIndex();
        String _type = indexResponse.getType();
        long _version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
        System.out.println("_index:"+_index);
        System.out.println("_type:"+_type);
        System.out.println("_version:"+_version);
        System.out.println("status:"+status);
        client.close();
    }
    
  //(4种格式json,map,bean,es XContentFactory)通过bean方式创建索引
    @Test
    public void test7() throws IOException{
        Settings settings = Settings.builder()
                .put("cluster.name", "es5")
                .put("client.transport.sniff", true) //开启集群嗅探功能，只需指定集群中一个节点信息，即可获得集群中所有节点信息
                .build();
        PreBuiltTransportClient client = new PreBuiltTransportClient(settings);
        InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName("192.168.20.105"), 9300);
        client.addTransportAddress(inetSocketTransportAddress);
        
        //获取已连接的节点信息
        List<DiscoveryNode> connectedNodes = client.connectedNodes();
        for(DiscoveryNode node : connectedNodes){
            System.out.println(node.getHostName()+","+ node.getHostAddress());
        }
        
        String index = "indexone";
        String type = "typeone";

        //创建bean方式
        //Map<String, Object> map = new HashMap<String, Object>();
        //map.put("user", "shawn3");
        //map.put("postDate", new Date());
        //map.put("message", "create index haha...");
        ObjectMapper objectMapper = new ObjectMapper();
        Person person = new Person();
        person.setName("shawn4");
        person.setAge(18);
        person.setHobby("girl");
        person.setNumber("18664863325");
        person.setSex("boy");
        String json = objectMapper.writeValueAsString(person);
        
        IndexResponse indexResponse = client.prepareIndex(index, type, "7")
            .setSource(json, XContentType.JSON).get();
        
        String _index = indexResponse.getIndex();
        String _type = indexResponse.getType();
        long _version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
        System.out.println("_index:"+_index);
        System.out.println("_type:"+_type);
        System.out.println("_version:"+_version);
        System.out.println("status:"+status);
        client.close();
    }
    
    //get查询
    //通过id查询
    @Test
    public void test8(){
        String index = "indexone";
        String type = "typeone";
        GetResponse getResponse = transportClient.prepareGet(index, type, "3").get();
        System.out.println(getResponse.getSourceAsString());
    }
    
    //局部更新
    @Test
    public void test9() throws IOException{
        String index = "indexone";
        String type = "typeone";
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("user", "shawn9").endObject();
        UpdateResponse updateResponse = transportClient.prepareUpdate(index, type, "3").setDoc(builder).get();
        System.out.println(updateResponse.getVersion());
    }
    
    //通过id删除
    @Test
    public void test10(){
        String index = "indexone";
        String type = "typeone";
        DeleteResponse deleteResponse = transportClient.prepareDelete(index, type, "1").get();
        System.out.println(deleteResponse.getResult());
    }
    
    //bulk批量操作
    @Test
    public void test11() throws IOException{
        String index = "indexone";
        String type = "typeone";
        BulkRequestBuilder prepareBulk = transportClient.prepareBulk();
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("user", "shawn11").field("food", "beaf").endObject();
        IndexRequest indexRequest = new IndexRequest(index, type, "5").source(builder);
        
        DeleteRequest deleteRequest = new DeleteRequest(index, type, "2");
        
        XContentBuilder updateBuilder = XContentFactory.jsonBuilder().startObject().field("user", "shawn333333").field("food", "beaf").endObject();
        UpdateRequest updateRequest = new UpdateRequest(index, type, "3").doc(updateBuilder);
        
        prepareBulk.add(indexRequest);
        prepareBulk.add(deleteRequest);
        prepareBulk.add(updateRequest);
        
        BulkResponse bulkResponse = prepareBulk.get();
        
        if(bulkResponse.hasFailures()){
            BulkItemResponse[] items = bulkResponse.getItems();
            
            for(BulkItemResponse bulkItemResponse : items){
                System.out.println(bulkItemResponse.getFailureMessage());
            }
        }else{
            System.out.println("all command execute successfully");
        }
    }
    
    //elasticsearch 四种查询类型和搜索原理
    /**
     * QUERY_AND_FETCH //只追求查询性能的时候选择
     * QUERY_THEN_FETCH //默认 
     * DFS_QUERY_AND_FETCH //只要排名准确即可
     * DFS_QUERY_THEN_FETCH //对效率要求不是非常高,对查询精度要求非常高
     *  
     */
    @Test
    public void test12(){
        String index = "indexone";
        String type = "typeone";
        SearchResponse searchResponse = transportClient.prepareSearch(index)
            .setTypes(type)
            .setQuery(QueryBuilders.matchQuery("food", "beaf"))
            .setSearchType(SearchType.QUERY_THEN_FETCH)
            .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        //获取满足条件的数据详细内容
        SearchHit[] hits2 = hits.getHits();
        for(SearchHit searchHit : hits2){
            System.out.println(searchHit.getSourceAsString());
        }
        
    }
    
    //x-pack
    @Test
    public void test13(){
        String index = "indexone";
        String type = "typeone";
        SearchResponse searchResponse = xpackTransportClient.prepareSearch(index)
            .setTypes(type)
            .setQuery(QueryBuilders.matchQuery("food", "beaf"))
            .setSearchType(SearchType.QUERY_THEN_FETCH)
            .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        //获取满足条件的数据详细内容
        SearchHit[] hits2 = hits.getHits();
        for(SearchHit searchHit : hits2){
            System.out.println(searchHit.getSourceAsString());
        }
        
    }
    
    //search查询详情
    @Test
    public void test14(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchQuery("food", "beaf"))     //指定查询条件，不支持通配符 * ?
        .setExplain(true)   //按查询数据的匹配度返回数据
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits2){
            System.out.println(searchHit.getSourceAsString());
        }
    }
    
    //查询所有 matchAllQuery
    @Test
    public void test15(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchAllQuery())
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //multiMatchQuery
    @Test
    public void test16(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.multiMatchQuery("shawn4", "user", "name"))
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //queryStringQuery ?匹配一个,*匹配多个
    @Test
    public void test17(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.queryStringQuery("user:shawn*"))        //支持lucene语法,支持通配符 *,?
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //组合查询 must、should、mustNot,must相当于 and,should相当于or
    @Test
    public void test18(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("food", "beaf")).must(QueryBuilders.matchQuery("user", "shawn11")))        //组合查询
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    
    //组合查询 must、should、mustNot,must相当于 and,should相当于or
    @Test
    public void test19(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("user", "shawn")).should(QueryBuilders.matchQuery("user", "shawn11")))        //组合查询
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //组合查询 must、should、mustNot,must相当于 and,should相当于or
    //设置权重
    @Test
    public void test20(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("user", "shawn")).should(QueryBuilders.matchQuery("user", "shawn11")).boost(1.0f))        //组合查询，设置权重
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //精确查询termQuery
    @Test
    public void test21(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.termQuery("name", "shawn xiao"))    //精确查询 主要针对人名、地名
        //注意：一般需要精确查询的字段，在存储的时候都不建议分词。但是已经分词了，还想精确查询，使用queryStringQuery，在需要精确查询的词语外面用双引号引起来，双引号需要转义
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //精确查询queryStringQuery
    //在需要精确查询的词语外面用双引号引起来，双引号需要转义
    @Test
    public void test22(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.queryStringQuery("name：\"shawn xiao\""))    //精确查询 主要针对人名、地名
        //注意：一般需要精确查询的字段，在存储的时候都不建议分词。但是已经分词了，还想精确查询，使用queryStringQuery，在需要精确查询的词语外面用双引号引起来，双引号需要转义
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //OR的意思是shawn xiao两个词任何一个存在都可以
    //AND的意思是shawn xiao两个词必须都有
    @Test
    public void test23(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchQuery("user", "shawn shawn11").operator(Operator.OR))
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //OR的意思是shawn xiao两个词任何一个存在都可以
    //AND的意思是shawn xiao两个词必须都有
    @Test
    public void test24(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchQuery("user", "shawn shawn11").operator(Operator.AND))
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //实现分页
    @Test
    public void test25(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        //.setQuery(QueryBuilders.matchQuery("user", "shawn shawn11").operator(Operator.AND))
        .setFrom(0)     //实现分页,默认从0开始,即第一行
        .setSize(5)     //查询多少条
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //实现排序 ASC,DESC
    @Test
    public void test26(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        //.setQuery(QueryBuilders.matchQuery("user", "shawn shawn11").operator(Operator.AND))
        .setFrom(0)     //实现分页,默认从0开始,即第一行
        .setSize(5)     //查询多少条
        .addSort("age", SortOrder.DESC)
        //.setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    
    //实现过滤
    //from(18) to(19), gte(18) lte(19), gt(18) lt(19)
    @Test
    public void test27(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        //.setQuery(QueryBuilders.matchQuery("user", "shawn shawn11").operator(Operator.AND))
        .setFrom(0)     //实现分页,默认从0开始,即第一行
        .setSize(10)     //查询多少条
        .addSort("age", SortOrder.DESC)
        //.setExplain(true)
        
        //过滤
        //.setPostFilter(QueryBuilders.rangeQuery("age").from(2).to(18).includeLower(true).includeUpper(true))
        //.setPostFilter(QueryBuilders.rangeQuery("age").gte(2).lte(18))
        .setPostFilter(QueryBuilders.rangeQuery("age").gt(2).lt(18))
        .setExplain(true)
        .get();
        
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("totalHits:" + totalHits);
        
        SearchHit[] hits2 = hits.getHits();
        System.out.println("hits2:" + hits2.length);
        for(SearchHit searchHit : hits){
            System.out.println("searchHit:" + searchHit.getSourceAsString());
        }
    }
    //实现分组求count
    @Test
    public void test28(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchAllQuery())
        //添加分组字段
        .addAggregation(AggregationBuilders.terms("term_age").field("age").size(10)) //设置为0默认会返回所有分组的数据
        .get();
        
        //获取分组信息
        Terms terms = searchResponse.getAggregations().get("term_age");
        List<? extends Bucket> buckets = terms.getBuckets();
        for(Bucket bucket :buckets){
            System.out.println(bucket.getKey() + "--"+bucket.getDocCount());
        }
    }
    
    
    //实现分组求sum
    @Test
    public void test29(){
        String index = "indexone";
        String type = "typeone";
        
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchAllQuery())
        //添加分组字段
        .addAggregation(AggregationBuilders.terms("term_name").field("name").size(10)
        .subAggregation(AggregationBuilders.sum("sum_age").field("age"))) //设置为0默认会返回所有分组的数据
        .get();
        
        //获取分组信息
        Terms terms = searchResponse.getAggregations().get("term_name");
        List<? extends Bucket> buckets = terms.getBuckets();
        for(Bucket bucket :buckets){
            Sum sum = bucket.getAggregations().get("sum_age");
            System.out.println(bucket.getKey() + "--"+sum.getValue());
        }
    }
    
    
    //多索引库多类型查询
    @Test
    public void test30(){
        String index = "indexone";
        String type = "typeone";
        
        //指定一个或多个索引库，支持通配符
        //SearchResponse searchResponse = transportClient.prepareSearch("indexone*")
        //指定一个或多个索引库，支持通配符
        SearchResponse searchResponse = transportClient.prepareSearch("indexone","indextwo")
        //指定一个或者多个类型，但不支持通配符
        .setTypes(type)
        //指定一个或者多个类型，但不支持通配符
        .setTypes("typeone", "typetwo")
        .setQuery(QueryBuilders.matchAllQuery())
        //添加分组字段
        .addAggregation(AggregationBuilders.terms("term_name").field("name").size(10)
        .subAggregation(AggregationBuilders.sum("sum_age").field("age"))) //设置为0默认会返回所有分组的数据
        .get();
        
        //获取分组信息
        Terms terms = searchResponse.getAggregations().get("term_name");
        List<? extends Bucket> buckets = terms.getBuckets();
        for(Bucket bucket :buckets){
            Sum sum = bucket.getAggregations().get("sum_age");
            System.out.println(bucket.getKey() + "--"+sum.getValue());
        }
    }
    
    //java操作settings,创建索引库，指定分片数量和副本数量
    @Test
    public void test31(){
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("number_of_shards", 5);
        settings.put("number_of_replicas", 0);
        CreateIndexRequestBuilder prepareCreate = transportClient.admin().indices().prepareCreate("index");
        prepareCreate.setSettings(settings).get();
    }
    
    //java操作settings,更新索引库的副本数量
    @Test
    public void test32(){
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("number_of_replicas", 1);
        CreateIndexRequestBuilder prepareCreate = transportClient.admin().indices().prepareCreate("index");
        prepareCreate.setSettings(settings).get();
    }
    
    //java操作mappings
    @Test
    public void test33() throws IOException{
       //settings 信息
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("number_of_replicas", 0);
        settings.put("number_of_shards", 5);
        
        //mappings 信息
        XContentBuilder mappings = XContentFactory.jsonBuilder().startObject().field("dynamic", "strict")
        .startObject("properties")
        .startObject("name")
        .field("type", "String")
        .field("analyzer", "ik_max_word")
        .endObject()
        .endObject()
        .endObject();
        
        CreateIndexRequestBuilder prepareCreate = transportClient.admin().indices().prepareCreate("love");
        prepareCreate.setSettings(settings).addMapping("emp", mappings).get();
    }
    
    
    //分片查询
    @Test
    public void test34(){
        String index = "indexone";
        String type = "typeone";
        
        SearchRequestBuilder builder = transportClient.prepareSearch(index)
        .setTypes(type)
        //.setPreference("_local")
        //.setPreference("_only_local")
        //.setPreference("_primary")
        //.setPreference("_replica")
        //.setPreference("_primary_first")
        //.setPreference("_replica_first")
        //.setPreference("_only_node")
        .setPreference("_shards:3");
      
        SearchResponse searchResponse = builder.get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits2 = hits.getHits();
        for(SearchHit searchHit : hits2){
            System.out.println(searchHit.getSourceAsString());
        }
    }
    
    //极速查询：通过路由插入数据(同一类别数据在一个分片)
    @Test
    public void test35() throws JsonProcessingException, InterruptedException{
        Person person1 = new Person("shawn1", "boy", 18, "coding1", "18664863325");
        Person person2 = new Person("shawn2", "boy", 18, "coding2", "18664863325");
        Person person3 = new Person("shawn3", "boy", 18, "coding3", "18664863326");
        Person person4 = new Person("shawn4", "boy", 18, "coding4", "18664863326");
        Person person5 = new Person("shawn5", "boy", 18, "coding5", "18664863326");
        Person person6 = new Person("shawn6", "boy", 18, "coding6", "18664863327");
    
        ArrayList<Person> arrayList = new ArrayList<Person>();
        arrayList.add(person1);
        arrayList.add(person2);
        arrayList.add(person3);
        arrayList.add(person4);
        arrayList.add(person5);
        arrayList.add(person6);
        
        
        BulkProcessor bulkProcessor = BulkProcessor.builder(transportClient, new Listener() {
            
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                
                // TODO Auto-generated method stub  
                System.out.println(request.numberOfActions());
                
            }
            
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                
                // TODO Auto-generated method stub  
                System.out.println(failure.getMessage());
            }
            
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                
                // TODO Auto-generated method stub  
                System.out.println(response.hasFailures());
            }
        }).setBulkActions(1000) //每个批次的最大数量
           .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) //每个批次的最大字节数
           .setFlushInterval(TimeValue.timeValueSeconds(5)) //每批提交时间间隔
           .setConcurrentRequests(1) //设置多少个并发处理线程
           //可以允许用户自定义当一个或者多个bulk请求失败后，该执行如何操作
           .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
           .build();
        
        
        for(Person p : arrayList){
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(p);
            bulkProcessor.add(new IndexRequest("indexone", "love")
                    .routing(p.getNumber().substring(0, 3))
                    .source(json, XContentType.JSON));
        }
        
        //阻塞至所有的请求线程处理完毕后，断开连接资源
        bulkProcessor.awaitClose(3, TimeUnit.MINUTES);
        transportClient.close();
    }
    //极速查询：通过路由查询，可以通过分片shards查询
    @Test
    public void test36(){
        String index = "indexone";
        String type = "typeone";
        SearchResponse searchResponse = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchAllQuery())
        .setRouting("123456789".substring(0, 3))
        .get();
        
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits2 = hits.getHits();
        for(SearchHit searchHit:hits2){
            System.out.println(searchHit.getSourceAsString());
        }
    }
    //高亮
    @Test
    public void test37(){
        String index = "indexone";
        String type = "type";
        SearchResponse response = transportClient.prepareSearch(index)
        .setTypes(type)
        .setQuery(QueryBuilders.matchAllQuery())
        .highlighter(new HighlightBuilder().preTags("<h2>").postTags("</h2>").field("title"))
        .execute().actionGet();
        
        SearchHits hits = response.getHits();
        SearchHit[] hits2 = hits.getHits();
        for(SearchHit hit : hits2){
           System.out.println(hit.getSourceAsString());
           System.out.println(hit.getHighlightFields());
           Text[] text = hit.getHighlightFields().get("title").getFragments();
           for(Text text1 : text){
               System.out.println(text1.string());
           }
        }
    }
    /**
     * 4种suggester
     * Term Suggester
     * Phrase Suggester
     * Completion Suggester
     * Context Suggester
     * */
    //搜索自动补全
    //搜索自动补全，可以使用CompletionSuggestionBuilder类来处理。如果使用CompletionSuggestionBuilder来做搜索提示的话需要在定义mapping的时候制定下字段的格式内容。
    //{  
    //    "expJob":{  
    //        "type":"completion",  
    //        "analyzer":"ik_smart",  
    //        "search_analyzer":"ik_smart",  
    //        "payloads":true,  
    //        "preserve_separators" : false,//那么建议将忽略空格之类的分隔符。  
    //        "preserve_position_increments" : false,//如果建议项的第一个词使用的是停用词,并且我们使用了过滤停用词的分析器，需要把该项设置成false  
    //   }  
    //} 
    @Test
    public List<String> test38(String keyword, int number){
        
        List<String> suggestList = new ArrayList<String>();
        String index = "indexone";
        String type = "typeone";
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("suggest").prefix(keyword).size(10);
        
         SearchResponse suggestResponse = transportClient.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.matchAllQuery())
                .suggest(new SuggestBuilder().addSuggestion("suggest", completionSuggestionBuilder))
                .execute()
                .actionGet();
         List<? extends Entry<? extends Option>> entries = suggestResponse.getSuggest().getSuggestion("suggest").getEntries();
         for(Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> op : entries){
             List<? extends Option> options = op.getOptions();
             if(suggestList.size() < number){
                 for(Suggest.Suggestion.Entry.Option pp : options){
                     if(suggestList.size() < number && !suggestList.contains(pp.getText().toString())){
                         suggestList.add(pp.getText().toString());
                     }
                 }
             }else{
                 break;
             }
             
         }
         return suggestList;
    }
    
    //错别词纠正
    //TermSuggest
    @Test 
    public void test39(){
        
    }
    
    //短语错别纠正
    //PhraseSuggest
    @Test
    public void test40(){
        
    }
}
  
