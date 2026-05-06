package com.wt.complaint.manage.api.model.req.approve;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintAuditListReq implements Serializable {

    private static final long serialVersionUID = 512127934016218652L;

    @ApiDocClassDefine(value = "auditStatusList", description = "审批状态列�?0 默认 1 待审�?2 通过 3 驳回 ")
    private List<Integer> auditStatusList;

    @ApiDocClassDefine(value = "auditTypeList",
                       description = "审批单类型列表�?-改派门店 2-72H无法结案 3-申请免责 4-申请结案 5-产品风险申请结案 6-服务投诉判责")
    private List<Integer> auditTypeList;

    @ApiDocClassDefine(value = "complaintNo", description = "投诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "orgIdList", description = "门店id列表,支持多�?)
    private List<String> orgIdList;

    @ApiDocClassDefine(value = "contactPhone", description = "联系电话")
    private String contactPhone;

    @ApiDocClassDefine(value = "carNo", description = "车牌�?)
    private String carNo;

    @ApiDocClassDefine(value = "vin", description = "VIN�?)
    private String vin;

    @ApiDocClassDefine(value = "createTimeStart", description = "创建时间起始,格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiDocClassDefine(value = "createTimeEnd", description = "创建时间结束,格式：yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;

    @ApiDocClassDefine(value = "pageNum", description = "页码, 默认�?")
    private Integer pageNum = 1;

    @ApiDocClassDefine(value = "pageSize", description = "每页大小, 默认�?0,最�?00,最�?")
    @Max(value = 100, message = "每页条数不能超过100")
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}
