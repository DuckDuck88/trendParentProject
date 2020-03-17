package com.duck.trend.service;

import cn.hutool.core.collection.CollUtil;
import com.duck.trend.pojo.IndexData;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataService
 * @Description: 代码数据服务类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 19:02
 */
@Service
@CacheConfig(cacheNames = "index_datas")
public class IndexDataService {

    @Cacheable(key = "'indexData-code-'+#p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }

}
