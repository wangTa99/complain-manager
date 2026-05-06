package com.wt.complaint.manage.config;

import com.xiaomi.nr.job.core.executor.impl.JobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {
    private Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value("${job.admin.addresses}")
    private String adminAddresses;

    @Value("${job.accessToken}")
    private String accessToken;

     @Value("${job.executor.appname}")
     private String appname;

    @Value("${job.executor.address}")
    private String address;

    @Value("${job.executor.port}")
    private int port;

    @Value("${job.executor.logpath}")
    private String logPath;

    @Value("${job.executor.logretentiondays}")
    private int logRetentionDays;

    @Bean
    public JobSpringExecutor jobExecutor() {
        logger.info(">>>>>>>>>>> nr-job config init.");
        JobSpringExecutor jobExecutor = new JobSpringExecutor();
        jobExecutor.setAdminAddresses(adminAddresses);
        jobExecutor.setAppname(appname);
        jobExecutor.setAddress(address);
        jobExecutor.setIp(getSystemProperty("TESLA_HOST"));
        jobExecutor.setPort(port);
        jobExecutor.setAccessToken(accessToken);
        jobExecutor.setLogPath(logPath);
        jobExecutor.setLogRetentionDays(logRetentionDays);
        return jobExecutor;
    }

    public static String getSystemProperty(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(key);
        }
        return value;
    }
}
