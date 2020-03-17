package com.duck.trend;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients     //断路器
@EnableDiscoveryClient
@EnableCircuitBreaker   //分享断路信息用于监控
@Slf4j
public class TrendTradingBackTestServiceApplication
{
    public static void main(String[] args) {
        int port=0;
        int eurekaPort=8761;
        int defaultPort=8051;

        if (NetUtil.isUsableLocalPort(eurekaPort)){
            //log.error("端口{}未启动，启动失败",eurekaPort);
            System.exit(1);
        }

        if (args.length!=0||args!=null){
            for (String arg:args){
                if (arg.startsWith("port=")){
                    String strPort= StrUtil.subAfter(arg,"port=", true);
                    if (NumberUtil.isNumber(strPort)){
                        port= Convert.toInt(strPort);
                    }
                }
            }
        }

        if(0==port) {
            Future<Integer> future = ThreadUtil.execAsync(() ->{
                int p = 0;
                System.out.printf("请于5秒钟内输入端口号, 推荐  %d ,超过5秒将默认使用 %d ",defaultPort,defaultPort);
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    String strPort = scanner.nextLine();
                    if(!NumberUtil.isInteger(strPort)) {
                        System.err.println("只能是数字");
                        continue;
                    }
                    else {
                        p = Convert.toInt(strPort);
                        scanner.close();
                        break;
                    }
                }
                return p;
            });
            try{
                port=future.get(5,TimeUnit.SECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e){
                port = defaultPort;
            }
        }

        if (!NetUtil.isUsableLocalPort(port)){
            //log.error("端口%d被占用，%n启动失败");
            System.exit(1);
        }

        new SpringApplicationBuilder(TrendTradingBackTestServiceApplication.class).properties("server.port="+port).run(args);

    }
    //zikin链路追踪
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }


    //解决短路监控无法连接
    @Value("${server.port}")
    String port;
    @RequestMapping("/sayHello")
    @HystrixCommand(fallbackMethod = "sayHelloFallback")
    public String home(@RequestParam String name) {
        return "hello, " + name + " from port:" +port;
    }

    public String sayHelloFallback(String name) {
        return "error, " + name;
    }

    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }

}
