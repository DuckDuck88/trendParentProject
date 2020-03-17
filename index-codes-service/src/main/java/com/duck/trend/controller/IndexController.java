package com.duck.trend.controller;

import com.duck.trend.config.IpConfiguration;
import com.duck.trend.pojo.Index;
import com.duck.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexController
 * @Description: index控制类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 17:13
 */
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    IpConfiguration ipConfiguration;

    //  http://127.0.0.1:8011/codes

    @GetMapping("/codes")
    @CrossOrigin  //允许跨域，后续的回测视图是另一个端口号，访问这个服务属于跨域。
    public List<Index> codes() throws Exception{
        System.out.println("当前的端口是："+ipConfiguration.getPort());
        return indexService.get();
    }

}
