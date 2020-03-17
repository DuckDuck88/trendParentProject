package com.duck.trend.service;

import cn.hutool.core.collection.CollUtil;
import com.duck.trend.pojo.Index;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexService
 * @Description: 指数服务类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 16:23
 */
@Service
@CacheConfig(cacheNames = "indexes")
public class IndexService {

    private List<Index> indexes;

    @Cacheable(key = "'all_codes'")
    public List<Index> get(){
        Index index=new Index();
        index.setName("无效的指令代码");
        index.setCode("000000");
        return CollUtil.toList(index);
    }
}
