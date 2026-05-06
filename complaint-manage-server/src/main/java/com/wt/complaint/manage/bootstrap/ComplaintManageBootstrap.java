package com.wt.complaint.manage.bootstrap;

import com.google.common.base.Stopwatch;
import com.wt.car.common.privacy.annotation.EnablePrivacyData;
import com.wt.car.common.watermark.annotation.EnableWatermark;
import com.wt.proretail.newcommon.keycenter.util.EnableIAuthSpringUtil;
import com.wt.proretail.newcommon.notice.NoticeClient;
import com.xiaomi.mone.dubbo.docs.EnableDubboApiDocs;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * @author zhangruke
 */
@Slf4j
@EnablePrivacyData
@EnableWatermark
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.wt.complaint.manage", "com.xiaomi.youpin", "com.xiaomi.mone"})
@DubboComponentScan(basePackages = "com.wt.complaint.manage")
@EnableScheduling
@EnableDubboApiDocs
@EnableIAuthSpringUtil
public class ComplaintManageBootstrap {


    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean success;
        long elapsed = 0;
        try {
            SpringApplication.run(ComplaintManageBootstrap.class);
            elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
            log.info("start success");
            success = true;
        } catch (Exception throwable) {
            success = false;
            elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
            log.error("#PcBootstrap.main#1 error:", throwable);
        }
        
        try {
            Resource resource = new ClassPathResource("application.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            String appName = props.getProperty("app.name");
            String env = props.getProperty("server.type");
            NoticeClient.sendStartMessage(appName, env, success, (int) elapsed);
        } catch (IOException e) {
            log.error("#PcBootstrap.main#2 error:", e);
        }
        if (!success) {
            System.exit(-1);
        }
    }
}
