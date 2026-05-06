package com.wt.complaint.manage.api.provider;

import com.wt.complaint.manage.api.model.req.common.CommonDataReq;
import com.wt.complaint.manage.api.model.resp.common.AllEnumListResp;
import com.wt.complaint.manage.api.model.resp.common.CommonOptionResp;
import com.xiaomi.youpin.infra.rpc.Result;

import java.util.List;
import java.util.Map;

/**
 * 通用信息提供�?
 * @author linjiehong
 * @date 2025/5/19 13:31
 */
public interface CommonDataProvider {
    /**
     * 获取所有枚举列�?
     * @param req 请求参数
     * @return 枚举列表
     */
    Result<AllEnumListResp> getStatusList(CommonDataReq req);

    /**
     * 获取下拉选项列表
     * @return
     */
    Result<Map<String, List<CommonOptionResp>>> getOptionList();

}
