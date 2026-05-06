package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.provider.DubboHealthProvider;
import com.xiaomi.data.push.common.Health;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.qps.QpsAop;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author lizhao description DubboHealthProviderImpl
 * @date 2021/6/2 11:59
 */
@DubboService(timeout = 1000, group = "${dubbo.group}", version = "1.0")
public class DubboHealthProviderImpl implements DubboHealthProvider {
    
    @Resource
    private QpsAop qpsAop;

    @ApiDoc(description = "健康检�?, value = "health")
    @Override
    public Result<Health> health() {
        long qps = qpsAop.getQps();
        return Result.success(new Health("0.0.1", "2019-11-11", qps));
    }
}
