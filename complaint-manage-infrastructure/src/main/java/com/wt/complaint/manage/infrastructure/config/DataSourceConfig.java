package com.wt.complaint.manage.infrastructure.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {
    "com.wt.complaint.manage.infrastructure.mapper"}, sqlSessionFactoryRef = "megaSqlSessionFactory")
@EnableTransactionManagement
public class DataSourceConfig {

    @Resource
    private DruidPrintSqlConfig printSqlConfig;

    @Bean(name = "megaDatasource", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource.mysql.write.druid")
    @Primary
    public DataSource mysqlDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setProxyFilters(printSqlConfig.proxyFilters());
        return dataSource;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean(name = "megaSqlSessionFactory")
    @Primary
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("megaDatasource") DataSource dataSource)
        throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("com.wt.complaint.manage.infrastructure.model");

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(false);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseColumnLabel(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultStatementTimeout(60);
        configuration.setAggressiveLazyLoading(true);
        configuration.setMapUnderscoreToCamelCase(true);

        sqlSessionFactoryBean.setConfiguration(configuration);
        //分页
        sqlSessionFactoryBean.setPlugins(paginationInterceptor());

        return sqlSessionFactoryBean.getObject();

    }

    @Bean(name = "megaTransactionManager")
    @Primary
    public PlatformTransactionManager prodTransactionManager(@Qualifier("megaDatasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "megaSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
        @Qualifier("megaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

