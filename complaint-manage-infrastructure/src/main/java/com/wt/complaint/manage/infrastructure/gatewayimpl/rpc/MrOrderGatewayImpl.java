package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.collection.CollUtil;
import com.wt.car.mrs.api.dto.MrOrderLightListReq;
import com.wt.car.soc.gw.api.MrOrderProvider;
import com.wt.car.soc.gw.api.dto.res.MrOrderSimple;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.MrOrderGateway;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Slf4j
@Service
public class MrOrderGatewayImpl implements MrOrderGateway {

    @DubboReference(group = "${dubbo.group}", check = false, interfaceClass = MrOrderProvider.class, timeout = DubboConstant.TIME_OUT)
    private MrOrderProvider mrOrderProvider;

    @Override
    public List<MrOrderSimple> getSimpleMrOrderInfo(List<String> stNoList) {
        MrOrderLightListReq req = new MrOrderLightListReq();
        req.setSuperTicketNoList(stNoList);
        req.setPageSize(stNoList.size());
        log.info("getSimpleMrOrderInfo req{}", GsonUtil.toJson(req));
        Result<List<MrOrderSimple>> result = mrOrderProvider.mrOrderLightList(req);
        if (result.getCode() != GeneralCodes.OK.getCode()) {
            log.error("查询维保单失�?req:{},res:{}", GsonUtil.toJson(req),
                    GsonUtil.toJson(result));
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "查询相关维保单信息失�?);
        }
        log.info("getSimpleMrOrderInfo res{}", GsonUtil.toJson(result));
        List<MrOrderSimple> data = result.getData();
        if(CollUtil.isEmpty(data)) {
            return Collections.emptyList();
        }
        return data;
    }
}
