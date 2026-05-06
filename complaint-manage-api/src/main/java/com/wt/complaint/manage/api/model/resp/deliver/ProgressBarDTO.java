package com.wt.complaint.manage.api.model.resp.deliver;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 详情进度条相关字�?
 *
 * @author huxiankang
 * @date 2025/6/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressBarDTO  implements Serializable {


    @ApiDocClassDefine(value = "progressStatus", description = "投诉单状态code, 10-待首�?20-跟进�?45-待判�?50-已结�?)
    private Integer progressStatus;

    @ApiDocClassDefine(value = "createTime", description = "创建时间")
    private Long createTime;

    @ApiDocClassDefine(value = "expectedFirstResponseTime", description = "预期首响时间")
    private Long expectedFirstResponseTime;
    @ApiDocClassDefine(value = "realFirstResponseTime", description = "实际首响时间")
    private Long realFirstResponseTime;

    @ApiDocClassDefine(value = "expectedFinishTime", description = "预期首响时间")
    private Long expectedFinishTime;
    @ApiDocClassDefine(value = "realFinishTime", description = "实际首响时间")
    private Long realFinishTime;

    @ApiDocClassDefine(value = "judgeFinishTime", description = "判责完成时间")
    private Long judgeFinishTime;


}
