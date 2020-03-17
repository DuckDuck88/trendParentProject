package com.duck.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.duck.trend.client.IndexDataClient;
import com.duck.trend.pojo.AnnualProfit;
import com.duck.trend.pojo.IndexData;
import com.duck.trend.pojo.Profit;
import com.duck.trend.pojo.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: trendParentProject
 * @ClassName BackTestService
 * @Description: 提供所有模拟回测数据的微服务
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-14 16:53
 */
@Service
@Slf4j
public class BackTestService {
    @Autowired
    IndexDataClient indexDataClient;

    //用于数据曲线
    public List<IndexData> listIndexData(String code) {
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);
//        for (IndexData indexData:result){
//            //log.info(indexData.getDate());
//        }
        return result;
    }

    /**
     * @description: 用于构造模拟收益回测曲线
     * @params: [ma MA均线,
     * sellRate 卖出点,
     * buyRate 买入点,
     * serviceCharge 手续费,
     * indexDatas 数据]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @Author: Mr.Liu
     * @Date: 2020/3/15
     */
    public Map<String, Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas) {

        List<Profit> profits = new ArrayList<>();
        List<Trade> trades = new ArrayList<>();

        float initCash = 1000;    //初始资金
        float cash = initCash;
        float share = 0; //持有的份额
        float value = 0;  //价值

        //用于收益统计模块
        int winCount = 0; //赚的次数
        int lossCount = 0;  //输的次数
        float totalWinRate = 0;//总赚的比率
        float avgWinRate = 0; //平均赚的比率
        float totalLossRate = 0;//总输的比率
        float avgLossRate = 0;//平均输的比率

        float init = 0;//初始收盘点
        if (!indexDatas.isEmpty()) {
            init = indexDatas.get(0).getClosePoint();
        }

        //从某个基金的所有的数据中逐日取出
        for (int i = 0; i < indexDatas.size(); i++) {
            //获取某日
            IndexData indexData = indexDatas.get(i);
            //收盘点价格
            float closePoint = indexData.getClosePoint();
            //均值
            float avg = getMA(i, ma, indexDatas);
            //最大值
            float max = getMax(i, ma, indexDatas);
            //增长率
            float increase_rate = closePoint / avg;
            //降低率
            float decrease_rate = closePoint / max;

            if (avg != 0) {
                //buy 超过了均线
                if (increase_rate > buyRate) {
                    //如果没买
                    if (0 == share) {
                        share = cash / closePoint;
                        cash = 0;
                        //买的时候创建收益对象，用于展示收益
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                //sell 低于了卖点
                else if (decrease_rate < sellRate) {
                    //如果还持有，没卖出
                    if (0 != share) {
                        cash = closePoint * share * (1 - serviceCharge);
                        share = 0;

                        Trade trade = trades.get(trades.size() - 1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        trade.setRate(cash / initCash); //设置收益率

                        //出售的时候记录输赢次数和比率。
                        if ((trade.getSellClosePoint() - trade.getBuyClosePoint()) > 0) {
                            totalWinRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                            winCount++;
                        } else if (((trade.getSellClosePoint() - trade.getBuyClosePoint()) < 0)) {
                            totalLossRate += (trade.getBuyClosePoint() - trade.getSellClosePoint() / trade.getBuyClosePoint());
                            lossCount++;
                        }
                    }
                }
                //do nothing
                else {

                }
            }

            if (share != 0) {
                value = closePoint * share;
            } else {
                value = cash;
            }
            float rate = value / initCash;

            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate * init);
            //log.info("profit.value:" + profit.getValue());
            profits.add(profit);
        }

        //交易全部结束之后，计算平均盈利和亏损利率
        avgWinRate = totalWinRate / winCount;
        avgLossRate = totalLossRate / lossCount;

        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);

        Map<String, Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);
        //用于交易统计模块的数据
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        //log.info("返回的数据大小:"+map.size());
        //log.info(("交易明细："+map.get("trades")));
        map.put("annualProfits", annualProfits);
        return map;
    }

    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i - 1 - day;
        if (start < 0)
            start = 0;
        int now = i - 1;

        if (start < 0)
            return 0;

        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean = list.get(j);
            if (bean.getClosePoint() > max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }

    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i - 1 - ma;
        int now = i - 1;

        if (start < 0)
            return 0;

        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean = list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }

    //获取投资年限
    public float getYear(List<IndexData> allIndexDatas) {
        float years;
        String sDateStart = allIndexDatas.get(0).getDate();
        String sDateEnd = allIndexDatas.get(allIndexDatas.size() - 1).getDate();
        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);
        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days / 365f;
        return years;
    }

    //获取日期中的年份
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }

    //计算某一年的指数收益
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
//			Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }

    //计算某一年的趋势投资收益
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }

    //计算所有时间内的指数收益和趋势投资收益
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
        }
        return result;
    }
}
