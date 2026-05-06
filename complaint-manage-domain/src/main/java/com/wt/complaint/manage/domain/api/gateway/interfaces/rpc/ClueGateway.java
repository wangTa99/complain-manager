package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetClueInfoByPhoneGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetCLueInfoByPhoneGoOut;

/**
 * 线索信息查询接口
 */
public interface ClueGateway {

    /**
     * 根据手机号查询线索信�?
     *
     * @param goIn 查询参数
     * @return 线索信息
     */
    GetCLueInfoByPhoneGoOut getClueInfoByPhone(GetClueInfoByPhoneGoIn goIn);

}
