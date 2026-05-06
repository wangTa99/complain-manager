package com.wt.complaint.manage.app.providerimpl;

import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.req.apply.ExemptionApplyReq;
import com.wt.complaint.manage.api.model.req.apply.Org72HFreeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgChangeApplyReq;
import com.wt.complaint.manage.api.model.req.apply.OrgFinishApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.provider.ComplaintApplyProvider;
import com.wt.complaint.manage.app.convert.ComplaintAuditConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintApplyService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintApplySoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

import javax.annotation.Resource;

@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
public class ComplaintApplyProviderImpl implements ComplaintApplyProvider {
    @Resource
    ComplaintApplyService complaintApplyService;

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/complaint/apply/submitChangeOrgApply",
            name = "改派门店申请", description = "路径�?mtop/proretailcarpad/complaint/apply/submitChangeOrgApply")
    public Result<OrgApplyResp> submitChangeOrgApply(OrgChangeApplyReq req) {
        try {
            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            ComplaintApplySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(req);
            soIn.setCreateMid(StringUtils.isNotEmpty(miID) ? Long.valueOf(miID) : null);
            soIn.setApplyContent(GsonUtil.toJson(req));
            soIn.setAuditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode());
            ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);
            return Result.success(ComplaintAuditConvert.INSTANCE.toResp(soOut));
        } catch (BusinessException e) {
            log.warn("submitChangeOrgApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("submitChangeOrgApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/complaint/apply/submitExemptionApply",
            name = "免责申请", description = "路径�?mtop/proretailcarpad/complaint/apply/submitExemptionApply")
    public Result<OrgApplyResp> submitExemptionApply(ExemptionApplyReq req) {
        try {
            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            ComplaintApplySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(req);
            soIn.setCreateMid(StringUtils.isNotEmpty(miID) ? Long.valueOf(miID) : null);
            soIn.setApplyContent(GsonUtil.toJson(req));
            soIn.setAuditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode());
            ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);
            return Result.success(ComplaintAuditConvert.INSTANCE.toResp(soOut));
        } catch (BusinessException e) {
            log.warn("submitExemptionApply fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("submitExemptionApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/complaint/apply/submit72HFreeApply",
            name = "72H无法结案申请", description = "路径�?mtop/proretailcarpad/complaint/apply/submit72HFreeApply")
    public Result<OrgApplyResp> submit72HFreeApply(Org72HFreeApplyReq req) {
        try {
            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            ComplaintApplySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(req);
            soIn.setCreateMid(StringUtils.isNotEmpty(miID) ? Long.valueOf(miID) : null);
            soIn.setApplyContent(GsonUtil.toJson(req));
            soIn.setAuditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode());
            ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);
            return Result.success(ComplaintAuditConvert.INSTANCE.toResp(soOut));
        } catch (BusinessException e) {
            log.warn("submit72HFreeApply fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("submit72HFreeApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/complaint/apply/submitFinishApply",
            name = "结案申请", description = "路径�?mtop/proretailcarpad/complaint/apply/submitFinishApply")
    public Result<OrgApplyResp> submitFinishApply(OrgFinishApplyReq req) {
        try {
            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            ComplaintApplySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(req);
            soIn.setCreateMid(StringUtils.isNotEmpty(miID) ? Long.valueOf(miID) : null);
            soIn.setApplyContent(GsonUtil.toJson(req));
            soIn.setAuditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode());
            ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);
            return Result.success(ComplaintAuditConvert.INSTANCE.toResp(soOut));
        } catch (BusinessException e) {
            log.warn("submitFinishApply fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("submitFinishApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/complaint/apply/submitFinishApplyV2", name = "结案申请V2",
            description = "路径�?mtop/proretailcarpad/complaint/apply/submitFinishApplyV2")
    public Result<OrgApplyResp> submitFinishApplyV2(OrgFinishApplyReq req) {
        try {
            if (req.getUserAgreement() == null) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "是否与用户达成一致不能为�?);
            }
            if (req.getVehicleRepaired() == null) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "车辆异常是否修复不能为空");
            }

            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            log.info("start call ComplaintApplyProviderImpl#submitFinishApplyV2, req={}, mid={}",
                    GsonUtil.toJson(req), miID);
            ComplaintApplySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(req);
            soIn.setCreateMid(StringUtils.isNotEmpty(miID) ? Long.valueOf(miID) : null);
            // V2版本去掉结案标签
            soIn.setClosingTagList(null);
            soIn.setApplyContent(GsonUtil.toJson(req));
            // 标记为V2版本，由Service层根据complaintType动态设置auditType
            soIn.setFinishApplyV2(true);

            ComplaintApplySoOut soOut = complaintApplyService.submitApply(soIn);
            return Result.success(ComplaintAuditConvert.INSTANCE.toResp(soOut));
        } catch (BusinessException e) {
            log.warn("submitFinishApplyV2 fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("submitFinishApplyV2 error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }
}
