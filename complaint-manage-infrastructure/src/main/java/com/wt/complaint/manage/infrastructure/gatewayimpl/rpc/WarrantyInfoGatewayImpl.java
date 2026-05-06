package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.convert.Convert;
import com.wt.car.warranty.policy.api.dto.req.GetCarWarrantyPeriodReq;
import com.wt.car.warranty.policy.api.dto.res.GetCarWarrantyPeriodRes;
import com.wt.car.warranty.policy.api.service.WarrantyInfoProvider;
import com.wt.commons.utils.StringUtils;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.WarrantyInfoGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.WarrantyPeriodGoOut;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.exception.ProretailBusinessException;
import com.wt.proretail.newcommon.util.GatewayInvokeUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author zhangzheyang
 * @date 2024/12/25
 */
@Slf4j
@Component
public class WarrantyInfoGatewayImpl implements WarrantyInfoGateway {

    @DubboReference(interfaceClass = WarrantyInfoProvider.class, group = "${dubbo.group.warranty}", timeout = 3000,
            version = "1.0")
    WarrantyInfoProvider warrantyInfoProvider;


    @Override
    public WarrantyPeriodGoOut getCarWarrantyPeriodInfo(String vin) {
        if (StringUtils.isEmpty(vin)) {
            log.error("WarrantyInfoGateway#getCarWarrantyPeriodInfo vin is empty");
            return null;
        }
        GetCarWarrantyPeriodReq getCarWarrantyPeriodReq = new GetCarWarrantyPeriodReq();
        getCarWarrantyPeriodReq.setVin(vin);

        log.info("getCarWarrantyPeriodInfo req vin: {}", vin);
        Result<GetCarWarrantyPeriodRes> carWarrantyPeriodInfo =
                warrantyInfoProvider.getCarWarrantyPeriodInfo(getCarWarrantyPeriodReq);
        log.info("getCarWarrantyPeriodInfo req:{}  resp:{}",
                GsonUtil.toJson(getCarWarrantyPeriodReq), GsonUtil.toJson(carWarrantyPeriodInfo));
        if (carWarrantyPeriodInfo != null && carWarrantyPeriodInfo.getCode() == 404
                && "车辆信息不存�?.equals(carWarrantyPeriodInfo.getMessage())) {
            throw new ProretailBusinessException(GeneralCodes.ParamError, carWarrantyPeriodInfo.getMessage());
        }
        if (carWarrantyPeriodInfo == null || carWarrantyPeriodInfo.getCode() != 0) {
            log.error("getCarWarrantyPeriodInfo error, req:{} resp={}",
                    GsonUtil.toJson(getCarWarrantyPeriodReq), GsonUtil.toJson(carWarrantyPeriodInfo));
            return null;
        }

        GetCarWarrantyPeriodRes carWarrantyPeriodRes = GatewayInvokeUtil.getResultData(carWarrantyPeriodInfo);
        return Convert.convert(WarrantyPeriodGoOut.class, carWarrantyPeriodRes);
    }
}
