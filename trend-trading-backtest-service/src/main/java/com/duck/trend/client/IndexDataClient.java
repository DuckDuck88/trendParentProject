package com.duck.trend.client;

import com.duck.trend.pojo.IndexData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataClient
 * @Description: 从 INDEX-DATA-SERVICE 微服务获取数据
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-14 16:43
 */
@FeignClient(value = "INDEX-DATA-SERVICE",fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    @GetMapping("/data/{code}")
    public List<IndexData> getIndexData(@PathVariable ("code") String code);
}
