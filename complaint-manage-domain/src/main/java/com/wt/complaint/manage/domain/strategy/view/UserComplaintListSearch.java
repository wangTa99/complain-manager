package com.wt.complaint.manage.domain.strategy.view;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.api.model.enums.ReportDetailTabEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.resp.UcOrderLightInfo;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarUserRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClubRpcGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.WarrantyInfoGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderExpandGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderExpandGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.WarrantyPeriodGoOut;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.api.service.interfaces.CustomeUserContext;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UcOrderBatchLightInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailFrameGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.CarEmployeeInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UcOrderBatchLightInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintDetailSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.domain.model.UserComplaintExpandInfo;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Service(StrategyConstant.REPORT_ORDER_LIST_SEARCH)
public class UserComplaintListSearch extends AbstractSearch {

    @Autowired
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Resource
    private CarUserRemoteGateway carUserRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Resource
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;

    @Resource
    private WarrantyInfoGateway warrantyInfoGateway;

    @Resource
    private ClubRpcGateway clubRpcGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private ReportAuthManager reportAuthManager;

    @Autowired
    private CustomeUserContext customeUserContext;

    @Override
    public UserComplaintListSearchSoOut searchUserComplaintList(UserComplaintListSearchGoIn goIn) {
        CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(goIn.getMid());
        // 如果用户没有配置汽车岗位,直接返回空列�?
        if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
            return UserComplaintListSearchSoOut.builder().dataList(Collections.emptyList()).total(0L).build();
        }
        // 构建权限信息
        buildAuth(goIn, carEmployeeInfoSoOut);
        // 转换参数
        transformSearchKey(goIn);
        return userComplaintOrderGateway.searchUserComplaintList(goIn);
    }

    @Override
    public UserComplaintDetailFrameSoOut getUserComplaintFrame(UserComplaintDetailFrameGoIn goIn) {
        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();
        log.info("searchUserComplaintList,userInfo:{}", RetailJsonUtil.toJson(userInfo));
        UserComplaintDetailFrameSoOut soOut = new UserComplaintDetailFrameSoOut();
        // 查询举报单是否存�?
        UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut =
                userComplaintOrderGateway.selectDetailByUcNo(goIn.getUcNo());
        if (ObjectUtil.isNull(userComplaintOrderDetailSoOut)) {
            log.info("未找举报单信�?ucNo={}", goIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.USER_COMPLAINT_ORDER_NOT_FOUND);
        }
        // 查询�?
        CompletableFuture<List<CarInfoGoOut>> carFuture =
                getCarFuture(Collections.singletonList(userComplaintOrderDetailSoOut.getVid()));
        // 查询车主
        CompletableFuture<CarUserAggGoOut> carUserFuture = getCarUserFuture(userComplaintOrderDetailSoOut.getVid());
        // 查询车辆动态信�?
        CompletableFuture<GetDynamicInfoResponseGoOut> carDynamicFuture =
                getDynamicInfoFuture(Collections.singletonList(userComplaintOrderDetailSoOut.getVid()));
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture =
                getProcessLogFuture(userComplaintOrderDetailSoOut);
        // 查询vip信息
        CompletableFuture<BatchMemberInfoBO> batchMemberInfoFuture =
                batchGetMemberByVidFuture(Collections.singletonList(userComplaintOrderDetailSoOut.getVid()));
        // 获取请求数据
        List<CarInfoGoOut> carInfoGoOutList = carFuture.join();
        CarUserAggGoOut carUserAgg = carUserFuture.join();
        GetDynamicInfoResponseGoOut carDynamicInfo = carDynamicFuture.join();
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        BatchMemberInfoBO memberInfoBO = batchMemberInfoFuture.join();

        // 设置VID字段
        soOut.setVid(userComplaintOrderDetailSoOut.getVid());
        // 填充基本信息
        soOut.fillBaseInfo(userComplaintOrderDetailSoOut);
        // 填充车辆信息
        soOut.fillCarInfo(carInfoGoOutList, carUserAgg, carDynamicInfo, memberInfoBO);
        // 售后工作台需要车辆保险信�?
        WarrantyPeriodGoOut warrantyPeriodGoOut = null;
        List<Long> midFromAuditProcess = new ArrayList<>();
        // 售后工作台需要额外车辆保险信�?
        String vin = soOut.getVin();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(vin)) {
            CompletableFuture<WarrantyPeriodGoOut> warrantyPeriodFuture = getWarrantyPeriodFuture(vin);
            warrantyPeriodGoOut = warrantyPeriodFuture.join();
        }
        // 查询客诉人员信息（跟进客服，客诉门店处理�? 跟进记录中涉及的申请人等�?
        midFromAuditProcess.add(userComplaintOrderDetailSoOut.getOperatorMid());
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture =
                getEmployInfoFuture(midFromAuditProcess.stream().distinct().collect(
                        Collectors.toList()));
        // 查询门店信息
        CompletableFuture<StoreInfoGoOut> storeInfoFuture =
                getStoreInfoFuture(userComplaintOrderDetailSoOut.getOrgId());
        // 查询tab信息
        List<ReportDetailTabEnum> detailTabByStatus = getDetailTabByStatus(userComplaintOrderDetailSoOut);
        // 获取请求数据
        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        StoreInfoGoOut storeIno = storeInfoFuture.join();
        // 填充举报单信�?
        soOut.fillComplaintOrderInfo(userComplaintOrderDetailSoOut, employeeInfoList, storeIno);
        // 填充tab
        soOut.fillDetailTab(detailTabByStatus, followProcessGoOuts);
        // 填充状态bar
        soOut.constructStatusBar(followProcessGoOuts, userComplaintOrderDetailSoOut);
        // 填充用户按钮权限
        CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(userInfo.getMiID());
        soOut.constructActionList(carEmployeeInfoSoOut, userInfo.getMiID(), reportAuthManager,
                userComplaintOrderDetailSoOut);
        // 填充售后工作台独有的保险信息
        if (ObjectUtil.isNotNull(warrantyPeriodGoOut)) {
            soOut.fillWarrantyPeriod(warrantyPeriodGoOut);
        }
        log.info("获取单据详情框架信息,soOut={}", JSONUtil.toJsonStr(soOut));
        return soOut;
    }

    @Override
    public UserComplaintDetailSoOut getUserComplaintDetail(UserComplaintDetailGoIn goIn) {
        UserComplaintDetailSoOut soOut = new UserComplaintDetailSoOut();
        UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut =
                userComplaintOrderGateway.selectDetailByUcNo(goIn.getUcNo());
        if (ObjectUtil.isNull(userComplaintOrderDetailSoOut)) {
            log.info("举报单详情信息不存在,ucNo={}", goIn.getUcNo());
            throw new BusinessException(ErrorCodeEnums.USER_COMPLAINT_ORDER_NOT_FOUND);
        }
        // 查询投诉内容
        String complaintContent = userComplaintOrderDetailSoOut.getComplaintContent();
        List<TemplateStructSoIn> complaintStructList = new ArrayList<>();
        if (StringUtils.isNotBlank(complaintContent)) {
            complaintStructList = GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {
            }.getType());
        }
        // 获取举报信息中的文件id
        List<Long> fileIdFromStruct = getFileIdFromStruct(complaintStructList);

        // 查询举报人员信息（举报门店处理人�?
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture = getEmployInfoFuture(
                Collections.singletonList(userComplaintOrderDetailSoOut.getOperatorMid()));
        // 查询门店信息
        CompletableFuture<StoreInfoGoOut> storeInfoFuture =
                getStoreInfoFuture(userComplaintOrderDetailSoOut.getOrgId());
        // 查询文件信息
        CompletableFuture<List<FileInfoGoOut>> fileFuture = getFileFuture(fileIdFromStruct);
        // 补充举报信息数据

        // 获取请求数据
        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        StoreInfoGoOut storeIno = storeInfoFuture.join();
        List<FileInfoGoOut> fileInfoList = fileFuture.join();

        // 填充基本信息
        soOut.fillBaseInfo(userComplaintOrderDetailSoOut);
        // 填充门店及人员信�?
        soOut.fillStoreUserInfo(userComplaintOrderDetailSoOut, employeeInfoList, storeIno);
        // 填充举报信息详情，文件url
        soOut.fillDetailInfo(complaintStructList, fileInfoList);
        return soOut;
    }

    @Override
    public UcOrderBatchInfoSoOut getUcOrderInfo(UcOrderBatchInfoSoIn soIn) {
        UcOrderInfoGoIn goIn = new UcOrderInfoGoIn();
        goIn.setUcNoList(soIn.getUcNoList());
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut =
                userComplaintOrderGateway.searchUserComplaintMainData(goIn);
        if (ObjectUtil.isNull(userComplaintOrderMainGoOut)) {
            log.info("举报单信息不存在,ucNo={}", soIn.getUcNoList());
            throw new BusinessException(ErrorCodeEnums.USER_COMPLAINT_ORDER_NOT_FOUND);
        }
        UcOrderBatchInfoSoOut soOut = OrderViewConverter.INSTANCE.toUcOrderBatchInfoSoOut(userComplaintOrderMainGoOut);

        // 查询举报结果
        UcOrderExpandGoIn goInExpand = UcOrderExpandGoIn.builder().ucNoList(soIn.getUcNoList()).build();
        UserComplaintOrderExpandGoOut userComplaintOrderExpandGoOut =
                userComplaintOrderGateway.searchUserComplaintExpandData(goInExpand);

        // 查询门店信息
        List<String> orgIdList = userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().stream()
                .map(UserComplaintOrderInfo::getOrgId)
                .collect(Collectors.toList());
        CompletableFuture<List<StoreInfoGoOut>> storeInfoFuture = batchGetStoreInfoFuture(orgIdList);
        // 查询处理人、创建人名称
        List<Long> operateMidList = userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().stream()
                .map(UserComplaintOrderInfo::getOperatorMid)
                .collect(Collectors.toList());
        List<Long> createMidList = userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().stream()
                .map(UserComplaintOrderInfo::getCreateMid)
                .collect(Collectors.toList());
        operateMidList.addAll(createMidList);
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture = getEmployInfoFuture(operateMidList);
        // 获取举报信息中的文件id
        List<TemplateStructSoIn> complaintStructList = new ArrayList<>();
        List<String> complaintContentList = userComplaintOrderMainGoOut.getUserComplaintOrderInfoList()
                .stream()
                .map(UserComplaintOrderInfo::getComplaintContent)
                .collect(Collectors.toList());
        for (String complaintContent : complaintContentList) {
            if (StringUtils.isNotBlank(complaintContent)) {
                List<TemplateStructSoIn> singleUcOrderTemplate = GsonUtil.fromJson(complaintContent,
                        new TypeToken<List<TemplateStructSoIn>>() {
                        }.getType());
                complaintStructList.addAll(singleUcOrderTemplate);
            }
        }
        List<Long> fileIdFromStruct = getFileIdFromStruct(complaintStructList);
        // 查询文件信息
        CompletableFuture<List<FileInfoGoOut>> fileFuture = getFileFuture(fileIdFromStruct);

        // 数据组装
        List<StoreInfoGoOut> storeListInfo = storeInfoFuture.join();
        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        List<FileInfoGoOut> fileInfoList = fileFuture.join();
        Map<String, StoreInfoGoOut> collect =
                storeListInfo.stream()
                        .collect(Collectors.toMap(StoreInfoGoOut::getOrgId, storeInfo -> storeInfo, (k1, k2) -> k1));
        Map<Long, EmployeeInfoGoOut> employeeMap =
                employeeInfoList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, e -> e, (k1, k2) -> k1));
        Map<String, UserComplaintExpandInfo> expandMap = userComplaintOrderExpandGoOut.getUserComplaintExpandInfoList()
                .stream().collect(Collectors.toMap(UserComplaintExpandInfo::getUcNo, e -> e, (k1, k2) -> k1));

        List<UserComplaintDetailSoOut> ucOrderViewInfoList = soOut.getUcOrderViewInfoList();
        ucOrderViewInfoList.forEach(e -> {
            // 举报单未完成，完成时间展示为 -
            if (e.getOrderStatus() != ReportOrderStatusEnum.FINISH.getCode()) {
                e.setFinishTime("-");
            }
            // 补充门店名称
            Optional.ofNullable(collect.get(e.getOrgId()))
                    .ifPresent(storeInfoGoOut -> e.setOrgName(
                            Optional.ofNullable(storeInfoGoOut.getOrgName()).orElse("")));
            // 补充处理人名�?
            Optional.ofNullable(employeeMap.get(e.getHandleMid()))
                    .ifPresent(employeeInfoGoOut -> e.setHandleName(employeeInfoGoOut.getName()));
            // 补充举报结果
            Optional.ofNullable(expandMap.get(e.getUcNo()))
                    .ifPresent(expand -> e.setJudgeType(expand.getJudgeType()));
            // 补充创建人名�?
            Optional.ofNullable(employeeMap.get(e.getCreateMid()))
                    .ifPresent(employeeInfoGoOut -> e.setCreateName(employeeInfoGoOut.getName()));
            // 补充状态名�?
            e.setOrderStatusName(ReportOrderStatusEnum.getDescByCode(e.getOrderStatus()));
            // 补充附件url信息
            e.fillDetailInfo(fileInfoList);
        });
        return soOut;
    }

    @Override
    public UcOrderBatchLightInfoSoOut getUcOrderLightInfo(UcOrderBatchLightInfoSoIn soIn) {
        UcOrderBatchLightInfoSoOut soOut = new UcOrderBatchLightInfoSoOut();
        List<ComplaintRelationOrderGoOut> relationList = soIn.getRelationList();

        //组装数据
        List<UcOrderLightInfo> ucOrderLightInfoList = relationList.stream()
                .map(e -> {
                    UcOrderLightInfo ucOrderLightInfo = new UcOrderLightInfo();
                    ucOrderLightInfo.setBizNo(e.getBizNo());
                    ucOrderLightInfo.setUcNo(e.getComplaintNo());
                    ucOrderLightInfo.setCreateTime(
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(e.getCreateTime()));
                    return ucOrderLightInfo;
                }).collect(Collectors.toList());
        soOut.setUcOrderInfoList(ucOrderLightInfoList);
        return soOut;
    }
}