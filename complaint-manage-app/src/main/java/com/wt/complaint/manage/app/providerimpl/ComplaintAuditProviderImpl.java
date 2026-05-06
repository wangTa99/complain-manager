package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.TestTagEnum;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.api.model.req.approve.*;
import com.wt.complaint.manage.api.model.resp.approve.AuditDetailForCustomerServiceResp;
import com.wt.complaint.manage.api.model.resp.approve.AuditTypeOptionItemDto;
import com.wt.complaint.manage.api.model.resp.approve.AuditTypeOptionResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditDetailResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintAuditListResp;
import com.wt.complaint.manage.api.model.resp.approve.ComplaintPreNextResp;
import com.wt.complaint.manage.api.provider.ComplaintAuditProvider;
import com.wt.complaint.manage.app.convert.ComplaintAuditConvert;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintAuditService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.approve.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.AuditDetailForCustomerServiceSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditListSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintPreNextSoOut;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "投诉单审批相关接�?, apiInterface = ComplaintAuditProvider.class)
public class ComplaintAuditProviderImpl implements ComplaintAuditProvider {

    @Resource
    private ComplaintAuditService complaintAuditService;

    @Resource
    private CustomeUserContext customeUserContext;

    @Override
    @ApiDoc(value = "查询投诉单审批列�?,
            description = "售后工作�?/mtop/proretailcar/complaint/searchComplaintAuditList")
    public Result<ComplaintAuditListResp> searchComplaintAuditList(ComplaintAuditListReq request) {
        try {
            ComplaintAuditListSoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            soIn.setMid(userInfo.getMiID());
            log.info("start ComplaintAuditProvider#searchComplaintAuditList,userInfo:{}, soIn={}",
                    RetailJsonUtil.toJson(userInfo), RetailJsonUtil.toJson(soIn));
            // 获取角色信息
            String userInfoRoleList = userInfo.getRoleList();
            // 仅产研角色可以看MOCK门店数据
            if (StrUtil.isNotEmpty(userInfoRoleList)
                    && !userInfoRoleList.contains(MrRoleConstant.PROGRAMMER)) {
                soIn.setTestTag(TestTagEnum.NON_TEST.getCode());
            }
            ComplaintAuditListSoOut soOut = complaintAuditService.searchComplaintAuditList(soIn);
            ComplaintAuditListResp resp = ComplaintAuditConvert.INSTANCE.toResp(soOut);
            log.info("call ComplaintAuditProvider#searchComplaintAuditList success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#searchComplaintAuditList business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#searchComplaintAuditList error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "查询有权限的投诉单类�?,
            description = "售后工作台：/mtop/proretailcar/complaint/listAllowedAuditTypes")
    public Result<AuditTypeOptionResp> listAllowedAuditTypes() {
        try {
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            List<Integer> allowedCodes = complaintAuditService.getAllowedAuditTypeList(userInfo.getMiID());
            List<AuditTypeOptionItemDto> list = allowedCodes.stream()
                    .map(code -> new AuditTypeOptionItemDto(code, AuditTypeEnum.getDescByCode(code)))
                    .collect(Collectors.toList());
            log.info("call ComplaintAuditProvider#listAllowedAuditTypes success, mid:{}, size:{}",
                    userInfo.getMiID(), list.size());
            return Result.success(new AuditTypeOptionResp(list));
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#listAllowedAuditTypes business error, e:{}", e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#listAllowedAuditTypes error", e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "连续审批,获取上一页和下一页审批流id", description = "售后工作�?/mtop/proretailcar/complaint/preNextAudit")
    public Result<ComplaintPreNextResp> preNextAudit(ComplaintPreNextReq request) {
        try {
            ComplaintPreNextSoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            soIn.setMid(userInfo.getMiID());
            log.info("ComplaintAuditProvider#preNextAudit,userInfo:{}, soIn:{}", RetailJsonUtil.toJson(userInfo),
                    RetailJsonUtil.toJson(soIn));
            ComplaintPreNextSoOut soOut = complaintAuditService.preNextAudit(soIn);
            ComplaintPreNextResp resp = ComplaintAuditConvert.INSTANCE.toResp(soOut);
            log.info("call ComplaintAuditProvider#preNextAudit success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#preNextAudit business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#preNextAudit error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "提交审批结果", description = "售后工作�?/mtop/proretailcar/complaint/submitForApproval\n" +
            "客服工作�?/mtop/car_cs/complaint/submitForApproval")
    public Result<Boolean> submitForApproval(SubmitForApprovalReq request) {
        try {
            SubmitForApprovalSoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            soIn.setAuditMid(userInfo.getMiID());
            log.info("start call ComplaintAuditProvider#submitForApproval,userInfo:{}, soIn:{}",
                    RetailJsonUtil.toJson(userInfo)
                    , RetailJsonUtil.toJson(soIn));
            Boolean result;
            ComplaintAuditSoOut complaintAuditSoOut = complaintAuditService.checkAuditParams(soIn);
            if (AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode().equals(complaintAuditSoOut.getAuditType())) {
                // 单独处理免责审批类型的申请：支持bpm和售后工作台多处审批
                result = complaintAuditService.submitForApprovalResponsibilityExemption(soIn, complaintAuditSoOut);
            } else {
                result = complaintAuditService.submitForApproval(soIn, complaintAuditSoOut, false);
            }
            log.info("call ComplaintAuditProvider#submitForApproval success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), result);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#submitForApproval business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#submitForApproval error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "审批详情�?, description = "售后工作�?/mtop/proretailcar/complaint/getComplaintAuditDetail")
    public Result<ComplaintAuditDetailResp> getComplaintAuditDetail(ComplaintAuditDetailReq request) {
        try {
            ComplaintAuditDetailSoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            soIn.setMid(userInfo.getMiID());
            log.info("start ComplaintAuditProvider#getComplaintAuditDetail,userInfo:{}, soIn:{}",
                    RetailJsonUtil.toJson(userInfo), RetailJsonUtil.toJson(soIn));
            ComplaintAuditSoOut soOut = complaintAuditService.getComplaintAuditDetail(soIn);
            ComplaintAuditDetailResp result = ComplaintAuditConvert.INSTANCE.toResp(soOut);
            // 特殊处理:服务判责类审批单，待审核状态下不返�?responsibility 字段，置�?null
            if (AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode().equals(result.getAuditType())
                    && AuditStatusEnum.PENDING.getCode().equals(result.getAuditStatus())) {
                result.setResponsibility(null);
            }
            log.info("call ComplaintAuditProvider#getComplaintAuditDetail success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(result));
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#getComplaintAuditDetail business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#getComplaintAuditDetail error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "结案申请审批详情�?用于客服工作�?, description = "客服工作�?/mtop/car_cs/complaint" +
            "/getAuditDetailForCustomerService")
    public Result<AuditDetailForCustomerServiceResp> getAuditDetailForCustomerService(
            AuditDetailForCustomerServiceReq request) {
        try {
            AuditDetailForCustomerServiceSoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            String miID = RpcContext.getContext().getAttachment(CommonConst.RPC_CONTEXT_UPC_MID);
            if (StringUtils.isBlank(miID)) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "当前用户未登�? 请登�?");
            }
            soIn.setMid(Long.valueOf(miID));
            log.info("start ComplaintAuditProvider#getAuditDetailForCustomerService,mid:{}, request:{}",
                    miID, RetailJsonUtil.toJson(request));
            AuditDetailForCustomerServiceSoOut soOut = complaintAuditService.getAuditDetailForCustomerService(soIn);
            AuditDetailForCustomerServiceResp result = ComplaintAuditConvert.INSTANCE.toResp(soOut);
            log.info("call ComplaintAuditProvider#getAuditDetailForCustomerService success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(result));
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#getAuditDetailForCustomerService business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#getAuditDetailForCustomerService error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "服务投诉判责", description = "售后工作�?/mtop/proretailcar/complaint/audit/judgeResponsibility")
    public Result<Boolean> judgeResponsibility(JudgeResponsibilityReq request) {
        try {
            JudgeResponsibilitySoIn soIn = ComplaintAuditConvert.INSTANCE.toSoIn(request);
            UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            soIn.setAuditMid(userInfo.getMiID());
            log.info("start call ComplaintAuditProvider#judgeResponsibility,userInfo:{}, soIn:{}",
                    RetailJsonUtil.toJson(userInfo), RetailJsonUtil.toJson(soIn));
            Boolean result = complaintAuditService.judgeResponsibility(soIn);
            log.info("call ComplaintAuditProvider#judgeResponsibility success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), result);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("ComplaintAuditProvider#judgeResponsibility business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintAuditProvider#judgeResponsibility error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }
}
