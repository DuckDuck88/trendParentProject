# 趋势投资项目
开发环境：JDK8、Redis 3.2.1、IDEA 2019、Spring Cloud Finchley

### 项目介绍
为了进行投资分析以及指导投资  
本项目根据2009-2019十年左右的指数基金的涨跌信息，
来进行模拟趋势投资  
并且将模拟投资的结果和指数涨跌的结果，以图表等多种形式直观的展现出来。
#### 涉及技术
Spring Cloud、Spring Boot、Redis、RabiitMQ、Vue.js、Ajax、chartjs、hutool

### 各模块功能如下：
- **eureka-server** ：Eureka注册中心服务，实现各个服务的统一调配
- **third-part-index-data-project**：第三方数据服务，本服务模拟第三方为该项目提供json数据
- **index-gather-store-service**：指数收集保存服务，本服务和第三方数据中心对接，使用定时器工具Quartz定时将数据保存并保存到Redis中，并通过Hystrix方式实现断路熔断
- **index-codes-service**：指数代码服务，本服务从Redis中获取指数代码数据，以供使用。可集群部署
- **index-data-service**：指数数据服务，本服务从Redis中获取指数详细数据，以供使用。可集群部署
- **index-zuul-service**：网关服务，本服务提供Zuul网关，实现动态路由
- **trend-trading-backtest-service**：交易回测服务，本服务为核心业务逻辑，使用Feign方式访问index-codes-service、index-data-service服务获取数据并对数据进行处理，并且实现了Feign模式的断路器
- **trend-trading-backtest-view**：视图服务，本服务提供视图功能。
- **index-config-server**：配置中心，从Git服务器获取配置文件。并且通过RabiitMQ进行消息广播，实现不影响项目的情况下更改配置
- **index-hystrix-dashboard**：断路监控服务，通过turbine服务监控可能会发生熔断的服务，实现集群层面的监控
- **index-turbine**：断路器聚合监控服务，本服务把被监控集群里的多个实例汇聚在一个turbine里

### 计划
- 增加模拟定投、价值投资等方式投资，对比各类投资的收益
- 增加用户模块，实现用户的登陆、选指数等功能，实现不同用户的不用需求


如有相关交流问题请联系本人：QQ：1307317886  emai：1307317886@qq.com
