package com.duck.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Program: trendParentProject
 * @ClassName EurekaServerApplication
 * @Description: 注册服务启动类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 16:07
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        //8761端口是注册服务默认端口
        int port=8761;
        if (!NetUtil.isUsableLocalPort(port)){
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(EurekaServerApplication.class)
                .properties("server.port=" + port).run(args);
    }
}
