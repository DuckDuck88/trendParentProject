package com.duck.trend.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;

/**
 * @Program: trendParentProject
 * @ClassName AccessService
 * @Description: 辅助工具类，用于测试断路监控。
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-16 21:20
 */
public class AccessService {

    public static void main(String[] args) {

        while(true) {
            ThreadUtil.sleep(1000);
            access(8051);
            access(8052);
        }
    }

    public static void access(int port) {
        try {
            String html= HttpUtil.get(String.format("http://127.0.0.1:%d/simulate/399975/20/1.01/0.99/0/null/null/",port));
            System.out.printf("%d 地址的模拟回测服务访问成功，返回大小是 %d%n" ,port, html.length());
        }
        catch(Exception e) {
            System.err.printf("%d 地址的模拟回测服务无法访问%n",port);
        }
    }
}
