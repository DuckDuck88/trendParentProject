package com.duck.trend.pojo;

import lombok.Data;

/**
 * @Program: trendParentProject
 * @ClassName IndexData
 * @Description: 指数数据实体类  例如 沪深300的具体数据
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-10 22:51
 */
@Data
public class IndexData {
    String date;
    float closePoint;
}
