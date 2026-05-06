package com.wt.complaint.manage.app.controller;

import com.wt.complaint.manage.api.provider.DemoProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description 具体使用可参�?https://github.com/alibaba/spring-cloud-alibaba/blob/master/spring-cloud-alibaba-docs/src/main/asciidoc-zh/sentinel.adoc
 * https://blog.csdn.net/weixin_42073629/article/details/107117585 这篇文章
 * @date 2021/7/2 14:13
 */
@RestController
@Slf4j
public class TestSentinelController {
    
    @Resource
    private DemoProvider demoProvider;
    
    /**
     * sentinel 测试http接口 接口名称即为 resource
     *
     * @param param
     * @return
     */
    @GetMapping("/testWebRequest")
    public String testWebRequest(String param) {
        return param + "http sentinel�?;
    }

    
    
}
