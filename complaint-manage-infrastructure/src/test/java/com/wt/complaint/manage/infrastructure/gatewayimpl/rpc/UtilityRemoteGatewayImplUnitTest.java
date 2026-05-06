package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.mrs.utility.api.model.req.UpcConfigReq;
import com.wt.mrs.utility.api.model.resp.UpcConfigResp;
import com.wt.mrs.utility.api.provider.UpcConfigProvider;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilityRemoteGatewayImplUnitTest {

    @InjectMocks
    private UtilityRemoteGatewayImpl gateway;

    @Mock
    private UpcConfigProvider upcConfigProvider;

    @Test
    void getUpcConfigShouldThrowWhenRpcFail() {
        Result fail = Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "ABC");
        when(upcConfigProvider.getUpcConfigByModuleWithMid(any(UpcConfigReq.class))).thenReturn(fail);
        UpcConfigGoIn goIn = UpcConfigGoIn.builder().mid("1").moduleKey("complaintFrame").currRole("role").orgId("org").build();
        Assertions.assertThrows(BusinessException.class, () -> gateway.getUpcConfig(goIn));
    }

    @Test
    void getUpcConfigShouldReturnEmptyWhenNoItems() {
        UpcConfigResp resp = UpcConfigResp.builder().itemList(Collections.emptyList()).build();
        when(upcConfigProvider.getUpcConfigByModuleWithMid(any(UpcConfigReq.class))).thenReturn(Result.success(resp));
        UpcConfigGoIn goIn = UpcConfigGoIn.builder().mid("1").moduleKey("complaintFrame").currRole("role").orgId("org").build();
        Assertions.assertTrue(gateway.getUpcConfig(goIn).isEmpty());
    }

    @Test
    void getUpcConfigByModulesShouldThrowOnEmptyModules() {
        Assertions.assertThrows(BusinessException.class, () -> gateway.getUpcConfigByModules(Collections.emptyList()));
    }

    @Test
    void getUpcConfigByModulesShouldThrowWhenEmptyData() {
        com.wt.mrs.utility.api.model.resp.UpcRoleConfigResp resp =
                com.wt.mrs.utility.api.model.resp.UpcRoleConfigResp.builder().roleConfigs(Collections.emptyList()).build();
        Result<com.wt.mrs.utility.api.model.resp.UpcRoleConfigResp> ok = Result.success(resp);
        when(upcConfigProvider.getAllUpcConfig(any())).thenReturn(ok);
        Assertions.assertThrows(BusinessException.class, () -> gateway.getUpcConfigByModules(Collections.singletonList("complaintFrame")));
    }
}
