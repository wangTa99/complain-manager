package com.wt.complaint.manage.infrastructure.gatewayimpl.rpc;

import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NrJobGateway;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.xiaomi.nr.job.admin.dto.TriggerJobRequestDTO;
import com.xiaomi.nr.job.admin.service.NrJobService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NrJobGatewayImpl implements NrJobGateway {

    @DubboReference(check = false, interfaceClass = NrJobService.class, group = "${dubbo.job.group}", timeout = 5000)
    private NrJobService nrJobService;

    @Override
    public String createExportTask(TriggerJobRequestDTO request) {
        log.info("NrJobGatewayImpl.createExportTask request:{}", JacksonUtil.toStr(request));

        Result<String> result;
        try {
            long start = System.currentTimeMillis();

            // 请求任务中心创建任务
            result = nrJobService.triggerJob(request);

            log.info("NrJobGatewayImpl.createExportTask call triggerJob finished, cost time:{}ms, result:{}", System.currentTimeMillis() - start, result);
        } catch (Exception e) {
            log.error("NrJobGatewayImpl.createExportTask call nrJob error ", e);
            throw new BusinessException(ErrorCodeEnums.THIRD_SERVICE_ERROR, "调用任务中心创建任务失败");
        }

        if (result.getCode() != GeneralCodes.OK.getCode()) {
            throw new BusinessException(ErrorCodeEnums.THIRD_SERVICE_ERROR, result.getMessage());
        }

        return result.getData();
    }
}
