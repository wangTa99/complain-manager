package com.wt.complaint.manage.infrastructure.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DruidPrintSqlConfig {

    public List<Filter> proxyFilters() {
        List<Filter> filters = new ArrayList<>(3);
        filters.add(slf4jLogFilter());
        filters.add(this.statFilter());
        filters.add(this.wallFilter());
        return filters;
    }

    @Bean(name = "slf4jLogFilter")
    @ConfigurationProperties("spring.datasource.druid.filter.slf4j")
    public Slf4jLogFilter slf4jLogFilter() {
        return new Slf4jLogFilter();
    }

    private StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        // 统计慢查�?
        statFilter.setSlowSqlMillis(5000);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    private WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(new WallConfig());
        wallFilter.setLogViolation(true); // 对被认为是攻击的SQL进行LOG.error输出
        wallFilter.setThrowException(false); // 对被认为是攻击的SQL抛出SQLException
        return wallFilter;
    }

}
