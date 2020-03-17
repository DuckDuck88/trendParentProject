package com.duck.trend.pojo;

import lombok.Data;

/**
 * @Program: trendParentProject
 * @ClassName AnnualProfit
 * @Description: 每年收益实体类
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-15 22:00
 */
@Data
public class AnnualProfit {
        private int year; //年份
        private float indexIncome;//指数收益
        private float trendIncome;//趋势收益

}
