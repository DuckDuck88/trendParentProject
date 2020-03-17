package com.duck.trend.controller;

import com.duck.trend.config.IpConfiguration;
import com.duck.trend.pojo.IndexData;
import com.duck.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataController
 * @Description:
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 19:14
 */
@RestController
public class IndexDataController {
    @Autowired
    IndexDataService indexDataService;
    @Autowired
    IpConfiguration ipConfiguration;

    // http://127.0.0.1:8021/data/000300

    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws  Exception{
        System.out.println("当前的端口是："+ipConfiguration.getPort());
        return indexDataService.get(code);
    }
}
