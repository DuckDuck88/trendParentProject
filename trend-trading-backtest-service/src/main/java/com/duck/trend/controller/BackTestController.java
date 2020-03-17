package com.duck.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.duck.trend.pojo.AnnualProfit;
import com.duck.trend.pojo.IndexData;
import com.duck.trend.pojo.Profit;
import com.duck.trend.pojo.Trade;
import com.duck.trend.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: trendParentProject
 * @ClassName BackTestController
 * @Description:
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-14 17:02
 */
@RestController
public class BackTestController {
    @Autowired BackTestService backTestService;


    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(
            @PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold
            ,@PathVariable("sellThreshold") float sellThreshold
            ,@PathVariable("serviceCharge") float serviceCharge
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate
    ) throws Exception {
        //所有数据
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        //数据的开始时间
        String indexStartDate = allIndexDatas.get(0).getDate();
        //数据的结束时间
        String indexEndDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();

        //获取从开始时间到结束时间的数据
        allIndexDatas = filterByDateRange(allIndexDatas,strStartDate, strEndDate);

        //卖出阈值
        float sellRate = sellThreshold;
        //买入阈值
        float buyRate = buyThreshold;

        //调用simulate方法获取各项数据
        Map<String,?> simulateResult= backTestService.simulate(ma, sellRate, buyRate, serviceCharge, allIndexDatas);
        List<Profit> profits= (List<Profit>) simulateResult.get("profits");
        List<Trade> trades= (List<Trade>) simulateResult.get("trades");

        //投资年限
        float years=backTestService.getYear(allIndexDatas);
        //指数总收益
        float indexIncomeTotal = (allIndexDatas.get(allIndexDatas.size()-1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        //指数年收益
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        //趋势总收入
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        //趋势年收益
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;

        int winCount=(Integer) simulateResult.get("winCount");
        int lossCount= (Integer) simulateResult.get("lossCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");
        //每年收益
        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");

        //将所有结果放入map中，并传递给前端
        Map<String,Object> result = new HashMap<>();
        //指数数据
        result.put("indexDatas", allIndexDatas);
        //数据的开始和结束时间信息
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        //交易的收益，构建收益曲线
        result.put("profits", profits);
        //每次交易
        result.put("trades", trades);

        //交易详情模块
        result.put("years", years);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);

        //交易统计模块
        result.put("winCount", winCount);
        result.put("lossCount", lossCount);
        result.put("avgWinRate", avgWinRate);
        result.put("avgLossRate", avgLossRate);

        result.put("annualProfits", annualProfits);
        return result;
    }

    /**
    * @description: 获取某段时间内的数据
    * @params: [allIndexDatas 初始所有数据,
     * strStartDate 开始时间,
     * strEndDate  结束时间]
    * @return: java.util.List<com.duck.trend.pojo.IndexData>
    * @Author: Mr.Liu
    * @Date: 2020/3/15
    */
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return allIndexDatas;

        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);

        for (IndexData indexData : allIndexDatas) {
            Date date =DateUtil.parse(indexData.getDate());
            if(date.getTime()>=startDate.getTime() && date.getTime()<=endDate.getTime()) {
                result.add(indexData);
            }
        }
        return result;
    }
}