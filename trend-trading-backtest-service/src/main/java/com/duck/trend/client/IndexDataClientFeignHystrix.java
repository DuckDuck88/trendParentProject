package com.duck.trend.client;

import cn.hutool.core.collection.CollectionUtil;
import com.duck.trend.pojo.IndexData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataClientFeignHystrix
 * @Description: 数据访问不了的时候，找 IndexDataClientFeignHystrix 拿数据。
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-14 16:49
 */
@Component
public class IndexDataClientFeignHystrix implements IndexDataClient
{
    @Override
    public List<IndexData> getIndexData(String code) {
        IndexData indexData=new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}
