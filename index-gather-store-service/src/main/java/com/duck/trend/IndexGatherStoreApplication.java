package com.duck.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @Program: trendParentProject
 * @ClassName IndexGatherStoreApplication
 * @Description: 启动类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 17:49
 */
@SpringBootApplication
@EnableEurekaClient //表明是注册中心客户机
@EnableHystrix  //启动断路器
@EnableCaching  //启动缓存
public class IndexGatherStoreApplication {
    public static void main(String[] args) {
        int port=0;
        int defaultPort=8001;
        int eurekaServerPort=8761;
        int redisPort=6379;
        port= defaultPort;

        //检测eureka端口
        if (!NetUtil.isUsableLocalPort(port)){
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n"
                    , eurekaServerPort);
        }

        if(null!=args && 0!=args.length) {
            for (String arg : args) {
                if(arg.startsWith("port=")) {
                    String strPort= StrUtil.subAfter(arg,
                            "port=", true);
                    if(NumberUtil.isNumber(strPort)) {
                        port = Convert.toInt(strPort);
                    }
                }
            }
        }

        //检测本服务端口
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }

        //检测Redis是否启动
        if (NetUtil.isUsableLocalPort(redisPort)){
            System.err.printf("端口%d未启动，redis服务启动失败，退出",redisPort);
            System.exit(1);
        }

        new SpringApplicationBuilder(IndexGatherStoreApplication.class)
                .properties("server.port=" + port).run(args);
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }


}
