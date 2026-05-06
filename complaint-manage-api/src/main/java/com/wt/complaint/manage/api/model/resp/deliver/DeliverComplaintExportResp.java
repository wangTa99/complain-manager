package com.wt.complaint.manage.api.model.resp.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
/**
 * 导出响应�?
 *
 * @author huxiankang
 * @date 2025/6/24
 */
@Data
public class DeliverComplaintExportResp implements Serializable {

    @ApiDocClassDefine(value = "taskId", description = "任务ID")
    private String taskId;
}
