package com.duck.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.duck.trend.pojo.Index;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Program: trendParentProject
 * @ClassName IndexService
 * @Description: 数据服务类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 17:42
 */
@Service
@CacheConfig(cacheNames = "indexes")
public class IndexService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    IndexService indexService;

    private List<Index> indexes;

    //刷新redis中的数据，因为第三方数据会变化，变化后缓存不会更新，所以需要刷新
    //刷新思路 1.获取数据据 2. 移除数据 3.保存数据
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        indexes =fetch_indexes_from_third_part();
        indexService.remove();
        return indexService.store();
    }

    //移除缓存数据
    @CacheEvict(allEntries=true)
    public void remove(){

    }

    //将数据放入缓存
    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        System.out.println(this);
        return indexes;
    }

    //从缓存获得数据
    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        return CollUtil.toList();
    }

    //从第三方获得数据
    public List<Index> fetch_indexes_from_third_part(){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json",List.class);
        return map2Index(temp);
    }

    //将map转换为list
    private List<Index> map2Index(List<Map> temp) {
        List<Index> indexes = new ArrayList<>();
        for (Map map : temp) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index= new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }

        return indexes;
    }

    //第三方断路后进入此方法
    public List<Index> third_part_not_connected(){
        System.out.println("third_part_not_connected()");
        Index index= new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }

}
