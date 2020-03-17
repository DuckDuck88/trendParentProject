package com.duck.trend.job;

import cn.hutool.core.date.DateUtil;
import com.duck.trend.pojo.Index;
import com.duck.trend.pojo.IndexData;
import com.duck.trend.service.IndexDataService;
import com.duck.trend.service.IndexService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * @Program: trendParentProject
 * @ClassName IndexDataSyncJob
 * @Description: 任务类，同时刷新指数代码和指数数据。
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-12 00:39
 */
public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    IndexDataService indexDataService;
    @Autowired
    IndexService indexService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时启动:"+ DateUtil.now());
        List<Index> indices=indexService.fresh();
        for(Index index:indices) {
            List<IndexData> indexData = indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束:"+DateUtil.now());
    }
}
