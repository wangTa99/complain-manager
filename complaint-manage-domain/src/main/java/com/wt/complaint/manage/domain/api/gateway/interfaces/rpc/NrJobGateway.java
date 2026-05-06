package com.wt.complaint.manage.domain.api.gateway.interfaces.rpc;


import com.xiaomi.nr.job.admin.dto.TriggerJobRequestDTO;

public interface NrJobGateway {

    /**
     * 创建NrJob任务
     */
    String createExportTask(TriggerJobRequestDTO request);
}
