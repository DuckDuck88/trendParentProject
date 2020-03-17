package com.duck.trend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Program: trendParentProject
 * @ClassName ViewController
 * @Description:
 * @Version 1.0
 * @Author: Mr.Liu
 * @Create: 2020-03-14 21:24
 */
@Controller
@RefreshScope //表示允许刷新
public class ViewController {
    @Value("${version}")
    String version;
    @GetMapping("/")
    public String view(Model model) throws Exception {
        model.addAttribute("version", version);
        return "view.html";
    }
}
