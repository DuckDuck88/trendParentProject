package com.duck.trend.pojo;

import lombok.Data;

/**
 * @Program: trendParentProject
 * @ClassName Trade
 * @Description: 交易实体类，用于交易明细
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-15 15:27
 */
@Data
public class Trade {
    private String buyDate; //购买日期
    private String sellDate;    //卖出日期
    private float buyClosePoint; //买时收盘点
    private float sellClosePoint;//卖时
    private float rate; //收益比率;
}
