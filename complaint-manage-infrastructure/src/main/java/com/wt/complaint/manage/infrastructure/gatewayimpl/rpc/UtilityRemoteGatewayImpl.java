package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.UtilityRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcConfigGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcModuleConfigGoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.mrs.utility.api.model.req.UpcAllConfigReq;
import com.wt.mrs.utility.api.model.req.UpcConfigReq;
import com.wt.mrs.utility.api.model.resp.UpcConfigResp;
import com.wt.mrs.utility.api.model.resp.UpcRoleConfigResp;
import com.wt.mrs.utility.api.provider.UpcConfigProvider;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UtilityRemoteGatewayImpl implements UtilityRemoteGateway {
    @DubboReference(interfaceClass = UpcConfigProvider.class, group = "${dubbo.group.utility}", timeout = 1000)
    private UpcConfigProvider upcConfigProvider;

    @Override
    public List<UpcConfigGoOut> getUpcConfig(UpcConfigGoIn goIn) {
        UpcConfigReq req = new UpcConfigReq();
        req.setMid(Long.valueOf(goIn.getMid()));
        req.setModuleKey(goIn.getModuleKey());
        req.setCurrRole(goIn.getCurrRole());
        req.setOrgId(goIn.getOrgId());
        Result<UpcConfigResp> upcConfig = upcConfigProvider.getUpcConfigByModuleWithMid(req);
        log.info("getUpcConfig res:{}", GsonUtil.toJson(upcConfig));
        if (upcConfig == null || upcConfig.getCode() != GeneralCodes.OK.getCode()) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "获取配置失败");
        }
        if (upcConfig.getData() == null || CollUtil.isEmpty(upcConfig.getData().getItemList())) {
            return new ArrayList<>();
        }
        return Convert.convert(new TypeReference<List<UpcConfigGoOut>>() {
        }, upcConfig.getData().getItemList());
    }

    @Override
    public List<UpcModuleConfigGoOut> getUpcConfigByModules(List<String> modules) {
        try {
            if (CollUtil.isEmpty(modules)) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "请求模块不能为空");
            }

            UpcAllConfigReq req = UpcAllConfigReq.builder()
                    .moduleKeyList(modules)
                    .build();
            log.info("getUpcConfigByModules param:{}", GsonUtil.toJson(modules));
            Result<UpcRoleConfigResp> allUpcConfig = upcConfigProvider.getAllUpcConfig(req);
            log.info("getUpcConfigByModules res:{}", GsonUtil.toJson(allUpcConfig));

            if (allUpcConfig == null || allUpcConfig.getCode() != GeneralCodes.OK.getCode()) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "拉取配置失败");
            }
            if (allUpcConfig.getData() == null || CollUtil.isEmpty(allUpcConfig.getData().getRoleConfigs())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "拉取到了空配�?);
            }

            return allUpcConfig.getData().getRoleConfigs()
                    .stream()
                    .map(t -> UpcModuleConfigGoOut.builder()
                            .roleKey(t.getRoleKey())
                            .moduleKey(t.getModuleKey())
                            .configs(Convert.convert(new TypeReference<List<UpcConfigGoOut>>() {
                            }.getType(), t.getItemList()))
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("UtilityRemoteGateway.getUpcConfigByModules req:{},e:", GsonUtil.toJson(modules), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, e.getMessage());
        }
    }

    @Override
    public void refreshCacheTtl() {
        upcConfigProvider.refreshCacheTtl();

    }
}
