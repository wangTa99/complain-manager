package com.wt.complaint.manage.app.convert;

import com.wt.complaint.manage.api.model.req.apply.ExemptionApplyReq;
import com.wt.complaint.manage.api.model.req.apply.Org72HFreeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgFinishApplyReq;
import com.wt.complaint.manage.api.model.req.approve.*;
import com.wt.complaint.manage.api.model.req.retail.RetailOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.model.resp.approve.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.*;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.ChangeOrgCallBackSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.AuditDetailForCustomerServiceSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintPreNextSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.ChangeOrgCallBackSoOut;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedRequest;
import com.xiaomi.newretail.bpm.api.model.callback.OnStatusChangedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Mapper
public interface ComplaintAuditConvert {

    ComplaintAuditConvert INSTANCE = Mappers.getMapper(ComplaintAuditConvert.class);

    @Mapping(target = "applyReason", source = "reassignRemark")
    ComplaintApplySoIn toSoIn(OrgChangeApplyReq source);

    @Mapping(target = "attachmentSoInList", source = "attachmentList")
    ComplaintApplySoIn toSoIn(Org72HFreeApplyReq source);

    @Mapping(target = "attachmentSoInList", source = "attachmentList")
    ComplaintApplySoIn toSoIn(ExemptionApplyReq source);

    @Mapping(target = "attachmentSoInList", source = "attachmentList")
    ComplaintApplySoIn toSoIn(OrgFinishApplyReq source);

    SubmitForApprovalSoIn toSoIn(SubmitForApprovalReq source);

    JudgeResponsibilitySoIn toSoIn(JudgeResponsibilityReq source);

    ComplaintAuditListSoIn toSoIn(ComplaintAuditListReq source);

    ComplaintAuditListResp toResp(ComplaintAuditListSoOut source);

    @Mapping(source = "riskLevel", target = "riskLevel", qualifiedByName = "riskLevelToStr")
    ComplaintAuditDTO toDTO(ComplaintAuditSoOut source);

    ComplaintPreNextSoIn toSoIn(ComplaintPreNextReq source);

    ComplaintPreNextResp toResp(ComplaintPreNextSoOut source);

    OrgApplyResp toResp(ComplaintApplySoOut source);

    ComplaintAuditDetailSoIn toSoIn(ComplaintAuditDetailReq source);
    
    ComplaintAuditDetailResp toResp(ComplaintAuditSoOut source);

    AuditDetailForCustomerServiceSoIn toSoIn(AuditDetailForCustomerServiceReq source);

    AuditDetailForCustomerServiceResp toResp(AuditDetailForCustomerServiceSoOut source);

    ChangeOrgCallBackSoIn toBpmAudit(OnStatusChangedRequest source);

    RetailComplaintApplySoIn toSubmitApply(RetailOrgChangeApplyReq source);

    OnStatusChangedResponse toBpmAuditResult(ChangeOrgCallBackSoOut source);

    @Named("riskLevelToStr")
    default String riskLevelToStr(Integer riskLevel) {
        if (riskLevel == null) {
            return "";
        }
        return "L" + riskLevel;
    }
}
