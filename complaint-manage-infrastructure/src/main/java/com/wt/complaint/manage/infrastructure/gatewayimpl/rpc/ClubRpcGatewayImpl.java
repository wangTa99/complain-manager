package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.convert.Convert;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClubRpcGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.micar.club.api.MemberApi;
import com.xiaomi.micar.club.api.req.member.BatchMemberInfoByVidReq;
import com.xiaomi.micar.club.api.resp.member.BatchMemberInfoResp;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * cis rpc 代理
 *
 * @author liubin
 */
@Slf4j
@Service
public class ClubRpcGatewayImpl implements ClubRpcGateway {

    @DubboReference(interfaceClass = MemberApi.class, group = "${dubbo.group.club}")
    private MemberApi memberApi;

    @Override
    public BatchMemberInfoBO batchGetMemberByVid(List<String> vids) {
        BatchMemberInfoByVidReq req = new BatchMemberInfoByVidReq();
        try {
            req.setVidList(vids);
            log.info("batchGetMemberByVid, req:{}", GsonUtil.toJson(req));
            Result<BatchMemberInfoResp> batchMemberInfoRespResult = memberApi.batchGetMemberByVid(req);
            log.info("batchGetMemberByVid, resp:{}", GsonUtil.toJson(batchMemberInfoRespResult));

            return Convert.convert(BatchMemberInfoBO.class, batchMemberInfoRespResult.getData());
        } catch (Exception e) {
            log.error("batchGetMemberByVid, req:{}", GsonUtil.toJson(req), e);
        }

        return new BatchMemberInfoBO();
    }
}
