package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.b2csvr.car.user.api.model.dto.CarInfoDto;
import com.wt.b2csvr.car.user.api.model.enums.QueryTypeEnum;
import com.wt.b2csvr.car.user.api.model.request.UserAggQueryRequest;
import com.wt.b2csvr.car.user.api.model.response.UserAggResponse;
import com.wt.b2csvr.car.user.api.provider.UserQueryProvider;
import com.wt.b2csvr.car.user.api.utils.SignUtil;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.constont.DubboConstant;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarUserRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.CarUserAggGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CarUserRemoteGatewayImpl implements CarUserRemoteGateway {
    @DubboReference(interfaceClass = UserQueryProvider.class, group = "${dubbo.group.userquery}", version = "1.0", retries = 0, timeout = DubboConstant.TIME_OUT)
    private UserQueryProvider userQueryProvider;

    @Value("${user.query.access.key}")
    private String accessKey;

    @Value("${user.query.secret.key}")
    private String secretKey;

    @Override
    public CarUserAggGoOut userAggQuery(CarUserAggGoIn goIn) {
        CarUserAggGoOut goOut = new CarUserAggGoOut();
        goIn.checkCarUserAggGoIn();
        log.info("CarUserRemoteGatewayImpl userAggQuery goIn:{}", goIn);
        UserAggQueryRequest req = new UserAggQueryRequest();
        req.setVid(goIn.getVid());
        try {
            log.info("req :{}", JacksonUtil.toStr(req));
            req.setQueryType(QueryTypeEnum.BIZ_USER.getCode() | QueryTypeEnum.SYS_USER.getCode() | QueryTypeEnum.USER_CAR.getCode());
            req.setAccessKey(accessKey);
            req.setSign(SignUtil.generateSign(req, KeyCenterUtil.decrypt(secretKey)));
            Result<UserAggResponse> res = userQueryProvider.userAggQuery(req);
            if (res.getCode() == GeneralCodes.OK.getCode()) {
                log.info("call userQueryProvider info: {}", GsonUtil.toJson(res.getData()));
                if (res.getData() != null) {
                    if (Objects.isNull(res.getData().getSysUser())) {
                        throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "汽车用户不存�?);
                    }
                    goOut.setSysUserMid(res.getData().getSysUser().getMid());
                    goOut.setSysUserName(res.getData().getSysUser().getUserName());
                    goOut.setSysUserPhone(res.getData().getSysUser().getPhone());
                    if (CollUtils.isNotEmpty(res.getData().getCarList())) {
                        CarInfoDto carInfoDto = res.getData().getCarList().get(0);
                        goOut.setCarNo(carInfoDto.getCarNo());
                        goOut.setCarVid(carInfoDto.getVid());
                        goOut.setCarVin(carInfoDto.getVin());
                    }
                }
                return goOut;
            } else {
                log.error("call userQueryProvider failed. code: {}, message:{}", res.getCode(), res.getMessage());
            }
        } catch (Exception e) {
            log.warn("call userQueryProvider exception: ", e);
        }
        return goOut;
    }
}
