package com.duck.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Program: trendParentProject
 * @ClassName IpConfiguration
 * @Description: 获取端口号，这个服务要做成集群，不同的服务不用的端口号
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 15:46
 */
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    private  int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.serverPort=webServerInitializedEvent.getWebServer().getPort();
    }

    public int getPort(){
        return this.serverPort;
    }


}
