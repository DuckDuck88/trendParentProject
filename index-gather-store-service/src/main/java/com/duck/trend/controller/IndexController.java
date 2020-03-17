package com.duck.trend.controller;

import com.duck.trend.pojo.Index;
import com.duck.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexController
 * @Description: 数据获取控制类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 17:46
 */
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;

//  http://127.0.0.1:8001/freshCodes
//  http://127.0.0.1:8001/getCodes
//  http://127.0.0.1:8001/removeCodes

    @GetMapping("/getCodes")
    public List<Index> get() throws Exception{
        return indexService.get();
    }

    @GetMapping("/freshCodes")
    public String fresh() throws Exception{
        indexService.fresh();
        return "index fresh successfully";
    }

    @GetMapping("/removeCodes")
    public String remove() throws Exception{
        indexService.remove();;
        return "remover codes successfully";
    }



}
