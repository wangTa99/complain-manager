package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.convert.Convert;
import com.wt.b2csvr.proretail.car.cluebenchapi.api.carapp.ClueInfoProvider;
import com.wt.b2csvr.proretail.car.cluebenchapi.dto.req.carapp.GetClueInfoByPhoneReq;
import com.wt.b2csvr.proretail.car.cluebenchapi.dto.resq.carapp.GetCLueInfoByPhoneResp;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClueGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.GetClueInfoByPhoneGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetCLueInfoByPhoneGoOut;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClueGatewayImpl implements ClueGateway {

    @DubboReference(interfaceClass = ClueInfoProvider.class, group = "${dubbo.group.clue}", timeout = 3000,
            version = "1.0", check = false)
    private ClueInfoProvider clueInfoProvider;

    @Override
    public GetCLueInfoByPhoneGoOut getClueInfoByPhone(GetClueInfoByPhoneGoIn goIn) {
        try {
            GetClueInfoByPhoneReq req = Convert.convert(GetClueInfoByPhoneReq.class, goIn);
            log.info("ClueGatewayImpl.getClueInfoByPhone, req:{}", GsonUtil.toJson(req));
            Result<GetCLueInfoByPhoneResp> result = clueInfoProvider.getClueInfoByPhone(req);
            if (result == null || GeneralCodes.OK.getCode() != result.getCode()) {
                String errorMsg = result != null ? result.getMessage() : "";
                log.error("调用rpc查询线索信息失败 fail, , err:{}, data:{}", errorMsg, GsonUtil.toJson(result));
                return new GetCLueInfoByPhoneGoOut();
            }
            log.info("ClueGatewayImpl.getClueInfoByPhone, resp:{}", GsonUtil.toJson(result));
            return Convert.convert(GetCLueInfoByPhoneGoOut.class, result.getData());
        } catch (Exception e) {
            log.error("ClueGatewayImpl.getClueInfoByPhone error:{}", e.toString());
        }
        return new GetCLueInfoByPhoneGoOut();
    }
}
