package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;

import java.util.List;

/**
 * cis rpc 代理
 *
 * @author liubin
 */
public interface ClubRpcGateway {

    /**
     * 根据车辆vid集合查询成员信息
     * @param vids 车辆vid集合
     * @return 成员信息
     */
    BatchMemberInfoBO batchGetMemberByVid(List<String> vids);
}
