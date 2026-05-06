package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;


import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CisVinVidServiceGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetVidRelationMapResponseBo;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.utils.GsonUtil;
import com.wt.complaint.manage.infrastructure.converter.ProcessConverter;
import com.xiaomi.nr.cis.api.dto.*;
import com.xiaomi.nr.cis.api.service.CisVinVidService;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.INTERNAL_ERROR;

/**
 * @author: qis
 * @date: 2023/11/10 18:33
 */
@Service
@Slf4j
public class CisVinVidServiceGatewayImpl implements CisVinVidServiceGateway {

    @DubboReference(interfaceClass = CisVinVidService.class, group = "${cis.dubbo.group}", version = "1.0", retries =
            0, timeout = 1000)
    private CisVinVidService cisVinVidService;

    @Override
    public GetVidRelationMapResponseBo getVidRelationMap(List<String> vidList) {
        log.info("getVidRelationMap param,request:{}", GsonUtil.toJson(vidList));

        GetVidRelationMapRequest req = GetVidRelationMapRequest.builder().vids(vidList).build();

        try {
            Result<GetVidRelationMapResponse> res = cisVinVidService.getVidRelationMap(req);
            log.info("getVidRelationMap,request:{},res:{}", GsonUtil.toJson(req), GsonUtil.toJson(res));

            if (res == null) {
                throw new BusinessException(INTERNAL_ERROR, INTERNAL_ERROR.getName());
            }
            if (res.getCode() != 0) {
                throw new BusinessException(INTERNAL_ERROR.getErrorCode(), res.getMessage());
            }

            return ProcessConverter.INSTANCE.toGetVidRelationMapResponseBo(res.getData());
        } catch (BusinessException e) {
            log.error("getVidRelationMap,error,request:{},e: ", GsonUtil.toJson(req), e);
            throw e;

        }
    }
}
