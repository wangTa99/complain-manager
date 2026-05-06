package com.wt.complaint.manage.infrastructure.outer.nacos;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * DESC : nacos动态配�?
 *
 * @author lizhao
 * @date 2021/6/2 16:22
 */
@Data
@Service
@NacosPropertySource(dataId = "dynamic_config", autoRefreshed = true)
public class ApkVersionNacoscfgResult {

    @NacosValue(value = "${apk.version.ios:''}", autoRefreshed = true)
    private String apkVersionIos;
    
    @NacosValue(value = "${apk.version.android:''}", autoRefreshed = true)
    private String apkVersionAndroid;
    
    @NacosValue(value = "${useRedisCache:''}", autoRefreshed = true)
    private String useRedisCache;
    
    
}
