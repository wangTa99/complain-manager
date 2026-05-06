package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.constont.ExcelConstants;
import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.api.model.enums.TestTagEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.api.model.req.*;
import com.wt.complaint.manage.api.model.resp.*;
import com.wt.complaint.manage.api.provider.ComplaintViewProvider;
import com.wt.complaint.manage.app.convert.ComplaintViewConvert;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.NrJobGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.UtilityRemoteGateway;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.SimpleComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.strategy.process.FollowProcessStrategy;
import com.wt.complaint.manage.domain.strategy.process.FollowProcessFactory;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.mone.docs.annotations.dubbo.ApiModule;
import com.xiaomi.nr.job.admin.dto.TriggerJobRequestDTO;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@DubboService(timeout = 3000, group = "${dubbo.group}", version = "1.0")
@ApiModule(value = "投诉单展示相关接�?, apiInterface = ComplaintViewProvider.class)
@SuppressWarnings("all")
public class ComplaintViewProviderImpl implements ComplaintViewProvider {

    @Resource
    private ComplaintViewService complaintViewService;

    @Autowired
    private CustomeUserContext customeUserContext;

    @Resource
    private NrJobGateway nrJobGateway;

    @Autowired
    private FollowProcessFactory userComplaintFollowProcessFactory;

    @Resource
    private UpcConfigLocalCache localCache;

    @Resource
    private UtilityRemoteGateway utilityRemoteGateway;

    @Value("${job.upc.project.id}")
    private Long jobProjectId;

    @Value("${job.executor.appname}")
    private String appname;

    @Override
    @ApiDoc(value = "/接口路径", name = "获取投诉详情框架信息", description = "获取投诉详情框架信息，PAD路径�?mtop/proretailcarpad/complaint/view/getConsultFrame 售后工作台路径：/mtop/proretailcar/complaint/view/getComplaintFrame")
    public Result<ComplaintDetailFrameResp> getComplaintFrame(ComplaintDetailFrameReq req) {
        try {
            ComplaintFrameInfoSoOut complaintFrameInfo =
                    complaintViewService.getComplaintFrameInfo(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(complaintFrameInfo));
        } catch (BusinessException be) {
            log.warn("getComplaintFrame exception, req: {}, errorMsg: {}", RetailJsonUtil.toJson(req), be.getMessage(),
                    be);
            return Result.fail(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            log.error("getComplaintFrame error, req: {}, errorMsg: {}", req, e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }

    }

    @Override
    @ApiDoc(value = "/接口路径", name = "获取投诉详情框架按钮", description = "获取投诉详情框架按钮，PAD路径�?mtop/proretailcarpad/complaint/view/getComplaintFrame 售后工作台路径：/mtop/proretailcar/complaint/view/getComplaintAuth")
    public Result<ComplaintDetailFrameResp> getComplaintAuth(ComplaintDetailFrameReq req) {
        try {
            ComplaintFrameInfoSoOut complaintFrameInfo =
                    complaintViewService.getComplaintAuth(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(complaintFrameInfo));
        } catch (BusinessException be) {
            log.warn("getComplaintAuth exception, req: {}, errorMsg: {}", RetailJsonUtil.toJson(req), be.getMessage(),
                    be);
            return Result.fail(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            log.error("getComplaintAuth error, req: {}, errorMsg: {}", req, e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "/接口路径", name = "清空按钮缓存", description = "清空按钮缓存")
    public Result<String> refreshCacheTtl() {
        localCache.refreshCacheTtl();
        utilityRemoteGateway.refreshCacheTtl();
        return Result.success("缓存时间重置成功");
    }

    @Override
    @ApiDoc(value = "/接口路径", name = "投诉信息tab接口", description = "投诉信息tab接口，PAD路径�?mtop/proretailcarpad/complaint/view/getComplaintDetail")
    public Result<ComplaintDetailResp> getComplaintDetail(ComplaintDetailReq req) {
        try {
            ComplaintDetailSoOut complaintDetail =
                    complaintViewService.getComplaintDetail(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(complaintDetail));
        } catch (BusinessException be) {
            log.warn("getComplaintDetail exception, req: {}, errorMsg: {}", RetailJsonUtil.toJson(req), be.getMessage(),
                    be);
            return Result.fail(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            log.error("getComplaintDetail error, req: {}, errorMsg: {}", req, e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    /**
     * 批量获取投诉信息接口
     * 上游客服也会调用此接�?
     */
    @Override
    @ApiDoc(value = "/接口路径", name = "批量获取投诉信息接口", description = "投诉信息tab接口，兼容支付和零售," +
            "PAD路径�?mtop/car_cs/complaint/view/batchGetComplaintDetail")
    public Result<ComplaintDetailBatchResp> batchGetComplaintDetail(ComplaintDetailBatchReq req) {
        try {
            ComplaintBatchDetailSoOut complaintBatchDetailSoOut =
                    complaintViewService.batchGetComplaintDetail(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(complaintBatchDetailSoOut));
        } catch (BusinessException be) {
            log.warn("batchGetComplaintDetail exception, req: {}, errorMsg: {}", RetailJsonUtil.toJson(req),
                    be.getMessage(), be);
            return Result.fail(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            log.error("batchGetComplaintDetail error, req: {}, errorMsg: {}", req, e.getMessage(), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    public Result<ComplaintFollowUpRecordsResp> getFollowUpRecords(ComplaintFollowUpRecordsReq req) {
        log.info("getFollowUpRecords req: {}", req);
        try {
            if (StrUtil.isBlank(req.getUcNo()) && StrUtil.isBlank(req.getComplaintNo()) && StrUtil.isBlank(req.getConsultNo())&& StrUtil.isBlank(req.getConsultSuperTicketNo())) {
                return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "查询单号不能同时为空");
            }
            // 兼容传参,客服侧投诉单和举报单都是传的ucNo,客诉侧投诉单传的是complaintNo,举报单传的是ucNo,零售&交付投诉单传的是ucNo
            String businessNo = StrUtil.isNotBlank(req.getUcNo()) ? req.getUcNo() : req.getComplaintNo();
            // 为了在策略实现类中处理逻辑，这里需要把单号重新赋�?
            req.setComplaintNo(businessNo);
            req.setUcNo(businessNo);
            if(StrUtil.isNotBlank(req.getConsultNo())){
                businessNo = req.getConsultNo();
            }
            UcOrderTypeEnum ucOrderTypeEnum = null;
            if(StrUtil.isNotBlank(req.getConsultSuperTicketNo())){
                ucOrderTypeEnum = UcOrderTypeEnum.CONSULT_ORDER;
            } else {
                // 根据单号前缀获取单号类型
                ucOrderTypeEnum = UcOrderTypeEnum.getByUcNo(businessNo);
            }
            if (ObjectUtil.isNull(ucOrderTypeEnum)) {
                return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "查询单号格式不正�?);
            }
            // 根据单号类型获取策略实现�?
            FollowProcessStrategy strategy =
                    userComplaintFollowProcessFactory.getStrategy(ucOrderTypeEnum.getCode());
            // 执行策略实现�?
            ComplaintProcessListSoOut complaintProcessRecords =
                    strategy.getFollowUpRecords(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            ComplaintFollowUpRecordsResp resp = ComplaintViewConvert.INSTANCE.convertToResp(complaintProcessRecords);
            return Result.success(resp);
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider#getFollowUpRecords business error,req:{},e:{}",
                    RetailJsonUtil.toJson(req), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider#getFollowUpRecords error,req:{}", RetailJsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }

    }

    @Override
    @ApiDoc(value = "查询精简版投诉单详情,包括车辆信息,用于pad端积分发放详情页边栏展示投诉单详�?)
    public Result<SimpleComplaintDetailResp> getSimpleComplaintDetail(SimpleComplaintDetailReq request) {
        log.info("start call ComplaintViewProvider#getSimpleComplaintDetail,req:{}", RetailJsonUtil.toJson(request));
        try {
            SimpleComplaintDetailSoIn soIn = new SimpleComplaintDetailSoIn();
            soIn.setComplaintNo(request.getComplaintNo());
            String mid = RpcContext.getContext().getAttachment("$upc_miID");
            if (request.getMid() != null) {
                soIn.setMidStr(String.valueOf(request.getMid()));
            } else {
                soIn.setMidStr(mid);
            }
            SimpleComplaintDetailSoOut goOut = complaintViewService.getSimpleComplaintDetail(soIn);
            SimpleComplaintDetailResp resp = ComplaintViewConvert.INSTANCE.convertToResp(goOut);
            log.info("call ComplaintViewProvider#getSimpleComplaintDetail success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider#getSimpleComplaintDetail business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider#getSimpleComplaintDetail error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "查询精简版投诉单详情,包括车辆信息,用于pad端积分发放详情页边栏展示投诉单详�?,
            description = "/mtop/proretailcarpad/complaint/getSimpleComplaintDetail")
    public Result<SimpleComplaintDetailV2Resp> getSimpleComplaintDetailV2(SimpleComplaintDetailReq request) {
        log.info("start call ComplaintViewProvider#getSimpleComplaintDetailV2,req:{}", RetailJsonUtil.toJson(request));
        try {
            SimpleComplaintDetailSoIn soIn = new SimpleComplaintDetailSoIn();
            soIn.setComplaintNo(request.getComplaintNo());
            String mid = RpcContext.getContext().getAttachment("$upc_miID");
            if (request.getMid() != null) {
                soIn.setMidStr(String.valueOf(request.getMid()));
            } else {
                soIn.setMidStr(mid);
            }
            SimpleComplaintDetailSoOut goOut = complaintViewService.getSimpleComplaintDetail(soIn);
            SimpleComplaintDetailV2Resp resp = ComplaintViewConvert.INSTANCE.convertToRespV2(goOut);
            log.info("call ComplaintViewProvider#getSimpleComplaintDetailV2 success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider#getSimpleComplaintDetailV2 business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider#getSimpleComplaintDetailV2 error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "查询投诉单列表接�?, description = "pad�?/mtop/proretailcarpad/complaint/searchComplaintList\n" +
            "售后工作�?/mtop/proretailcar/complaint/searchComplaintList")
    public Result<ComplaintListSearchResp> searchComplaintList(ComplaintListSearchReq request) {
        log.info("start call ComplaintViewProvider#searchComplaintList,req:{}", RetailJsonUtil.toJson(request));
        try {
            UserInfo userInfo;
            if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(request.getSource())) {
                userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
            } else {
                userInfo = UserInfo.fromRpcContext();
            }
            log.info("searchComplaintList,userInfo:{}", RetailJsonUtil.toJson(userInfo));
            ComplaintListSearchGoIn goIn = ComplaintViewConvert.INSTANCE.convertToGoIn(request);
            List<String> roleList = JacksonUtil.parseArray(userInfo.getRoleList(), String.class);
            goIn.setRoleList(roleList);
            goIn.setMid(userInfo.getMiID());
            goIn.setCurrRole(userInfo.getCurrRole());
            // 获取角色信息
            String userInfoRoleList = userInfo.getRoleList();
            // 仅产研角色可以看MOCK门店数据
            if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(request.getSource())
                    && StrUtil.isNotEmpty(userInfoRoleList)
                    && !userInfoRoleList.contains(MrRoleConstant.PROGRAMMER)) {
                goIn.setTestTag(TestTagEnum.NON_TEST.getCode());
            }
            log.info("searchComplaintList goIn={}", JSONUtil.toJsonStr(goIn));
            ComplaintListSearchSoOut goOut = complaintViewService.searchComplaintList(goIn);
            ComplaintListSearchResp resp = ComplaintViewConvert.INSTANCE.convertToResp(goOut);
            log.info("call ComplaintViewProvider#searchComplaintList success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider#searchComplaintList business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider#searchComplaintList error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "投诉单列表各个tab数量统计", description = "pad�?/mtop/proretailcarpad/complaint/countComplaintListTab")
    public Result<CountComplaintListTabResp> countComplaintListTab(ComplaintListSearchReq request) {
        log.info("start call ComplaintViewProvider#countComplaintListTab,req:{}", RetailJsonUtil.toJson(request));
        try {
            UserInfo userInfo = UserInfo.fromRpcContext();
            log.info("countComplaintListTab,userInfo:{}", RetailJsonUtil.toJson(userInfo));
            ComplaintListSearchGoIn goIn = ComplaintViewConvert.INSTANCE.convertToGoIn(request);
            List<String> roleList = JacksonUtil.parseArray(userInfo.getRoleList(), String.class);
            goIn.setRoleList(roleList);
            goIn.setMid(userInfo.getMiID());
            goIn.setCurrRole(userInfo.getCurrRole());
            log.info("countComplaintListTab goIn={}", JSONUtil.toJsonStr(goIn));
            CountComplaintListTabSoOut goOut = complaintViewService.countComplaintListTab(goIn);
            CountComplaintListTabResp resp = ComplaintViewConvert.INSTANCE.convertToResp(goOut);
            log.info("call ComplaintViewProvider#countComplaintListTab success, request:{}, resp:{}",
                    RetailJsonUtil.toJson(request), RetailJsonUtil.toJson(resp));
            return Result.success(resp);
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider#countComplaintListTab business error,req:{},e:{}",
                    RetailJsonUtil.toJson(request), e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider#countComplaintListTab error,req:{}", RetailJsonUtil.toJson(request), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部异常");
        }
    }

    @Override
    @ApiDoc(value = "获取可派单人列表", description = "pad�?/mtop/proretailcarpad/complaint/view/getComplaintHandlerList")
    public Result<ComplaintHandlerListResp> getComplaintHandlerList(ComplaintHandlerListReq req) {
        try {
            log.info("MainTenanceRepairProvider.queryTechnician req:{}", GsonUtil.toJson(req));
            GetComplaintHandlerSoOut complaintHandler =
                    complaintViewService.getComplaintHandler(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(complaintHandler));
        } catch (BusinessException e) {
            log.error("ComplaintViewProvider.getComplaintHandlerList error,req:{},e:{}", GsonUtil.toJson(req),
                    e.getMessage());
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("ComplaintViewProvider.getComplaintHandlerList error,req:{}", GsonUtil.toJson(req), e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "内部错误");
        }
    }

    @Override
    @ApiDoc(value = "投诉单编辑页回显", description = "客服工作�?/mtop/car_cs/complaint/getComplaintEditDetail")
    public Result<ComplaintEditDetailResp> getComplaintEditDetail(ComplaintDetailReq req) {
        try {
            if (StringUtils.isBlank(req.getComplaintNo())) {
                throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不能为空");
            }
            log.info("start call getComplaintEditDetail, complaintNo:{}", req.getComplaintNo());
            ComplaintEditDetailSoOut soOut =
                    complaintViewService.getComplaintEditDetail(ComplaintViewConvert.INSTANCE.convertToSoIn(req));
            return Result.success(ComplaintViewConvert.INSTANCE.convertToResp(soOut));
        } catch (BusinessException be) {
            log.warn("getComplaintEditDetail exception, req: {}", GsonUtil.toJson(req), be);
            return Result.fail(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            log.error("getComplaintEditDetail error, req: {}", req, e);
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), ErrorCodeEnums.INTERNAL_ERROR.getName());
        }
    }

    @Override
    @ApiDoc(value = "投诉单列表导�?, description = "售后工作�?/mtop/proretailcar/complaint/exportComplaintList")
    public Result<ComplaintListExportRes> exportComplaintList(ComplaintListSearchReq request) {
        log.info("start call ComplaintViewProvider#exportComplaintList,req:{}", RetailJsonUtil.toJson(request));
        // 导出必须选择创建时间
        if (StrUtil.isBlank(request.getCreateTimeStart()) || StrUtil.isBlank(request.getCreateTimeEnd())) {
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "导出请先选择创建时间");
        }
        UserInfo userInfo;
        if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(request.getSource())) {
            userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        } else {
            userInfo = UserInfo.fromRpcContext();
        }
        log.info("exportComplaintList,userInfo:{}", RetailJsonUtil.toJson(userInfo));
        ComplaintListSearchGoIn goIn = ComplaintViewConvert.INSTANCE.convertToGoIn(request);
        List<String> roleList = JacksonUtil.parseArray(userInfo.getRoleList(), String.class);
        goIn.setRoleList(roleList);
        goIn.setMid(userInfo.getMiID());
        goIn.setCurrRole(userInfo.getCurrRole());
        // 获取角色信息
        String userInfoRoleList = userInfo.getRoleList();
        // 仅产研角色可以看MOCK门店数据
        if (StrUtil.isNotEmpty(userInfoRoleList)
                && !userInfoRoleList.contains(MrRoleConstant.PROGRAMMER)) {
            goIn.setTestTag(TestTagEnum.NON_TEST.getCode());
        }
        goIn.setTraceId(userInfo.getTraceId());
        String email = userInfo.getEmail();
        goIn.setEmail(email);
        String userName = email.replace("@xiaomi.com", "");
        log.info("exportComplaintList goIn={}", JSONUtil.toJsonStr(goIn));
        ComplaintListSearchSoOut goOut = complaintViewService.searchComplaintList(goIn);
        // 无有效数据导�?
        if (ObjectUtil.isNull(goOut) || CollectionUtil.isEmpty(goOut.getDataList())) {
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(), "无有效数据导�?);
        }
        Integer total = goOut.getTotal();
        // 导出数据超过最大限�?
        if (total > ExcelConstants.MAX_EXPORT_COUNT) {
            return Result.fail(ErrorCodeEnums.INTERNAL_ERROR.getErrorCode(),
                    "导出数据超过最大限�? + ExcelConstants.MAX_EXPORT_COUNT + "�?请筛选条件后重新导出");
        }
        // 创建导出任务
        TriggerJobRequestDTO jobReq = new TriggerJobRequestDTO();
        String taskName = String.format("投诉单列表导出_%s.xlsx",
                DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss"));
        jobReq.setTaskName(taskName);
        jobReq.setTaskParam(JacksonUtil.toStr(goIn));
        jobReq.setTaskDesc("客诉管理-投诉单列表导�?);
        jobReq.setJobKey("complaintListExportJobKey");
        jobReq.setOwner(userName);
        jobReq.setProjectId(jobProjectId);
        jobReq.setProjectName(appname);
        String result = nrJobGateway.createExportTask(jobReq);
        // 返回任务id
        ComplaintListExportRes res = new ComplaintListExportRes();
        res.setTaskId(result);
        return Result.success(res);
    }
}
