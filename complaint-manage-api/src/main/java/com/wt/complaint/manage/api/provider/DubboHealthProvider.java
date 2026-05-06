package com.wt.complaint.manage.api.provider;

import com.xiaomi.data.push.common.Health;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * description 心跳
 *
 * @author lizhao
 * @date 2021/6/2 14:29
 */
public interface DubboHealthProvider {
    
    /**
     * 心跳检�?
     *
     * @return 心跳
     */
    Result<Health> health();
}
