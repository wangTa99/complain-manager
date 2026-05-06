package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintProcessSoOut {
    /**
     * 记录id
     */
    public Long processId;

    /**
     * 跟进记录类型
     * APPLY_CHANGE_STORE(1, "APPLY_CHANGE_STORE", "申请改派门店"),
     * APPLY_72H_CANNOT_FINISH(2, "APPLY_72H_CANNOT_FINISH", "申请72H无法结案"),
     * APPLY_EXEMPTION(3, "APPLY_EXEMPTION", "申请免责"),
     * APPLY_FINISH(4, "APPLY_FINISH", "申请结案"),
     * PICKUP_ORDER(5, "PICKUP_ORDER", "接单"),
     * DISPATCH_ORDER(6, "DISPATCH_ORDER", "派单"),
     * FIRST_RESPONSE(7, "FIRST_RESPONSE", "首次响应"),
     * ADD_FOLLOW_RECORD(8, "ADD_FOLLOW_RECORD", "添加跟进记录"),
     * REMIND(9, "REMIND", "催单"),
     * APPOINT_TO_STORE_MAINTENANCE(10, "APPOINT_TO_STORE_MAINTENANCE", "预约到店维保"),
     * TO_STORE_MAINTENANCE(11, "TO_STORE_MAINTENANCE", "到店维保"),
     * SEND_INTEGRAL(12, "SEND_INTEGRAL", "积分发放"),
     * AUDIT_CHANGE_STORE_PASS(13, "AUDIT_CHANGE_STORE_PASS", "申请改派门店-审核通过"),
     * AUDIT_CHANGE_STORE_REJECT(14, "AUDIT_CHANGE_STORE_REJECT", "申请改派门店-审核驳回"),
     * AUDIT_72H_CANNOT_FINISH_PASS(15, "AUDIT_72H_CANNOT_FINISH_PASS", "申请72H无法结案-审核同意"),
     * AUDIT_72H_CANNOT_FINISH_REJECT(16, "AUDIT_72H_CANNOT_FINISH_REJECT", "申请72H无法结案-审核驳回"),
     * AUDIT_EXEMPTION_PASS(17, "AUDIT_EXEMPTION_PASS", "申请免责-审核通过，历�?),
     * AUDIT_EXEMPTION_REJECT(18, "AUDIT_EXEMPTION_REJECT", "申请免责-审核驳回，历�?),
     * AUDIT_EXEMPTION_FIRST_PASS / SECOND_PASS / THIRD_PASS，AUDIT_EXEMPTION_FIRST_REJECT / SECOND_REJECT / THIRD_REJECT（新免责分审级）�?
     * AUDIT_FINISH_PASS(19, "AUDIT_FINISH_PASS", "申请结案-审核通过"),
     * AUDIT_FINISH_REJECT(20, "AUDIT_FINISH_REJECT", "申请结案-审核驳回");
     */
    public String processType;

    /**
     * 客诉单号
     */
    public String complaintNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 记录信息
     */
    public RecordInfoSoOut info;
}
