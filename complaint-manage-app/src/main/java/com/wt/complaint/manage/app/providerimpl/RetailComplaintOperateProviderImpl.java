package com.wt.complaint.manage.app.providerimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.req.retail.CreateRetailComplaintOrderReq;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintFinishApplyReq;
import com.wt.complaint.manage.api.model.req.retail.RetailFollowRecordReq;
import com.wt.complaint.manage.api.model.req.retail.RetailOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.api.model.resp.operate.AddFollowRecordResp;
import com.wt.complaint.manage.api.model.resp.retail.CreateRetailComplaintOrderResp;
import com.wt.complaint.manage.api.provider.RetailComplaintOperateProvider;
import com.wt.complaint.manage.app.aspect.ExceptionHandle;
import com.wt.complaint.manage.domain.api.enums.CarChannelTypeEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RetailComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.converter.RetailOrderConverter;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailFollowRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailSubmitFinishApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.apply.RetailComplaintApplySoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.AddFollowRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.CreateRetailComplaintOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDoc;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil.parseComplaintScene;

/**
 * 零售投诉视图服务相关接口
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Slf4j
@DubboService(timeout = 1000, group = "${dubbo.group}", version = "1.0")
@Validated
public class RetailComplaintOperateProviderImpl implements RetailComplaintOperateProvider {

    @Autowired
    private RetailComplaintOperateService retailComplaintOperateService;

    @Autowired
    private RetailComplaintGateway retailComplaintGateway;

    @Resource
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Resource(name = "submitFinishApplyExecutor")
    private ThreadPoolTaskExecutor executor;

    @Resource(name = "submitChangeApplyExecutor")
    private ThreadPoolTaskExecutor applyChangeExecutor;

    private static final String UPC_MIID = "$upc_miID";

    @ApiDoc(value = "创建交付或零售客诉单", description = "/mtop/proretailcar/retailComplaint/createComplaintOrder")
    @ExceptionHandle
    public Result<CreateRetailComplaintOrderResp> createComplaintOrder(CreateRetailComplaintOrderReq req) {
        log.info("RetailComplaintOperateProviderImpl.createComplaintOrder req:{}", RetailJsonUtil.toJson(req));
        CreateRetailComplaintOrderSoOut createRetailComplaintOrderSoOut =
                retailComplaintOperateService.createComplaintOrder(RetailOrderConverter.INSTANCE.toCreateSoIn(req));
        CreateRetailComplaintOrderResp resp = Convert.convert(CreateRetailComplaintOrderResp.class, createRetailComplaintOrderSoOut);
        log.info("RetailComplaintOperateProviderImpl.createComplaintOrder resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "添加跟进记录", description = "/mtop/proretailcar/retailComplaint/addFollowRecord")
    @ExceptionHandle
    public Result<AddFollowRecordResp> addFollowRecord(RetailFollowRecordReq req) {
        log.info("RetailComplaintOperateProviderImpl.addFollowRecord req:{}", RetailJsonUtil.toJson(req));
        RetailFollowRecordSoIn soIn = Convert.convert(RetailFollowRecordSoIn.class, req);
        soIn.setFollowUpMid(RpcContext.getContext().getAttachment(UPC_MIID));
        AddFollowRecordSoOut addFollowRecordSoOut =
                retailComplaintOperateService.addFollowRecord(soIn);
        AddFollowRecordResp resp = Convert.convert(AddFollowRecordResp.class, addFollowRecordSoOut);
        log.info("RetailComplaintOperateProviderImpl.addFollowRecord resp:{}", RetailJsonUtil.toJson(resp));
        return Result.success(resp);
    }

    @Override
    @ApiDoc(value = "提交结案", description = "/mtop/proretailcar/retailComplaint/submitFinishApply")
    @ExceptionHandle
    @SuppressWarnings("squid:S3776")
    public Result<OrgApplyResp> submitFinishApply(RetailComplaintFinishApplyReq req) {
        req.check();
        UserInfo userInfo = UserInfo.fromRpcContext();

        /** -------------- 文件持久�?---------------- **/
        if (!CollUtil.isEmpty(req.getFileIds())) {
            fileRemoteGateway.fileCommit(req.getFileIds());
        }

        String key = "RC:" + ":operate:" + req.getDrNo();
        try {
            log.info("零售客诉客诉 - 尝试获取�?{}", key);
            // 尝试获取锁，不等待， 锁失效时�?�?
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }

            /** -------------- 主表内容校验 ---------------- **/
            RetailComplaintDetaiGoOut complaintDetail = retailComplaintGateway.getRetailComplaintDetail(RetailComplaintDetailGoIn.builder()
                    .drNo(req.getDrNo())
                    .build());
            if (complaintDetail == null) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "零售客诉号不存在");
            }
            if (!StrUtil.equals(complaintDetail.getOrgId(), req.getApplyOrgId())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "发起门店与客诉单记录门店不一�? 请联系管理员");
            }
            if (Arrays.stream(RetailComplaintOrderStatusEnum.values()).map(RetailComplaintOrderStatusEnum::getCode).noneMatch(t -> Objects.equals(t, complaintDetail.getOrderStatus()))
                    ||
                    Arrays.stream(RiskLevelEnum.values()).map(RiskLevelEnum::getCode).noneMatch(t -> Objects.equals(t, complaintDetail.getRiskLevel()))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "客诉单详情记录中出现了非法的内容, 请联系管理员");
            }
            if (complaintDetail.getZoneId() == null || complaintDetail.getLittleZoneId() == null) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "客诉单详情缺少区域信�? 请联系管理员");
            }
            /** -------------- 权限控制 ---------------- **/
            CarEmployeeInfoGoOut employeeInfoV2 = carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(userInfo.getMiID(), CarChannelTypeEnum.CAR_SALE.getCode());

            if (CollUtil.isEmpty(
                    CollUtil.intersection(
                            Arrays.asList(PositionEnum.CAR_STORE_MANAGER.getCode(), PositionEnum.CAR_STORE_OA.getCode()),
                            employeeInfoV2.getStorePositionInfoList()
                                    .stream()
                                    .filter(t -> StrUtil.equals(t.getOrgId(), req.getApplyOrgId()))
                                    .map(CarEmployeeInfoGoOut.StorePositionInfo::getPositionId)
                                    .distinct()
                                    .collect(Collectors.toList())))
                    &&
                    !Objects.equals(userInfo.getMiID(), complaintDetail.getOperatorMid())) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "仅有主管跟店�?�?客诉单跟进人 能操作提交结�?);
            }
            /** -------------- 数据补齐 ---------------- **/
            // 申请人姓�?
            CompletableFuture<String> nameFuture = CompletableFuture.supplyAsync(() -> eiamRemoteGateway.getEmployee(userInfo.getMiID()).getName(), executor);
            // 联系人姓�?
            CompletableFuture<String> contactNameFuture = CompletableFuture.supplyAsync(() -> KeyCenterUtil.decrypt(complaintDetail.getContactNameC()), executor);
            // 联系人电�?
            CompletableFuture<String> contactTelFuture = CompletableFuture.supplyAsync(() -> KeyCenterUtil.decrypt(complaintDetail.getContactPhoneC()), executor);
            // 门店名称
            CompletableFuture<String> orgNameFuture = CompletableFuture.supplyAsync(() -> storeRemoteGateway.getStoreNameMap(Collections.singletonList(req.getApplyOrgId())).getOrDefault(req.getApplyOrgId(), "-"), executor);
            String name = nameFuture.join();
            String contactName = contactNameFuture.join();
            String contactTel = contactTelFuture.join();
            String orgName = orgNameFuture.join();

            RetailSubmitFinishApplySoIn soIn = RetailSubmitFinishApplySoIn.builder()
                    .drNo(req.getDrNo())
                    .applyOrgId(req.getApplyOrgId())
                    .isReconcile(req.getIsReconcile())
                    .canBeRevisited(req.getCanBeRevisited().toString())
                    .solutionDesc(req.getSolutionDesc())
                    .attachmentList(req.getAttachmentList())
                    .orderStatus(complaintDetail.getOrderStatus())
                    .riskLevel(RiskLevelEnum.fromCode(complaintDetail.getRiskLevel()))
                    .operatorMid(userInfo.getMiID())
                    .applyName(StrUtil.isBlank(name) ? "-" : name)
                    .reminderFlag(complaintDetail.getReminderFlag())
                    .contactName(StrUtil.isBlank(contactName) ? "-" : contactName)
                    .contactTel(StrUtil.isBlank(contactTel) ? "-" : contactTel)
                    .complaintTypeName(ComplaintTypeEnum.getDescByCode(complaintDetail.getComplaintType()))
                    .problemCategory(complaintDetail.getProblemCategory())
                    .orgName(orgName)
                    .questionDesc(complaintDetail.getProblemDesc())
                    .complaintScene(parseComplaintScene(complaintDetail.getComplaintContent()))
                    .littleZoneId(complaintDetail.getLittleZoneId())
                    .zoneId(complaintDetail.getZoneId())
                    .build();
            log.info("RetailComplaintOperateProvider#submitFinishApply soIn:{}", GsonUtil.toJson(soIn));
            String bpmId = retailComplaintOperateService.submitFinishApply(soIn);
            log.info("RetailComplaintOperateProvider#submitFinishApply bpmId:{}", bpmId);
            return Result.success(OrgApplyResp.builder().applyId(bpmId).build());
        } finally {
            RedisUtil.unlock(key);
        }
    }

    @Override
    @ApiDoc(value = "申请改派门店", description = "/mtop/proretailcar/retailComplaint/submitChangeOrgApply")
    public Result<OrgApplyResp> submitChangeOrgApply(RetailOrgChangeApplyReq req) {
        log.info("RetailComplaintOperateProvider#submitChangeOrgApply req:{}", GsonUtil.toJson(req));
        req.checkReq();
        UserInfo userInfo = UserInfo.fromRpcContext();
        // 登陆人权限校�?
        changeOrgApplyAuthPreCheck(userInfo, req);
        String key = "RC:" + ":operate:" + req.getDrNo();
        try {
            log.info("零售客诉--申请改派门店 - 尝试获取操作�?{}", key);
            // 尝试获取锁，不等待， 锁失效时�?�?
            if (BooleanUtils.isFalse(RedisUtil.tryLock(key))) {
                throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "有其他操作正在进行中，请稍后再试");
            }
            // 主表内容校验
            RetailComplaintDetaiGoOut complaintDetail =
                    retailComplaintGateway.getRetailComplaintDetail(RetailComplaintDetailGoIn.builder()
                            .drNo(req.getDrNo())
                            .build());
            changeOrgApplyDetailPreCheck(complaintDetail, req);
            RetailComplaintApplySoIn changeApplySoIn = toChangeApplySoIn(userInfo, complaintDetail, req);
            RetailComplaintApplySoOut soOut = retailComplaintOperateService.submitChangeOrgApply(changeApplySoIn);
            return Result.success(OrgApplyResp.builder().applyId(soOut.getProcessInstanceId().toString()).build());
        } finally {
            RedisUtil.unlock(key);
        }
    }

    /**
     * 改派门店登录人权限校�?
     *
     * @param userInfo
     * @param req
     */
    private void changeOrgApplyAuthPreCheck(UserInfo userInfo, RetailOrgChangeApplyReq req) {
        /** -------------- 权限控制 ---------------- **/
        CarEmployeeInfoGoOut employeeInfoGoOut = carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(userInfo.getMiID(),CarChannelTypeEnum.CAR_SALE.getCode());
        if (CollUtil.isEmpty(employeeInfoGoOut.getStorePositionInfoList()) ||
                employeeInfoGoOut.getStorePositionInfoList().stream().noneMatch(t -> StrUtil.equals(t.getOrgId(), req.getApplyOrgId()))) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "没有对应门店权限");
        }

        List<CarEmployeeInfoGoOut.StorePositionInfo> orgInfoList = employeeInfoGoOut.getStorePositionInfoList().stream().filter(t -> StrUtil.equals(t.getOrgId(), req.getApplyOrgId())).collect(Collectors.toList());
        log.info("orgInfoList:{}", GsonUtil.toJson(orgInfoList));
        if (CollUtil.isEmpty(orgInfoList)) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR.getErrorCode(), "门店权限异常");
        }
        if (CollUtil.isEmpty(CollUtil.intersection(
                Arrays.asList(PositionEnum.CAR_STORE_MANAGER.getCode(), PositionEnum.CAR_STORE_OA.getCode()),
                orgInfoList.stream()
                        .map(CarEmployeeInfoGoOut.StorePositionInfo::getPositionId)
                        .distinct()
                        .collect(Collectors.toList())))) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR.getErrorCode(), "仅有主管跟店长能操作提交结案");
        }
    }

    /**
     * 申请改派门店详情校验
     *
     * @param complaintDetail
     * @param req
     */
    private void changeOrgApplyDetailPreCheck(RetailComplaintDetaiGoOut complaintDetail, RetailOrgChangeApplyReq req) {
        if (complaintDetail == null) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "零售客诉号不存在");
        }
        if (!StrUtil.equals(complaintDetail.getOrgId(), req.getApplyOrgId())) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "发起门店与客诉单记录门店不一�? 请联系管理员");
        }
        if (complaintDetail.getReassignmentTimes() >= 1) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "已改派一次，不可再次发起门店改派");
        }
        if (Arrays.stream(RetailComplaintOrderStatusEnum.values()).map(RetailComplaintOrderStatusEnum::getCode).noneMatch(t -> Objects.equals(t, complaintDetail.getOrderStatus()))
                ||
                Arrays.stream(RiskLevelEnum.values()).map(RiskLevelEnum::getCode).noneMatch(t -> Objects.equals(t, complaintDetail.getRiskLevel()))) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "客诉单详情记录中出现了非法的内容, 请联系管理员");
        }
    }

    private RetailComplaintApplySoIn toChangeApplySoIn(UserInfo userInfo,
                                                       RetailComplaintDetaiGoOut retailComplaintDetail, RetailOrgChangeApplyReq req) {
        RetailComplaintApplySoIn soIn = new RetailComplaintApplySoIn();

        // 获取登录人信�?
        CompletableFuture<String> nameFuture = CompletableFuture.supplyAsync(() -> eiamRemoteGateway.getEmployee(userInfo.getMiID()).getName(), applyChangeExecutor);
        // 获取门店信息
        CompletableFuture<List<StoreInfoGoOut>> listCompletableFuture = CompletableFuture.supplyAsync(() -> storeRemoteGateway.getStoreListInfo(Arrays.asList(req.getApplyOrgId(), req.getDesOrgId())), applyChangeExecutor);
        // 联系人姓�?
        CompletableFuture<String> contactNameFuture = CompletableFuture.supplyAsync(() -> KeyCenterUtil.decrypt(retailComplaintDetail.getContactNameC()), applyChangeExecutor);
        // 联系人电�?
        CompletableFuture<String> contactTelFuture = CompletableFuture.supplyAsync(() -> KeyCenterUtil.decrypt(retailComplaintDetail.getContactPhoneC()), applyChangeExecutor);
        String contactName = contactNameFuture.join();
        String contactTel = contactTelFuture.join();
        String loginName = nameFuture.join();
        List<StoreInfoGoOut> storeInfoList = listCompletableFuture.join();

        // 组建soIn
        Map<String, StoreInfoGoOut> storeMap = storeInfoList.stream().collect(Collectors.toMap(StoreInfoGoOut::getOrgId, Function.identity()));
        soIn.setDrNo(retailComplaintDetail.getDrNo());
        soIn.setOrderStatus(retailComplaintDetail.getOrderStatus());
        soIn.setApplyOrgId(req.getApplyOrgId());
        soIn.setApplyOrgName(storeMap.containsKey(req.getApplyOrgId()) ? storeMap.get(req.getApplyOrgId()).getOrgName() : "-");
        soIn.setDesOrgId(req.getDesOrgId());
        soIn.setDesOrdName(storeMap.containsKey(req.getDesOrgId()) ? storeMap.get(req.getDesOrgId()).getOrgName() : "-");
        soIn.setReassignRemark(req.getReassignRemark());
        soIn.setContactName(contactName);
        soIn.setContactPhone(contactTel);
        soIn.setComplaintType(retailComplaintDetail.getComplaintType());
        soIn.setComplaintTypeName(ComplaintTypeEnum.getDescByCode(retailComplaintDetail.getComplaintType()));
        soIn.setProblemCategory(retailComplaintDetail.getProblemCategory());
        soIn.setComplaintScene(parseComplaintScene(retailComplaintDetail.getComplaintContent()));
        soIn.setProblemDesc(retailComplaintDetail.getProblemDesc());
        soIn.setCreateMid(userInfo.getMiID());
        soIn.setCreateName(loginName);
        soIn.setStoreMap(storeMap);
        return soIn;
    }
}
