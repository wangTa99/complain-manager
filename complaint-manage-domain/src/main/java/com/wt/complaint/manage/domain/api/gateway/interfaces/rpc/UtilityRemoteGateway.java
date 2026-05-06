package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcConfigGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UpcModuleConfigGoOut;

import java.util.List;

public interface UtilityRemoteGateway {

    List<UpcConfigGoOut> getUpcConfig(UpcConfigGoIn goIn);
    List<UpcModuleConfigGoOut> getUpcConfigByModules(List<String> modules);
    void refreshCacheTtl();
}
