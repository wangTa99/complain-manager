package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.req.FollowRecordReq;
import com.wt.complaint.manage.api.model.req.consult.*;
import com.wt.complaint.manage.api.model.req.operate.CreateConsultReq;
import com.wt.complaint.manage.api.model.req.operate.CsEnquireInfo;
import com.wt.complaint.manage.api.model.req.operate.PickUpOrderReq;
import com.wt.complaint.manage.api.model.req.operate.UpdateHandlerReq;
import com.wt.complaint.manage.api.model.resp.operate.*;
import com.wt.complaint.manage.api.provider.UserConsultOperateProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.app.convert.UserConsultOperateConvert;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultOperateService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

import javax.annotation.Resource;

/**
 * 咨询单操作提供者实现类
 */
@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "咨询单操作相关接�?, apiInterface = UserConsultOperateProviderImpl.class)
@SuppressWarnings("all")
public class UserConsultOperateProviderImpl implements UserConsultOperateProvider {

    @Resource
    private UserConsultOperateService userConsultOperateService;

    @Resource
    private CustomeUserContext customeUserContext;


    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Override
    @ExceptionHandle
    @ApiDoc(value = "新建咨询�?, name = "新建咨询�?, description = "dubbo 接口")
    public Result<CreateOrderResp> createOrder(CreateConsultReq req) {
        try {
            log.info("UserConsultOperateProviderImpl.createOrder req:{}", GsonUtil.toJson(req));
            if (CollUtil.isNotEmpty(req.getExpand().getAttachments())) {
                req.getExpand().getAttachments().forEach(attachment -> {
                    attachment.setId(attachment.getFileId());
                });
            }
            CreateConsultOrderSoIn soIn = UserConsultOperateConvert.INSTANCE.toCreateConsultSoIn(req);
            CreateConsultOrderSoOut soOut = userConsultOperateService.createConsultOrder(soIn);
            CreateOrderResp resp = new CreateOrderResp();
            resp.setWorkNo(soOut.getConsultNo());
            log.info("UserConsultOperateProviderImpl.createOrder resp:{}", GsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.createOrder fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.createOrder error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "创建咨询单失�?);
        }
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "编辑咨询�?, name = "编辑咨询�?, description = "dubbo 接口")
    public Result<EditComplaintResp> editConsult(EditConsultReq req) {
        try {
            log.info("UserConsultOperateProviderImpl.editConsult req:{}", GsonUtil.toJson(req));
            if (CollUtil.isNotEmpty(req.getExpand().getCsEnquire().getAttachments())) {
                req.getExpand().getCsEnquire().getAttachments().forEach(attachment -> {
                    attachment.setId(attachment.getFileId());
                });
            }
            // 填充登陆人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            CsEnquireInfo csEnquireInfo = req.getExpand().getCsEnquire();
            OrderEditConsultSoIn soIn = UserConsultOperateConvert.INSTANCE.toEditConsultSoIn(req);
            soIn.setExpandSoIn(BeanUtil.copyProperties(csEnquireInfo, ConsultCreateExpandSoIn.class));
            if (miID != null && !miID.isEmpty()) {
                soIn.setOperatorMid(Long.valueOf(miID));
            } else {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未获取到当前登录�?mid");
            }
            log.info("UserConsultOperateProviderImpl.editConsult soIn:{}", GsonUtil.toJson(soIn));
            OrderEditConsultSoOut soOut = userConsultOperateService.editConsult(soIn);
            EditComplaintResp resp = UserConsultOperateConvert.INSTANCE.toEditConsultResp(soOut);
            log.info("UserConsultOperateProviderImpl.editConsult resp:{}", GsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.editConsult fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.editConsult error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "编辑咨询单失�?);
        }
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "咨询单接�?, name = "咨询单接�?, description = "dubbo 接口")
    public Result<PickUpOrderResp> pickUpOrder(PickUpOrderReq req) {
        log.info("UserConsultOperateProviderImpl.pickUpOrder req:{}", GsonUtil.toJson(req));
        String key = "ZX:pickUpOrder:" + req.getConsultNo();
        try {
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }
            ConsultOrderPickUpSoIn soIn = UserConsultOperateConvert.INSTANCE.toPickUpSoIn(req);
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            soIn.setPickUpMid(miID);
            ConsultOrderPickUpSoOut soOut = userConsultOperateService.pickUpOrder(soIn);
            PickUpOrderResp resp = UserConsultOperateConvert.INSTANCE.toPickUpResp(soOut);
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.pickUpOrder fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.pickUpOrder error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "接单失败");
        }finally {
            RedisUtil.unlock(key);
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/consultoperate/addFollowRecord", name = "添加跟进记录")
    public Result<AddFollowRecordResp> addFollowRecord(FollowRecordReq req) {
        try {
            log.info("UserConsultOperateProviderImpl.addFollowRecord req:{}", GsonUtil.toJson(req));
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            OrderAddFollowUpRecordSoIn soIn = UserConsultOperateConvert.INSTANCE.toFollowRecordSoIn(req);
            soIn.setFollowUpMid(miID);
            OrderFollowUpRecordSoOut soOut = userConsultOperateService.addFollowUpRecords(soIn);
            AddFollowRecordResp resp = UserConsultOperateConvert.INSTANCE.toFollowRecordResp(soOut);
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.addFollowRecord fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.addFollowRecord error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "添加跟进记录失败");
        }
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "申请改派门店", name = "申请改派门店", description = "dubbo 接口")
    public Result<ChangeOrgResp> submitChangeOrgApply(ConsultOrgChangeApplyReq req) {
        log.info("UserConsultOperateProviderImpl.submitChangeOrgApply req:{}", GsonUtil.toJson(req));
        String key = "ZX:submitChangeOrgApply:" + req.getConsultNo();
        try {
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            ConsultOrgChangeApplySoIn soIn = UserConsultOperateConvert.INSTANCE.toOrgChangeApplySoIn(req);
            if (miID != null && !miID.isEmpty()) {
                soIn.setOperateMid(Long.valueOf(miID));
            }
            ConsultOrgChangeApplySoOut soOut = userConsultOperateService.submitChangeOrgApply(soIn);
            ChangeOrgResp resp = UserConsultOperateConvert.INSTANCE.toOrgChangeResp(soOut);
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.submitChangeOrgApply fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.submitChangeOrgApply error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "申请改派门店失败");
        }finally {
            RedisUtil.unlock(key);
        }
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "更新作业单处理人", name = "更新作业单处理人", description = "dubbo 接口")
    public Result<UpdateHandlerResp> updateHandler(UpdateHandlerReq req) {
        log.info("UserConsultOperateProviderImpl.updateHandler req:{}", GsonUtil.toJson(req));
        String key = "ZX:updateHandler:" + req.getConsultNo();
        try {
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            ConsultUpdateHandlerSoIn soIn = UserConsultOperateConvert.INSTANCE.toUpdateHandlerSoIn(req);
            if (miID != null && !miID.isEmpty()) {
                soIn.setOperateMid(Long.valueOf(miID));
            }else {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未获取到当前登录�?mid");
            }
            ConsultUpdateHandlerSoOut soOut = userConsultOperateService.updateHandler(soIn);
            UpdateHandlerResp resp = new UpdateHandlerResp();
            resp.setResult(soOut.getResult());
            return Result.success(resp);
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.updateHandler fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.updateHandler error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "更新处理人失�?);
        }finally {
            RedisUtil.unlock(key);
        }
    }

    @Override
    @ExceptionHandle
    @ApiDoc(value = "改派跟进�?, name = "改派跟进�?, description = "dubbo 接口")
    public Result<String> reassign(ConsultReassignReq req) {
        log.info("UserConsultOperateProviderImpl.reassign req:{}", GsonUtil.toJson(req));
        String key = "ZX:reassign:" + req.getConsultNo();
        try {
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            ConsultReassignSoIn soIn = UserConsultOperateConvert.INSTANCE.toReassignSoIn(req);
            if (miID != null && !miID.isEmpty()) {
                soIn.setOperateMid(Long.valueOf(miID));
            }else {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未获取到当前登录�?mid");
            }
            ConsultReassignSoOut soOut = userConsultOperateService.reassign(soIn);
            return Result.success(soOut.getResult());
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.reassign fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.reassign error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "改派跟进人失�?);
        }finally {
            RedisUtil.unlock(key);
        }
    }

    @Override
    @ApiDoc(value = "/mtop/proretailcarpad/consultoperate/finish", name = "申请结案")
    public Result<String> finish(ConsultFinishReq req) {
        try {
            log.info("UserConsultOperateProviderImpl.finish req:{}", GsonUtil.toJson(req));
            req.check();
            // 获取当前登录人信�?
            String miID = RpcContext.getContext().getAttachment("$upc_miID");
            ConsultFinishSoIn soIn = UserConsultOperateConvert.INSTANCE.toFinishSoIn(req);
            if (miID != null && !miID.isEmpty()) {
                soIn.setOperateMid(Long.valueOf(miID));
            }else {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "未获取到当前登录�?mid");
            }
            ConsultFinishSoOut soOut = userConsultOperateService.finish(soIn);
            return Result.success(soOut.getResult());
        } catch (BusinessException e) {
            log.warn("UserConsultOperateProviderImpl.finish fail, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("UserConsultOperateProviderImpl.finish error, req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "结案失败");
        }
    }
}
