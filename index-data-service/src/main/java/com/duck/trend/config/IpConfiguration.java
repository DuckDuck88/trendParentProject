package com.duck.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Program: trendParentProject
 * @ClassName IpConfiguration
 * @Description: 获取端口类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 18:55
 */
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent>{

    private int serverPort;


    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.serverPort=webServerInitializedEvent.getWebServer().getPort();
    }

    public int getPort(){
        return serverPort;
    }
}
