package com.duck.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.duck.trend.pojo.IndexData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataService
 * @Description: 数据代码服务
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 22:53
 */
@Service
@CacheConfig(cacheNames = "index_datas")
public class IndexDataService {

    private Map<String, List<IndexData>> indexDatas=new HashMap<>();
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    IndexDataService indexDataService;

    //刷新缓存数据
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<IndexData> fresh(String code) {
        List<IndexData> indexeDatas =fetch_indexes_from_third_part(code);

        indexDatas.put(code, indexeDatas);

        System.out.println("code:"+code);
        System.out.println("indexeDatas:"+indexDatas.get(code).size());

        indexDataService.remove(code);
        return indexDataService.store(code);
    }

    //移除缓存数据
    @CacheEvict(key="'indexData-code-'+ #p0")
    public void remove(String code){

    }

    //将数据存入缓存
    @CachePut(key="'indexData-code-'+ #p0")
    public List<IndexData> store(String code){
        return indexDatas.get(code);
    }

    //从缓存获得数据
    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }

    //从第三方获得数据
    public List<IndexData> fetch_indexes_from_third_part(String code){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/"+code+".json",List.class);
        return map2IndexData(temp);
    }

    //将map转换为list
    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();

            indexData.setDate(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }
        return indexDatas;
    }

    //第三方断路后提供数据
    public List<IndexData> third_part_not_connected(String code){
        System.out.println("third_part_not_connected()");
        IndexData index= new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }

}
