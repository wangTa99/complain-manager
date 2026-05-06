package com.wt.complaint.manage.api.model.req.approve;

import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitForApprovalReq implements Serializable {

    private static final long serialVersionUID = -409396870144648367L;

    @ApiDocClassDefine(value = "id", description = "审批单ID", required = true)
    @NotNull(message = "审批流id不能为空")
    private Long id;

    @ApiDocClassDefine(value = "complaintNo", description = "客诉单号")
    private String complaintNo;

    @ApiDocClassDefine(value = "auditStatus", description = "审核状�? 2 通过 3 驳回")
    private Integer auditStatus;

    @ApiDocClassDefine(value = "auditComment", description = "审核意见，等同于驳回原因，纯字符�?)
    private String auditComment;

    /**
     * @see AuditTypeEnum
     */
    @ApiDocClassDefine(value = "auditType", description = "审批单类�?1 申请改派门店 2 申请72H无法结案 3 申请免责 4 申请结案")
    private Integer auditType;

    /**
     * 用于申请改派门店类型审批
     */
    @ApiDocClassDefine(value = "targetOrgId", description = "改派后的门店id")
    private String targetOrgId;

    @ApiDocClassDefine(value = "targetOrgName", description = "改派后的门店名称")
    private String targetOrgName;

    @ApiDocClassDefine(value = "tags", description = "结案标签列表")
    private List<Tag> tags;


    @Data
    public static class Tag implements Serializable{
        
        private static final long serialVersionUID = 570772286037473128L;

        @ApiDocClassDefine(value = "closingTagIdLink", description = "结案标签id链路,�?连接,例如 1/2/3")
        private String closingTagIdLink;

        @ApiDocClassDefine(value = "closingTagNameLink", description = "结案标签名称链路,�?连接,例如 汽车/一般投�?)
        private String closingTagNameLink;
    }
}
