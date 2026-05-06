package com.wt.complaint.manage.api.model.resp;

import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;

import java.io.Serializable;
import java.util.Date;

public class ComplaintFollowUpRecordInfo implements Serializable {

    @ApiDocClassDefine(value = "processId", description = "记录id")
    public int processId;

    @ApiDocClassDefine(value = "processType", description = "跟进记录类型 " +
            "APPLY_CHANGE_STORE-申请改派门店\n" +
            "APPLY_72H_CANNOT_FINISH-申请72H无法结案\n" +
            "APPLY_EXEMPTION-申请免责\n" +
            "APPLY_FINISH-申请结案\n" +
            "PICKUP_ORDER-接单\n" +
            "DISPATCH_ORDER-派单\n" +
            "FIRST_RESPONSE-首次响应\n" +
            "ADD_FOLLOW_RECORD-添加跟进记录\n" +
            "REMIND-催单\n" +
            "APPOINT_TO_STORE_MAINTENANCE-预约到店维保\n" +
            "TO_STORE_MAINTENANCE-到店维保\n" +
            "SEND_INTEGRAL-积分发放\n" +
            "AUDIT_CHANGE_STORE_PASS-申请改派门店-审核通过\n" +
            "AUDIT_CHANGE_STORE_REJECT-申请改派门店-审核驳回\n" +
            "AUDIT_72H_CANNOT_FINISH_PASS-申请72H无法结案-审核同意\n" +
            "AUDIT_72H_CANNOT_FINISH_REJECT-申请72H无法结案-审核驳回\n" +
            "AUDIT_EXEMPTION_PASS-申请免责-审核通过(历史)\n" +
            "AUDIT_EXEMPTION_REJECT-申请免责-审核驳回(历史)\n" +
            "AUDIT_EXEMPTION_FIRST_PASS-申请免责-一审通过\n" +
            "AUDIT_EXEMPTION_SECOND_PASS-申请免责-二审通过\n" +
            "AUDIT_EXEMPTION_THIRD_PASS-申请免责-三审通过\n" +
            "AUDIT_EXEMPTION_FIRST_REJECT-申请免责-一审驳回\n" +
            "AUDIT_EXEMPTION_SECOND_REJECT-申请免责-二审驳回\n" +
            "AUDIT_EXEMPTION_THIRD_REJECT-申请免责-三审驳回\n" +
            "AUDIT_FINISH_PASS-申请结案-审核通过\n" +
            "AUDIT_FINISH_REJECT-申请结案-审核驳回\n" +
            "UPGRADE_COMPLAINT-升级投诉记录\n" +
            "COMPLAINT_INFO_UPDATE-投诉单信息更新记录\n" +
            "SUBMIT_REVIEW-提交复盘\n" +
            "COMPLAINT_ADJUDICATION-服务投诉判责\n" +
            "AUDIT_EXEMPTION_WITHDRAW-申请免责-申请撤回")
    public String processType;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    public String complaintNo;

    @ApiDocClassDefine(value = "order", description = "记录顺序")
    public int order;

    @ApiDocClassDefine(value = "createTime", description = "记录创建时间")
    public Date createTime;

    @ApiDocClassDefine(value = "info", description = "记录信息")
    public RecordInfo info;
}
