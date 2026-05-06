package com.wt.complaint.manage.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import org.springframework.context.annotation.Configuration;

/**
 * description NacosConfiguration
 *
 * @author lizhao
 * @date 2021/6/2 12:59
 */
@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySources({
        @NacosPropertySource(dataId = "test", autoRefreshed = true),
        @NacosPropertySource(dataId = "MESSAGE_CONFIG", autoRefreshed = true),
        @NacosPropertySource(dataId = "COMPLAINT_CLOSINGTAG_CONFIG", autoRefreshed = true),
        @NacosPropertySource(dataId = "DELIVER_RETAIL_CONFIG", autoRefreshed = true)
})
public class NacosConfiguration {

}
