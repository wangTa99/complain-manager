package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.wt.complaint.manage.api.model.enums.*;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.*;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.constant.ComplaintActionConst;
import com.wt.complaint.manage.domain.converter.DomainConverter;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.UserActionAuthContext;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigParser;
import com.wt.complaint.manage.domain.model.BatchComplaintQueryResult;
import com.wt.complaint.manage.domain.model.BatchQueryFutures;
import com.wt.complaint.manage.domain.model.ComplaintDataCollectionResult;
import com.wt.complaint.manage.domain.strategy.ComplaintListFactory;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import com.wt.complaint.manage.domain.strategy.complaintlist.ComplaintListStrategy;
import com.wt.complaint.manage.domain.strategy.complaintlist.PadComplaintListSearch;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.api.model.enums.PadTabEnum.*;
import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND;

@Slf4j
@Service
@SuppressWarnings({"squid:S1192", "squid:S3252"})
public class ComplaintViewServiceImpl implements ComplaintViewService {

    @Resource
    private ComplaintListFactory complaintListFactory;

    @Resource
    private PadComplaintListSearch padComplaintListSearch;

    @Resource
    private MoneThreadPoolExecutor complaintTabCountExecutor;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;


    @Resource
    private ComplaintGateway complaintGateway;

    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Resource
    private ComplaintTagGateway complaintTagGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Resource
    private CarUserRemoteGateway carUserRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private WarrantyInfoGateway warrantyInfoGateway;

    @Resource
    private UserAuthManager userAuthManager;
    @Resource
    private ClubRpcGateway clubRpcGateway;

    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;

    @Resource
    UtilityRemoteGateway utilityRemoteGateway;

    @Resource
    UpcConfigParser upcConfigParser;

    @Resource
    UpcConfigLocalCache localCache;

    @Override
    public ComplaintFrameInfoSoOut getComplaintFrameInfo(ComplaintFrameInfoSoIn soIn) {
        String roleStr = RpcContext.getContext().getAttachment("$curr_role");
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        ComplaintFrameInfoSoOut soOut = new ComplaintFrameInfoSoOut();
        OrderListGoIn orderListGoIn = new OrderListGoIn();
        orderListGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(orderListGoIn);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("未找到客诉单信息,complaintNo={}", soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);

        // 查询�?
        CompletableFuture<List<CarInfoGoOut>> carFuture = getCarFuture(Collections.singletonList(complaintOrderInfoGoIn.getVid()));
        // 查询车主
        CompletableFuture<CarUserAggGoOut> carUserFuture = getCarUserFuture(complaintOrderInfoGoIn.getVid());
        // 查询车辆动态信�?
        CompletableFuture<GetDynamicInfoResponseGoOut> carDynamicFuture = getDynamicInfoFuture(Collections.singletonList(complaintOrderInfoGoIn.getVid()));
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture = getProcessLogFuture(complaintOrderInfoGoIn);
        // 查询vip信息
        CompletableFuture<BatchMemberInfoBO> batchMemberInfoFuture = batchGetMemberByVidFuture(Collections.singletonList(complaintOrderInfoGoIn.getVid()));
        // 获取请求数据
        List<CarInfoGoOut> carInfoGoOutList = carFuture.join();
        CarUserAggGoOut carUserAgg = carUserFuture.join();
        GetDynamicInfoResponseGoOut carDynamicInfo = carDynamicFuture.join();
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        BatchMemberInfoBO memberInfoBO = batchMemberInfoFuture.join();

        // 设置VID字段
        soOut.setVid(complaintOrderInfoGoIn.getVid());
        // 填充基本信息
        soOut.fillBaseInfo(complaintOrderInfoGoIn);
        // 填充车辆信息
        soOut.fillCarInfo(carInfoGoOutList, carUserAgg, carDynamicInfo, memberInfoBO);
        // 获取车辆质保信息（包含里程和交付日期�?
        WarrantyPeriodGoOut warrantyPeriodGoOut = null;
        String vin = soOut.getVin();
        if (StringUtils.isNotBlank(vin)) {
            CompletableFuture<WarrantyPeriodGoOut> warrantyPeriodFuture = getWarrantyPeriodFuture(vin);
            warrantyPeriodGoOut = warrantyPeriodFuture.join();
        }
        // 填充车辆里程和交付日期信�?
        soOut.fillCarMileageAndDeliveryDate(warrantyPeriodGoOut);
        List<Long> midFromAuditProcess = new ArrayList<>();
        if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(soIn.getSource())) {
            // 售后工作台需要额外添加审批日�?
            midFromAuditProcess = getMidFromAuditProcess(followProcessGoOuts);
        }

        // 查询客诉人员信息（跟进客服，客诉门店处理�? 跟进记录中涉及的申请人，审批人等�?
        midFromAuditProcess.add(complaintOrderInfoGoIn.getCustomerServiceMid());
        midFromAuditProcess.add(complaintOrderInfoGoIn.getOperatorMid());
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture = getEmployInfoFuture(midFromAuditProcess.stream().distinct().collect(Collectors.toList()));
        // 查询门店信息
        CompletableFuture<StoreInfoGoOut> storeInfoFuture = getStoreInfoFuture(complaintOrderInfoGoIn.getOrgId());
        // 查询客诉标签
        CompletableFuture<List<ComplaintTagGoOut>> complaintTagFuture = getComplaintTagFuture(complaintOrderInfoGoIn.getComplaintNo());
        // 查询tab信息
        List<DetailTabEnum> detailTabByStatus = getDetailTabByStatus(complaintOrderInfoGoIn);

        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        StoreInfoGoOut storeIno = storeInfoFuture.join();
        List<ComplaintTagGoOut> complaintTagGoOutList = complaintTagFuture.join();

        // 填充客诉信息
        soOut.fillComplaintOrderInfo(complaintOrderInfoGoIn, employeeInfoList, storeIno);
        // 填充客诉标签
        soOut.fillComplaintTag(complaintTagGoOutList);
        // 填充tab
        soOut.fillDetailTab(detailTabByStatus, followProcessGoOuts);
        // 填充状态bar
        soOut.constructStatusBar(followProcessGoOuts, complaintOrderInfoGoIn);
        // 填充用户按钮权限
        soOut.constructActionList(roleStr, Long.valueOf(miID), userAuthManager, complaintOrderInfoGoIn);
        // 填充售后工作台独有的保险信息&审批日志列表
        if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(soIn.getSource())) {
            soOut.fillWarrantyPeriod(warrantyPeriodGoOut);
            soOut.fillAuditProcessLog(followProcessGoOuts, employeeInfoList);
        }
        return soOut;
    }

    @Override
    public ComplaintFrameInfoSoOut getComplaintAuth(ComplaintFrameInfoSoIn param) {
        String roleStr = RpcContext.getContext().getAttachment("$curr_role");
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        if (StringUtils.isBlank(roleStr)) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "没获取到用户当前登录角色");
        }
        if (StringUtils.isBlank(miID)) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "用户未登�?);
        }
        ComplaintFrameInfoSoOut soOut = new ComplaintFrameInfoSoOut();
        OrderListGoIn orderListGoIn = new OrderListGoIn();
        orderListGoIn.setComplaintNo(param.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(orderListGoIn);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("未找到客诉单信息,complaintNo={}", param.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        log.info("getComplaintAuth complaintOrderInfoGoIn:{}", GsonUtil.toJson(complaintOrderInfoGoIn));

        List<String> roleKeyList = upcConfigParser.getRoleList(UpcConfigGoIn.builder()
                                                                            .moduleKey("complaintFrame")
                                                                            .orgId(complaintOrderInfoGoIn.getOrgId())
                                                                            .mid(miID)
                                                                            .currRole(roleStr)
                                                                            .build());
        Map<String, List<String>> upcConfigMap = localCache.getUpcConfigMap();
        Set<String> resourceTags = new HashSet<>();


        // 3. 服务顾问 接单之后只能 处理自己的工单�?
        // 特殊处理: 服务顾问
        if (roleKeyList.contains(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey())
                && !(Objects.equals(complaintOrderInfoGoIn.getOperatorMid(), Long.valueOf(miID)))) {
            roleKeyList.remove(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey());
            List<String> receiverTags = upcConfigMap.getOrDefault("complaintFrame" + "|" + ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey(), new ArrayList<>());

            // 除了待接�?收押�? 查看定损单权限不过滤，其他都过滤
            List<String> allAuth = Lists.newArrayList(ComplaintActionConst.PICK_UP,
                    ComplaintActionConst.APPLY_REASSIGN_STORE);
            resourceTags.addAll(receiverTags.stream().filter(t -> StrUtil.containsAny(t, allAuth.toArray(new String[0]))).collect(Collectors.toList()));
        }

        // 合并角色的资源权�?
        for (String roleKey : roleKeyList) {
            List<String> roleTags = upcConfigMap.getOrDefault("complaintFrame" + "|" + roleKey, new ArrayList<>());
            resourceTags.addAll(roleTags);
        }

        // 特殊处理：升级button，仅产品风险类型的客诉单可以展示
        if (!ComplaintTypeEnum.PRODUCT_RISK.getCode().equals(complaintOrderInfoGoIn.getComplaintType())) {
            resourceTags = resourceTags
                    .stream()
                    .filter(t -> !StrUtil.containsAny(t, ComplaintActionConst.UPGRADE_COMPLAINT))
                    .collect(Collectors.toSet());
            log.info("getComplaintAuth resourceTags:{}", GsonUtil.toJson(resourceTags));
        }

        UserActionAuthContext context = new UserActionAuthContext();
        context.setRole(roleStr);
        context.setLoginMid(Long.valueOf(miID));
        context.setComplaintNo(param.getComplaintNo());
        context.setHandlerMid(complaintOrderInfoGoIn.getOperatorMid());
        context.setResponsibility(complaintOrderInfoGoIn.getResponsibility());
        context.setOrgId(complaintOrderInfoGoIn.getOrgId());
        context.setStatus(complaintOrderInfoGoIn.getStatus());
        context.setCreateSource(complaintOrderInfoGoIn.getCreateSource());
        context.setReviewed(complaintOrderInfoGoIn.getReviewed());
        context.setComplaintType(complaintOrderInfoGoIn.getComplaintType());
        context.setExemptionApplyTimes(complaintOrderInfoGoIn.getExemptionApplyTimes());
        List<String> buttons = upcConfigParser.calcButtons(new ArrayList<>(resourceTags), context, complaintOrderInfoGoIn);

        UserActionAuth userAuth = new UserActionAuth();
        userAuth.setActionsList(buttons);
        userAuth.setButtons(buttons);
        soOut.setUserActionAuth(userAuth);
        return soOut;
    }

    @Override
    public ComplaintDetailSoOut getComplaintDetail(ComplaintDetailSoIn soIn) {
        String roleStr = RpcContext.getContext().getAttachment("$curr_role");
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        ComplaintDetailSoOut soOut = new ComplaintDetailSoOut();
        OrderListGoIn orderListGoIn = new OrderListGoIn();
        orderListGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(orderListGoIn);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("未找到客诉单信息,complaintNo={}", soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }
        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);

        // 查询投诉内容
        String complaintContent = complaintOrderInfoGoIn.getComplaintContent();
        List<TemplateStructSoIn> complaintStructList = new ArrayList<>();
        if (StringUtils.isNotBlank(complaintContent)) {
            complaintStructList = GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {
            }.getType());
        }
        // 获取客诉信息中的文件id
        List<Long> fileIdFromStruct = getFileIdFromStruct(complaintStructList);


        // 查询客诉人员信息（跟进客服，客诉门店处理人）
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture = getEmployInfoFuture(Arrays.asList(complaintOrderInfoGoIn.getCustomerServiceMid(), complaintOrderInfoGoIn.getOperatorMid()));
        // 查询门店信息
        CompletableFuture<StoreInfoGoOut> storeInfoFuture = getStoreInfoFuture(complaintOrderInfoGoIn.getOrgId());
        // 查询客诉标签
        CompletableFuture<List<ComplaintTagGoOut>> complaintTagFuture = getComplaintTagFuture(complaintOrderInfoGoIn.getComplaintNo());
        // 查询文件信息
        CompletableFuture<List<FileInfoGoOut>> fileFuture = getFileFuture(fileIdFromStruct, null);
        // 补充客诉信息数据

        // 获取请求数据
        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        StoreInfoGoOut storeIno = storeInfoFuture.join();
        List<ComplaintTagGoOut> complaintTagGoOutList = complaintTagFuture.join();
        List<FileInfoGoOut> fileInfoList = fileFuture.join();

        // 填充基本信息
        soOut.fillBaseInfo(complaintOrderInfoGoIn);
        // 填充客诉标签
        soOut.fillComplaintTag(complaintTagGoOutList, complaintOrderInfoGoIn);
        // 填充门店及人员信�?
        soOut.fillStoreUserInfo(complaintOrderInfoGoIn, employeeInfoList, storeIno);
        // 填充客诉信息详情，文件url，考核标签
        soOut.fillDetailInfo(complaintStructList, fileInfoList);
        return soOut;
    }

    @Override
    public ComplaintEditDetailSoOut getComplaintEditDetail(ComplaintDetailSoIn soIn) {
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(soIn.getComplaintNo());

        String complaintContent = complaintOrderGoOut.getComplaintContent();

        // 投诉场景：从 complaint_content �?fieldCode=complaint 解析
        FieldValueSoIn complaint = ParseComplaintContentUtil.parseComplaintFieldValue(complaintContent);

        // 风险等级、是否涉媒、涉媒链接：直接�?complaintOrderGoOut 获取
        // 风险等级返回 code，如 1, 2, 3, 4
        String riskLevel = String.valueOf(complaintOrderGoOut.getRiskLevel());
        String mediaInvolved = String.valueOf(complaintOrderGoOut.getMediaInvolved());
        String mediaLink = complaintOrderGoOut.getMediaLink();

        return ComplaintEditDetailSoOut.builder()
                .complaint(complaint)
                .riskLevel(riskLevel)
                .mediaInvolved(mediaInvolved)
                .mediaLink(mediaLink)
                .build();
    }

    @Override
    public ComplaintBatchDetailSoOut batchGetComplaintDetail(ComplaintBatchDetailSoIn param) {
        // 参数校验
        validateBatchDetailParam(param);

        // 查询客诉单信�?
        BatchComplaintQueryResult queryResult = queryComplaintOrders(param.getComplaintNoList());

        // 解析投诉内容并收集相关数�?
        ComplaintDataCollectionResult dataCollection = collectComplaintData(queryResult.getAllOrderList());

        // 并行查询相关信息
        BatchQueryFutures futures = createBatchQueryFutures(dataCollection, queryResult.getOldComplaintNoList());

        // 查询客诉标签
        List<ComplaintTagGoOut> complaintTagList = queryComplaintTags(queryResult.getOldComplaintNoList(),
                queryResult.getDeliverRetailComplaintList(),
                futures.getComplaintTagFuture());

        // 等待并行查询完成，组装结�?
        return assembleComplaintDetails(queryResult.getAllOrderMap(), dataCollection.getStructMap(),
                futures, complaintTagList);
    }

    /**
     * 参数校验
     */
    private void validateBatchDetailParam(ComplaintBatchDetailSoIn param) {
        if (CollectionUtils.isEmpty(param.getComplaintNoList())) {
            log.error("客诉单号列表为空,参数异常");
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号列表为空,参数异常");
        }
    }

    /**
     * 查询客诉单信�?
     */
    private BatchComplaintQueryResult queryComplaintOrders(List<String> complaintNoList) {
        List<ComplaintOrderInfoGoIn> allOrderList = new ArrayList<>();
        List<String> oldComplaintNoList = complaintNoList
                .stream()
                .filter(e -> e.startsWith(UcOrderTypeEnum.COMPLAINT_ORDER.getPrefix()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(oldComplaintNoList)) {
            OrderListGoIn orderListGoIn = new OrderListGoIn();
            orderListGoIn.setComplaintNoList(oldComplaintNoList);
            allOrderList.addAll(complaintOrderRepositoryGateway.findList(orderListGoIn));
        }


        List<String> newComplaintNoList = complaintNoList
                .stream()
                .filter(e -> e.startsWith(UcOrderTypeEnum.DELIVER_COMPLAINT_ORDER.getPrefix())
                        || e.startsWith(UcOrderTypeEnum.RETAIL_COMPLAINT_ORDER.getPrefix()))
                .collect(Collectors.toList());
        List<DeliverComplaintBO> deliverRetailComplaintList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(newComplaintNoList)) {
            deliverRetailComplaintList =
                    deliverComplaintGateway.selectByDrNoList(newComplaintNoList);
            allOrderList.addAll(DomainConverter.INSTANCE.convertToGoInList(deliverRetailComplaintList));
        }


        if (CollectionUtils.isEmpty(allOrderList)) {
            log.info("batchGetComplaintDetail 未找到客诉单信息,complaintNo={}", complaintNoList);
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }

        Map<String, ComplaintOrderInfoGoIn> allOrderMap = allOrderList.stream()
                .collect(Collectors.toMap(ComplaintOrderInfoGoIn::getComplaintNo,
                        Function.identity(), (k1, k2) -> k1));

        return BatchComplaintQueryResult.builder()
                .allOrderList(allOrderList)
                .allOrderMap(allOrderMap)
                .deliverRetailComplaintList(deliverRetailComplaintList)
                .oldComplaintNoList(oldComplaintNoList)
                .build();
    }

    @Override
    public ComplaintProcessListSoOut getComplaintProcessRecords(ComplaintProcessSoIn soIn) {
        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();
        OrderListGoIn orderListGoIn = new OrderListGoIn();
        orderListGoIn.setComplaintNo(soIn.getComplaintNo());
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(orderListGoIn);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("未找到客诉单信息,complaintNo={}", soIn.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }

        ComplaintOrderInfoGoIn complaintOrderInfoGoIn = orderList.get(0);
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture = getProcessLogFuture(complaintOrderInfoGoIn);
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        Map<Long, FileInfoGoOut> processRecordsAttachments = getProcessRecordsAttachments(followProcessGoOuts);
        soOut.fillProcessList(followProcessGoOuts, processRecordsAttachments);
        return soOut;
    }

    @Override
    public ComplaintListSearchSoOut searchComplaintList(ComplaintListSearchGoIn param) {
        String source = param != null ? param.getSource() : null;
        Stopwatch stopwatch = Stopwatch.createStarted();

        ComplaintListStrategy complaintListStrategy = complaintListFactory.getStrategy(param);
        log.info("ComplaintViewServiceImpl#searchComplaintList strategy cost, source={}, costMs={}",
                source, stopwatch.elapsed(TimeUnit.MILLISECONDS));

        if (complaintListStrategy == null) {
            return new ComplaintListSearchSoOut();
        }

        stopwatch.reset().start();
        ComplaintListSearchSoOut result = complaintListStrategy.searchComplaintList(param);
        log.info("ComplaintViewServiceImpl#searchComplaintList listQuery cost, source={}, costMs={}",
                source, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    public void initParam(ComplaintListSearchGoIn newGoIn, ComplaintListSearchGoIn param) {
        newGoIn.setSource(param.getSource());
        newGoIn.setOrgId(param.getOrgId());
        newGoIn.setRoleList(param.getRoleList());
        newGoIn.setMid(param.getMid());
        newGoIn.setCurrRole(param.getCurrRole());
        newGoIn.setMediaInvolved(param.getMediaInvolved());
        newGoIn.setOnlyShowMyCompositeOrder(param.getOnlyShowMyCompositeOrder());
    }

    @Override
    public CountComplaintListTabSoOut countComplaintListTab(ComplaintListSearchGoIn param) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ComplaintListSearchGoIn totalGoIn = new ComplaintListSearchGoIn();
        initParam(totalGoIn, param);
        ComplaintListSearchGoIn pendingOrderGoIn = new ComplaintListSearchGoIn();
        initParam(pendingOrderGoIn, param);
        ComplaintListSearchGoIn inProgressGoIn = new ComplaintListSearchGoIn();
        initParam(inProgressGoIn, param);
        ComplaintListSearchGoIn approachingTimeoutGoIn = new ComplaintListSearchGoIn();
        initParam(approachingTimeoutGoIn, param);
        ComplaintListSearchGoIn finishEvaluationPendingGoIn = new ComplaintListSearchGoIn();
        initParam(finishEvaluationPendingGoIn, param);
        ComplaintListSearchGoIn onlyViewGoIn = new ComplaintListSearchGoIn();
        initParam(onlyViewGoIn, param);
        ComplaintListSearchGoIn pendingReviewGoIn = new ComplaintListSearchGoIn();
        initParam(pendingReviewGoIn, param);
        padComplaintListSearch.transformSearchKey(param);
        if (TOTAL.getCode().equals(param.getTab())) {
            totalGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (PENDING_ORDER.getCode().equals(param.getTab())) {
            pendingOrderGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (IN_PROGRESS.getCode().equals(param.getTab())) {
            inProgressGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (APPROACHING_TIMEOUT.getCode().equals(param.getTab())) {
            approachingTimeoutGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (FINISH_EVALUATION_PENDING.getCode().equals(param.getTab())) {
            finishEvaluationPendingGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (ONLY_VIEW.getCode().equals(param.getTab())) {
            onlyViewGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        } else if (PENDING_REVIEW.getCode().equals(param.getTab())) {
            pendingReviewGoIn = RetailJsonUtil.fromJson(RetailJsonUtil.toJson(param), ComplaintListSearchGoIn.class);
        }
        List<CompletableFuture<Void>> fnList = Lists.newArrayList();
        CountComplaintListTabSoOut result = new CountComplaintListTabSoOut();
        totalGoIn.setTab(TOTAL.getCode());
        pendingOrderGoIn.setTab(PENDING_ORDER.getCode());
        inProgressGoIn.setTab(IN_PROGRESS.getCode());
        approachingTimeoutGoIn.setTab(APPROACHING_TIMEOUT.getCode());
        finishEvaluationPendingGoIn.setTab(FINISH_EVALUATION_PENDING.getCode());
        onlyViewGoIn.setTab(ONLY_VIEW.getCode());
        pendingReviewGoIn.setTab(PENDING_REVIEW.getCode());
        ComplaintListSearchGoIn finalTotalGoIn = totalGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setTotal(getComplaintTabCount(finalTotalGoIn, TOTAL)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalPendingOrderGoIn = pendingOrderGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setPendingOrderCount(getComplaintTabCount(finalPendingOrderGoIn, PENDING_ORDER)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalInProgressGoIn = inProgressGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setDealingCount(getComplaintTabCount(finalInProgressGoIn, IN_PROGRESS)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalApproachingTimeoutGoIn = approachingTimeoutGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setApproachingTimeoutCount(getComplaintTabCount(finalApproachingTimeoutGoIn, APPROACHING_TIMEOUT)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalFinishEvaluationPendingGoIn = finishEvaluationPendingGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setFinishEvaluationPendingCount(getComplaintTabCount(finalFinishEvaluationPendingGoIn, FINISH_EVALUATION_PENDING)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalOnlyViewGoIn = onlyViewGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setOnlyViewCount(getComplaintTabCount(finalOnlyViewGoIn, ONLY_VIEW)), complaintTabCountExecutor));
        ComplaintListSearchGoIn finalPendingReviewGoIn = pendingReviewGoIn;
        fnList.add(CompletableFuture.runAsync(() -> result.setPendingReviewCount(getComplaintTabCount(finalPendingReviewGoIn, PENDING_REVIEW)), complaintTabCountExecutor));
        CompletableFuture.allOf(fnList.toArray(new CompletableFuture[0])).join();
        log.info("ComplaintViewService#countComplaintListTab cost:{}ms, param:{}, result:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS), RetailJsonUtil.toJson(param), RetailJsonUtil.toJson(result));
        return result;
    }

    private Integer getComplaintTabCount(ComplaintListSearchGoIn param, PadTabEnum tabEnum) {
        ComplaintListSearchGoIn newGoIn = padComplaintListSearch.genNewSearchGoIn(tabEnum, param);
        return complaintGateway.getComplaintOrderCount(newGoIn);
    }

    @Override
    public SimpleComplaintDetailSoOut getSimpleComplaintDetail(SimpleComplaintDetailSoIn soIn) {
        ComplaintOrderGoOut complaintOrderGoOut = complaintGateway.selectByComplaintNo(soIn.getComplaintNo());
        if (complaintOrderGoOut == null) {
            log.error("ComplaintViewService#getSimpleComplaintDetail complaintNo:{} not found", soIn.getComplaintNo());
            throw new BusinessException(COMPLAINT_ORDER_NOT_FOUND);
        }
        SimpleComplaintDetailSoOut result = new SimpleComplaintDetailSoOut();
        // 组装 ComplaintInfoGoOut
        result.setComplaintInfo(genComplaintInfo(complaintOrderGoOut));
        // 组装 CarInfoSoOut
        SimpleComplaintDetailSoOut.CarInfoSoOut carInfoSoOut = new SimpleComplaintDetailSoOut.CarInfoSoOut();
        result.setCarInfo(carInfoSoOut);
        if (StringUtils.isBlank(complaintOrderGoOut.getVid())) {
            log.info("客诉单未绑定车辆vid,getSimpleComplaintDetail, complaintOrderGoOut:{}", RetailJsonUtil.toJson(complaintOrderGoOut));
        } else {
            // 查询车辆信息
            List<CarInfoGoOut> carInfoGoOutList = carRemoteGateway.getCarSimpleInfo(Collections.singletonList(complaintOrderGoOut.getVid()), null);
            if (CollectionUtils.isEmpty(carInfoGoOutList)) {
                log.info("未查询到车辆信息,getSimpleComplaintDetail, vid:{}", complaintOrderGoOut.getVid());
            } else {
                CarInfoGoOut carInfoGoOut = carInfoGoOutList.get(0);
                carInfoSoOut.setCarImg(carInfoGoOut.getCarImg());
                carInfoSoOut.setVid(carInfoGoOut.getVid());
                carInfoSoOut.setVin(carInfoGoOut.getVin());
                carInfoSoOut.setCarType(carInfoGoOut.getCarType());

                // 查询车主信息
                List<OwnerInfoItemGoOut> ownerInfoList = carRemoteGateway.getOwnerInfo(soIn.getMidStr(), Collections.singletonList(complaintOrderGoOut.getVid()));
                if (CollectionUtils.isEmpty(ownerInfoList)) {
                    log.info("未查询到车主信息,getSimpleComplaintDetail, vid:{}", complaintOrderGoOut.getVid());
                } else {
                    OwnerInfoItemGoOut ownerInfo = ownerInfoList.get(0);
                    carInfoSoOut.setOwnerName(ownerInfo.getName());
                    carInfoSoOut.setOwnerTel(ownerInfo.getMobile());
                    carInfoSoOut.setCarNo(ownerInfo.getCarNo());
                    carInfoSoOut.setOwnerMiId(ownerInfo.getMid() != null ? ownerInfo.getMid() : 0L);
                }
            }
        }
        return result;
    }

    @Override
    public GetComplaintHandlerSoOut getComplaintHandler(GetComplaintHandlerSoIn soIn) {
        GetComplaintHandlerSoOut soOut = new GetComplaintHandlerSoOut();
        // 获取员工信息及客诉单信息
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoByStoreFuture = getEmployInfoByStoreFuture(CarEmployeeEnum.getHandlerPositionId(), soIn.getOrgId());
        CompletableFuture<List<ComplaintOrderInfoGoIn>> orderListFuture = getOrderListFuture(soIn.getOrgId(), ComplaintStatusEnum.getUnfinishedStatus());

        // 获取数据
        List<EmployeeInfoGoOut> employeeInfoGoOuts = employInfoByStoreFuture.join();
        List<ComplaintOrderInfoGoIn> orderInfoList = orderListFuture.join();

        // 组装数据
        soOut.fillHandlerInfoList(employeeInfoGoOuts, orderInfoList);
        return soOut;
    }

    private Map<Long, FileInfoGoOut> getProcessRecordsAttachments(List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        Map<Long, FileInfoGoOut> resultMap = new HashMap<>();
        if (CollUtil.isEmpty(followProcessGoOuts)) {
            return resultMap;
        }
        List<Long> fileIdUrl = new ArrayList<>();
        for (ComplaintFollowProcessGoOut followProcessGoOut : followProcessGoOuts) {
            if (StringUtils.isNotEmpty(followProcessGoOut.getProcessContent())) {
                RecordInfoGoIn recordInfoGoIn = GsonUtil.fromJson(followProcessGoOut.getProcessContent(), RecordInfoGoIn.class);
                if (CollUtil.isNotEmpty(recordInfoGoIn.getAttachments())) {
                    List<Long> tempFileIdUrl = recordInfoGoIn.getAttachments().stream().map(AttachmentGoIn::getId).collect(Collectors.toList());
                    fileIdUrl.addAll(tempFileIdUrl);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fileIdUrl)) {
            List<FileInfoGoOut> fileList = fileRemoteGateway.getFileList(fileIdUrl, null);
            resultMap = fileList.stream().collect(Collectors.toMap(FileInfoGoOut::getFileId, e -> e));
        }
        return resultMap;

    }
    private SimpleComplaintDetailSoOut.ComplaintInfoGoOut genComplaintInfo(ComplaintOrderGoOut complaintOrderGoOut) {
        SimpleComplaintDetailSoOut.ComplaintInfoGoOut complaintInfoGoOut = OrderViewConverter.INSTANCE.toComplaintInfoGoOut(complaintOrderGoOut);
        // 用户名称查询
        EmployeeListGoIn midListParam = EmployeeListGoIn.builder().miIdList(Arrays.asList(complaintInfoGoOut.getCustomerServiceMid(), complaintInfoGoOut.getOperatorId())).build();
        List<EmployeeInfoGoOut> employeeList = eiamRemoteGateway.getEmployeeList(midListParam);
        Map<Long, EmployeeInfoGoOut> employeeMap = employeeList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, Function.identity(), (a, b) -> a));
        EmployeeInfoGoOut customerServiceInfo = employeeMap.get(complaintInfoGoOut.getCustomerServiceMid());
        if (customerServiceInfo != null) {
            complaintInfoGoOut.setCustomerServiceName(customerServiceInfo.getName());
            if (StringUtils.isNotBlank(customerServiceInfo.getEmailPrefix())) {
                complaintInfoGoOut.setCustomerServiceEmailPrefix(customerServiceInfo.getEmailPrefix());
            }
        }
        EmployeeInfoGoOut operatorInfo = employeeMap.get(complaintInfoGoOut.getOperatorId());
        if (operatorInfo != null) {
            complaintInfoGoOut.setOperatorName(operatorInfo.getName());
        }
        // 店铺名称查询
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(complaintInfoGoOut.getOrgId());
        if (storeInfo != null) {
            complaintInfoGoOut.setOrgName(storeInfo.getOrgName());
        }
        return complaintInfoGoOut;
    }

    private CompletableFuture<List<CarInfoGoOut>> getCarFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getCarSimpleInfo(vidList, null), commonThreadPoolExecutor);
    }

    private CompletableFuture<CarUserAggGoOut> getCarUserFuture(String vid) {
        return CompletableFuture.supplyAsync(() -> carUserRemoteGateway.userAggQuery(CarUserAggGoIn.builder().vid(vid).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<GetDynamicInfoResponseGoOut> getDynamicInfoFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getDynamicInfo(vidList), commonThreadPoolExecutor);
    }

    private CompletableFuture<WarrantyPeriodGoOut> getWarrantyPeriodFuture(String vin) {
        return CompletableFuture.supplyAsync(() -> warrantyInfoGateway.getCarWarrantyPeriodInfo(vin), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<EmployeeInfoGoOut>> getEmployInfoFuture(List<Long> midList) {
        return CompletableFuture.supplyAsync(() -> eiamRemoteGateway.getEmployeeList(EmployeeListGoIn.builder().miIdList(midList).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<EmployeeInfoGoOut>> getEmployInfoByStoreFuture(List<Integer> positionIdList, String orgId) {
        return CompletableFuture.supplyAsync(() -> eiamRemoteGateway.queryEmployeeByStore(StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<StoreInfoGoOut> getStoreInfoFuture(String orgId) {
        return CompletableFuture.supplyAsync(() -> storeRemoteGateway.getStoreInfo(orgId), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<StoreInfoGoOut>> getStoreInfoBatchFuture(List<String> orgIdList) {
        return CompletableFuture.supplyAsync(() -> storeRemoteGateway.listCarStore(CarStoreListGoIn.builder().filter(new String[]{"base"}).orgIdList(orgIdList).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<ComplaintTagGoOut>> getComplaintTagFuture(String complaintNo) {
        return CompletableFuture.supplyAsync(() -> complaintTagGateway.getComplaintTagByComplaintNo(ComplaintTagListGoIn.builder().complaintNoList(Arrays.asList(complaintNo)).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<ComplaintTagGoOut>> getComplaintTagBatchFuture(List<String> complaintNoList) {
        return CompletableFuture.supplyAsync(() -> complaintTagGateway.getComplaintTagByComplaintNo(ComplaintTagListGoIn.builder().complaintNoList(complaintNoList).build()), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<ComplaintOrderInfoGoIn>> getOrderListFuture(String orgId, List<Integer> statusList) {
        return CompletableFuture.supplyAsync(() -> complaintOrderRepositoryGateway.findList(OrderListGoIn.builder().orgId(orgId).complaintStatusList(statusList).build()), commonThreadPoolExecutor);
    }

    private List<DetailTabEnum> getDetailTabByStatus(ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        List<DetailTabEnum> detailTabEnums = DetailTabEnum.listTab(complaintOrderInfoGoIn.getOnlyView(), complaintOrderInfoGoIn.getStatus());
        return detailTabEnums;
    }

    private CompletableFuture<List<ComplaintFollowProcessGoOut>> getProcessLogFuture(ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        return CompletableFuture.supplyAsync(() -> processRepositoryGateway.getProcessListByNo(complaintOrderInfoGoIn.getComplaintNo()), commonThreadPoolExecutor);
    }

    private CompletableFuture<List<FileInfoGoOut>> getFileFuture(List<Long> fileIds, Integer expireTime) {
        return CompletableFuture.supplyAsync(() -> fileRemoteGateway.getFileList(fileIds, expireTime), commonThreadPoolExecutor);
    }

    private List<Long> getFileIdFromStruct(List<TemplateStructSoIn> complaintStructList) {
        List<Long> fileIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(complaintStructList)) {
            for (TemplateStructSoIn templateStructSoIn : complaintStructList) {
                List<Long> tempFileIdList = templateStructSoIn.getFields()
                        .stream()
                        .filter(e -> CollUtil.isNotEmpty(e.getAttachmentList()))
                        .flatMap(e -> e.getAttachmentList().stream())
                        .map(AttachmentSoIn::getId)
                        .collect(Collectors.toList());
                fileIdList.addAll(tempFileIdList);
            }
        }
        return fileIdList;
    }

    private List<Long> getMidFromAuditProcess(List<ComplaintFollowProcessGoOut> followProcessList) {
        List<Long> midList = new ArrayList<>();
        List<ComplaintFollowProcessGoOut> auditProcessList = followProcessList.stream().filter(e -> ProcessTypeEnum.getAuditProcessCodeList().contains(e.getProcessType())).collect(Collectors.toList());
        for (ComplaintFollowProcessGoOut processGoOut : auditProcessList) {
            if (ProcessTypeEnum.getApplyProcessCodeList().contains(processGoOut.getProcessType())) {
                RecordInfoGoIn recordInfoGoIn = GsonUtil.fromJson(processGoOut.getProcessContent(), RecordInfoGoIn.class);
                midList.add(recordInfoGoIn.getApplyMid());
            }
            if (ProcessTypeEnum.getOnlyAuditProcessCodeList().contains(processGoOut.getProcessType())) {
                RecordInfoGoIn recordInfoGoIn = GsonUtil.fromJson(processGoOut.getProcessContent(), RecordInfoGoIn.class);
                midList.add(recordInfoGoIn.getAuditMid());
            }
        }
        return midList;
    }

    public CompletableFuture<BatchMemberInfoBO> batchGetMemberByVidFuture(List<String> vidList) {
        if (CollUtil.isEmpty(vidList)) {
            return CompletableFuture.completedFuture(BatchMemberInfoBO.builder().build());
        }
        return CompletableFuture.supplyAsync(() -> {
            BatchMemberInfoBO ownerInfo = BatchMemberInfoBO.builder().build();
            try {
                log.info("batchGetMemberByVidFuture param : {}", GsonUtil.toJson(vidList));
                ownerInfo = clubRpcGateway.batchGetMemberByVid(vidList);
                log.info("batchGetMemberByVidFuture resp : {}", GsonUtil.toJson(ownerInfo));
            } catch (Exception e) {
                log.error("batchGetMemberByVidFuture error", e);
            }
            return ObjectUtil.defaultIfNull(ownerInfo, BatchMemberInfoBO.builder().build());
        }, commonThreadPoolExecutor);
    }

    /**
     * 收集投诉数据（解析投诉内容、收集文件ID、员工ID、门店ID等）
     */
    private ComplaintDataCollectionResult collectComplaintData(List<ComplaintOrderInfoGoIn> orderList) {
        Map<String, List<TemplateStructSoIn>> structMap = new HashMap<>();
        List<Long> fileIdFromStruct = new ArrayList<>();
        List<Long> operatorMidList = new ArrayList<>();
        List<String> orgList = new ArrayList<>();
        List<String> complaintNoList = new ArrayList<>();

        for (ComplaintOrderInfoGoIn order : orderList) {
            // 解析投诉内容
            List<TemplateStructSoIn> complaintStructList = parseComplaintContent(order.getComplaintContent());
            structMap.put(order.getComplaintNo(), complaintStructList);

            // 收集文件ID
            fileIdFromStruct.addAll(getFileIdFromStruct(complaintStructList));

            // 收集员工ID
            collectOperatorMids(operatorMidList, order);

            // 收集门店ID和客诉单�?
            orgList.add(order.getOrgId());
            complaintNoList.add(order.getComplaintNo());
        }

        return ComplaintDataCollectionResult.builder()
                .structMap(structMap)
                .fileIdFromStruct(fileIdFromStruct)
                .operatorMidList(operatorMidList)
                .orgList(orgList)
                .complaintNoList(complaintNoList)
                .build();
    }

    /**
     * 解析投诉内容
     */
    private List<TemplateStructSoIn> parseComplaintContent(String complaintContent) {
        if (StringUtils.isBlank(complaintContent)) {
            return new ArrayList<>();
        }
        return GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {}.getType());
    }

    /**
     * 收集操作员ID
     */
    private void collectOperatorMids(List<Long> operatorMidList, ComplaintOrderInfoGoIn order) {
        operatorMidList.add(order.getOperatorMid());
        operatorMidList.add(order.getCustomerServiceMid());
        operatorMidList.add(order.getCreateMid());
    }

    /**
     * 创建批量查询的Future
     */
    private BatchQueryFutures createBatchQueryFutures(ComplaintDataCollectionResult dataCollection,
                                                     List<String> oldComplaintNoList) {
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture =
                getEmployInfoFuture(dataCollection.getOperatorMidList());
        CompletableFuture<List<StoreInfoGoOut>> batchStoreInfoFuture =
                getStoreInfoBatchFuture(dataCollection.getOrgList());
        CompletableFuture<List<FileInfoGoOut>> fileFuture =
                getFileFuture(dataCollection.getFileIdFromStruct(), null);
        CompletableFuture<List<ComplaintTagGoOut>> complaintTagFuture = null;
        // 旧客诉需要查�?
        if (CollectionUtils.isNotEmpty(oldComplaintNoList)) {
            complaintTagFuture =
                    getComplaintTagBatchFuture(oldComplaintNoList);
        }


        return BatchQueryFutures.builder()
                .employInfoFuture(employInfoFuture)
                .batchStoreInfoFuture(batchStoreInfoFuture)
                .fileFuture(fileFuture)
                .complaintTagFuture(complaintTagFuture)
                .build();
    }

    /**
     * 查询客诉标签
     */
    private List<ComplaintTagGoOut> queryComplaintTags(List<String> oldComplaintNoList,
                                                      List<DeliverComplaintBO> deliverRetailComplaintList,
                                                      CompletableFuture<List<ComplaintTagGoOut>> complaintTagFuture) {
        List<ComplaintTagGoOut> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(oldComplaintNoList)) {
            result.addAll(complaintTagFuture.join());
        }
        if (CollectionUtils.isNotEmpty(deliverRetailComplaintList)) {
            result.addAll(buildDeliverComplaintTags(deliverRetailComplaintList));
        }
        return result;
    }

    /**
     * 构建交付客诉标签
     */
    private List<ComplaintTagGoOut> buildDeliverComplaintTags(List<DeliverComplaintBO> deliverComplaintList) {
        List<ComplaintTagGoOut> complaintTagGoOutList = new ArrayList<>();
        for (DeliverComplaintBO deliverComplaintBO : deliverComplaintList) {
            addTimeoutTagIfNeeded(complaintTagGoOutList, deliverComplaintBO);
        }
        return complaintTagGoOutList;
    }

    /**
     * 添加超时标签（如果需要）
     */
    private void addTimeoutTagIfNeeded(List<ComplaintTagGoOut> complaintTagGoOutList,
                                      DeliverComplaintBO deliverComplaintBO) {
        if (Objects.equals(deliverComplaintBO.getFirstResponseTag(), TimeoutOptionEnum.YES.getCode())) {
            ComplaintTagGoOut firstResponseTimeout = ComplaintTagGoOut.builder()
                    .complaintNo(deliverComplaintBO.getDrNo())
                    .tagType(TagTypeEnum.FIRST_RESPONSE_TIMEOUT.getCode())
                    .build();
            complaintTagGoOutList.add(firstResponseTimeout);
        }
        if (Objects.equals(deliverComplaintBO.getFinishTag(), TimeoutOptionEnum.YES.getCode())) {
            ComplaintTagGoOut finishTimeout = ComplaintTagGoOut.builder()
                    .complaintNo(deliverComplaintBO.getDrNo())
                    .tagType(TagTypeEnum.FINISH_TIMEOUT.getCode())
                    .build();
            complaintTagGoOutList.add(finishTimeout);
        }
    }

    /**
     * 组装客诉详情结果
     */
    private ComplaintBatchDetailSoOut assembleComplaintDetails(Map<String, ComplaintOrderInfoGoIn> orderMap,
                                                              Map<String, List<TemplateStructSoIn>> structMap,
                                                              BatchQueryFutures futures,
                                                              List<ComplaintTagGoOut> complaintTagList) {
        // 等待并行查询完成
        List<EmployeeInfoGoOut> employeeInfoList = futures.getEmployInfoFuture().join();
        List<StoreInfoGoOut> storeInfoList = futures.getBatchStoreInfoFuture().join();
        List<FileInfoGoOut> fileInfoList = futures.getFileFuture().join();

        Map<String, StoreInfoGoOut> orgMap = storeInfoList.stream()
                .collect(Collectors.toMap(StoreInfoGoOut::getOrgId, e -> e));

        List<ComplaintDetailSoOut> resultList = new LinkedList<>();
        for (Map.Entry<String, ComplaintOrderInfoGoIn> orderEntry : orderMap.entrySet()) {
            ComplaintDetailSoOut soOut = createComplaintDetailSoOut(orderEntry, structMap,
                    complaintTagList, employeeInfoList, orgMap, fileInfoList);
            resultList.add(soOut);
        }

        ComplaintBatchDetailSoOut soOutResult = new ComplaintBatchDetailSoOut();
        soOutResult.setDetailSoOutList(resultList);
        return soOutResult;
    }

    /**
     * 创建单个客诉详情输出对象
     */
    private ComplaintDetailSoOut createComplaintDetailSoOut(Map.Entry<String, ComplaintOrderInfoGoIn> orderEntry,
                                                           Map<String, List<TemplateStructSoIn>> structMap,
                                                           List<ComplaintTagGoOut> complaintTagList,
                                                           List<EmployeeInfoGoOut> employeeInfoList,
                                                           Map<String, StoreInfoGoOut> orgMap,
                                                           List<FileInfoGoOut> fileInfoList) {
        ComplaintDetailSoOut soOut = new ComplaintDetailSoOut();
        // 填充基本信息
        soOut.fillBaseInfo(orderEntry.getValue());
        // 填充客诉标签
        soOut.fillComplaintTag(complaintTagList, orderEntry.getValue());
        // 填充门店及人员信�?
        soOut.fillStoreUserInfo(orderEntry.getValue(), employeeInfoList,
                orgMap.get(orderEntry.getValue().getOrgId()));
        // 填充客诉信息详情，文件url，考核标签
        soOut.fillDetailInfo(structMap.get(orderEntry.getKey()), fileInfoList);
        return soOut;
    }

}
