package com.duck.trend.util;

import cn.hutool.http.HttpUtil;

import java.util.HashMap;

/**
 * @Program: trendParentProject
 * @ClassName FreshConfigUtil
 * @Description: 使用 post 的方式访问 http://localhost:8041/actuator/bus-refresh 地址,刷新配置
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-16 20:24
 */
public class FreshConfigUtil {
    public static void main(String[] args) {
        HashMap<String,String> headers =new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        System.out.println("需要git获取配置文件，还要刷新index-config-server, 所以比较慢，请耐心等待几秒");

        String result = HttpUtil.createPost("http://localhost:8041/actuator/bus-refresh").addHeaders(headers).execute().body();
        System.out.println("result:"+result);
        System.out.println("refresh 完成");
    }
}
