package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.wt.car.soc.gw.api.dto.res.MrOrderSimple;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.*;
import com.wt.complaint.manage.api.model.req.ConsultHandlerListReq;
import com.wt.complaint.manage.api.model.req.consult.PadConsultListReq;
import com.wt.complaint.manage.api.model.resp.ComplaintHandlerInfo;
import com.wt.complaint.manage.api.model.resp.ConsultHandlerListResp;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.api.model.resp.consult.ConsultSelectorResp;
import com.wt.complaint.manage.api.model.resp.consult.SelectorItem;
import com.wt.complaint.manage.domain.api.enums.ConsultOrderStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintRelationOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserConsultOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.AttachmentGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.rpc.UpcConfigGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintRelationOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.UserConsultViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintFrameInfoSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintProcessSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultListSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.ConsultStatisticsSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.*;
import com.wt.complaint.manage.domain.constant.ComplaintActionConst;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.UserActionAuthContext;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigLocalCache;
import com.wt.complaint.manage.domain.manager.componment.UpcConfigParser;
import com.wt.complaint.manage.domain.model.ConsultStatusCountInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.utils.ComplaintConsultUtil;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 咨询单视图服务实现类
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class UserConsultViewServiceImpl implements UserConsultViewService {

    private static final String VIN_MASK_PREFIX = "*******";
    private static final int VIN_LAST_DIGITS = 6;

    @Resource
    private UserConsultOrderGateway userConsultOrderGateway;

    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;


    @Resource
    private UserAuthManager userAuthManager;


    @Resource
    private CarRemoteGateway carRemoteGateway;


    @Resource
    private CarUserRemoteGateway carUserRemoteGateway;

    @Resource
    private ClubRpcGateway clubRpcGateway;


    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;


    @Resource
    UpcConfigParser upcConfigParser;


    @Resource
    UpcConfigLocalCache localCache;


    @Resource
    private FileRemoteGateway fileRemoteGateway;


    @Resource
    private ComplaintRelationOrderRepositoryGateway complaintRelationOrderRepositoryGateway;


    @Resource
    private MrOrderGateway mrOrderGateway;

    /**
     * 获取咨询单枚举下拉列�?
     */
    @Override
    public ConsultSelectorResp getConsultSelectorList() {
        ConsultSelectorResp resp = new ConsultSelectorResp();
        resp.setConsultStatusEnum(Arrays.stream(ConsultOrderStatusEnum.values())
                .map(e -> buildSelectorItem(e.getCode(), e.getDesc()))
                .collect(Collectors.toList()));
        resp.setConsultTypeEnum(Arrays.stream(ConsultTypeEnum.values())
                .map(e -> buildSelectorItem(e.getCode(), e.getDesc()))
                .collect(Collectors.toList()));
        resp.setUrgencyLevelEnum(Arrays.stream(UrgencyLevelEnum.values())
                .map(e -> buildSelectorItem(e.getCode(), e.getDesc()))
                .collect(Collectors.toList()));
        resp.setHandleResultEnum(Arrays.stream(HandleResultEnum.values())
                .map(e -> buildSelectorItem(e.getCode(), e.getDesc()))
                .collect(Collectors.toList()));
        return resp;
    }

    /**
     * 查询咨询单统计项
     */
    @Override
    public ConsultStatisticsSoOut queryStatisticsItems(ConsultStatisticsSoIn soIn) {

        List<ConsultStatusCountInfo> consultStatusCountInfos =
                userConsultOrderGateway.countConsultStatistics(soIn.getOrgId(),soIn.getMid());

        if(CollUtil.isEmpty(consultStatusCountInfos)) {
            return null;
        }

        ConsultStatisticsSoOut t = new ConsultStatisticsSoOut();
        consultStatusCountInfos.forEach(c->{
            if(Objects.equals(c.getOrderStatus(), ConsultOrderStatusEnum.WAIT_RECEIVE.getCode())){
                t.setPendingReceiveCount(c.getCnt());
            }
            if(Objects.equals(c.getOrderStatus(), ConsultOrderStatusEnum.WAIT_FIRST_RESPONSE.getCode())){
                t.setPendingFirstResponseCount(c.getCnt());
            }
            if(Objects.equals(c.getOrderStatus(), ConsultOrderStatusEnum.WAIT_CLOSE.getCode())){
                t.setPendingCloseCount(c.getCnt());
            }
            if(Objects.equals(c.getOrderStatus(), ConsultOrderStatusEnum.COMPLETED.getCode())){
                t.setCompletedCount(c.getCnt());
            }
        });
        return t;
    }

     /**
     * 分页查询咨询单列表（PAD端：vin脱敏�?
     */
    @Override
    public ConsultListSoOut queryConsultList(ConsultListSoIn soIn) {
        return buildConsultList(soIn, false);
    }

    /**
     * 查询咨询单详情（PAD端）
     */
    @Override
    public ConsultDetailSoOut queryConsultDetail(ConsultDetailSoIn soIn) {
        return buildConsultDetail(soIn);
    }

    /**
     * 分页查询咨询单列表（Web端：带门店信息，vin不脱敏）
     */
    @Override
    public ConsultListSoOut queryWebConsultList(ConsultListSoIn soIn) {
        return buildConsultList(soIn, true);
    }

    /**
     * 查询咨询单详情（Web端）
     */
    @Override
    public ConsultDetailSoOut queryWebConsultDetail(ConsultDetailSoIn soIn) {
        return buildConsultDetail(soIn);
    }

    @Override
    public ConsultHandlerListResp getConsultHandler(ConsultHandlerListReq req) {
        ConsultHandlerListResp soOut = new ConsultHandlerListResp();
        List<ComplaintHandlerInfo> handlerList = new ArrayList<>();
        // 获取员工信息及咨询单单信�?
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoByStoreFuture = getEmployInfoByStoreFuture(Arrays.asList(
                CarEmployeeEnum.RECEIVER_MANAGER.getCode(),
                CarEmployeeEnum.RECEIVER.getCode()
        ), req.getOrgId());
        CompletableFuture<List<UserConsultOrderInfo>> orderListFuture = getOrderListFuture(req.getOrgId(), ConsultStatusEnum.getUnfinishedStatus());

        // 获取数据
        List<EmployeeInfoGoOut> employeeInfoGoOuts = employInfoByStoreFuture.join();
        List<UserConsultOrderInfo> orderInfoList = orderListFuture.join();
        employeeInfoGoOuts = employeeInfoGoOuts.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmployeeInfoGoOut::getMiId))), ArrayList::new));
        Map<Long, List<UserConsultOrderInfo>> orderMap = orderInfoList.stream().collect(Collectors.groupingBy(e -> e.getOperatorMid()));
        employeeInfoGoOuts.stream().forEach(e->{
            handlerList.add(ComplaintHandlerInfo.builder()
                    .mid(e.getMiId().toString())
                    .name(e.getName())
                    .notFinishedCnt(orderMap.getOrDefault(e.getMiId(), new LinkedList<>()).size())
                    .build());
        });
        soOut.setHandlerList(handlerList);
        return soOut;
    }

    @Override
    public ConsultListSoOut queryPadConsultList(PadConsultListReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        ConsultListGoIn goIn = buildPadConsultListGoIn(req, pageNum, pageSize);
        long total =  userConsultOrderGateway.countPadConsultPage(goIn);
        List<UserConsultOrderInfo> orderInfoList = userConsultOrderGateway.pagePadConsultOrders(goIn);
        ConsultListSoOut consultListSoOut = getConsultListSoOut(orderInfoList, total, pageNum, pageSize);
        if (!CollUtil.isEmpty(consultListSoOut.getDataList())) {
            //根据咨询单状态设置展示vin的效果（全展示还是部分展示）
            Map<String, String> vidToVinMap = genVidToVinMap(consultListSoOut.getDataList().stream()
                    .map(ConsultListItemSoOut::getVid)
                    .distinct()
                    .collect(Collectors.toList()));
            consultListSoOut.getDataList().forEach(c-> {
                c.setVin(maskVidForList(vidToVinMap.get(c.getVid()),c.getConsultStatus()));
                if(c.getConsultStatus() == ConsultOrderStatusEnum.COMPLETED.getCode()){
                    CompletableFuture<CarUserAggGoOut> carUserFuture = getCarUserFuture(c.getVid());
                    CarUserAggGoOut carUserAgg = carUserFuture.join();
                    c.setCarNo(carUserAgg.getCarNo());
                }
                c.setVid("");
            });
        }

        return consultListSoOut;
    }

    @Override
    public ComplaintFrameInfoSoOut getComplaintAuth(ComplaintFrameInfoSoIn param) {
        String roleStr = RpcContext.getContext().getAttachment("$curr_role");
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        log.info("getComplaintAuth, param: {},roleStr: {},miID: {}", param, roleStr, miID);
        if (StringUtils.isBlank(roleStr)) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "没获取到用户当前登录角色");
        }
        if (StringUtils.isBlank(miID)) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "用户未登�?);
        }
        ComplaintFrameInfoSoOut soOut = new ComplaintFrameInfoSoOut();
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        goIn.setConsultNo(param.getComplaintNo());
        UserConsultOrderInfo userConsultOrderInfo = userConsultOrderGateway.searchUserConsultOrderInfo(goIn);
        if (userConsultOrderInfo == null) {
            log.info("未找到咨询单信息,consultNo={}", param.getComplaintNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }

        List<String> roleKeyList = upcConfigParser.getRoleList(UpcConfigGoIn.builder()
                .moduleKey("complaintFrame")
                .orgId(userConsultOrderInfo.getOrgId())
                .mid(miID)
                .currRole(roleStr)
                .build());
        Map<String, List<String>> upcConfigMap = localCache.getUpcConfigMap();
        Set<String> resourceTags = new HashSet<>();


        // 3. 服务顾问 接单之后只能 处理自己的工单�?
        // 特殊处理: 服务顾问
        if (roleKeyList.contains(ProretailRoleEnum.CAR_SERVICE_REPRESENTATIVE.getKey())
                && !(Objects.equals(userConsultOrderInfo.getOperatorMid(), Long.valueOf(miID)))) {
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

        UserActionAuthContext context = new UserActionAuthContext();
        context.setRole(roleStr);
        context.setLoginMid(Long.valueOf(miID));
        context.setComplaintNo(param.getComplaintNo());
        context.setHandlerMid(userConsultOrderInfo.getOperatorMid());
        context.setOrgId(userConsultOrderInfo.getOrgId());
        context.setStatus(userConsultOrderInfo.getOrderStatus());
        List<String> buttons = upcConfigParser.calcButtons(new ArrayList<>(resourceTags), context, userConsultOrderInfo);

        UserActionAuth userAuth = new UserActionAuth();
        userAuth.setActionsList(buttons);
        userAuth.setButtons(buttons);
        soOut.setUserActionAuth(userAuth);
        return soOut;
    }

    // --------- private helpers ---------

    /**
     * 构建下拉选项
     */
    private SelectorItem buildSelectorItem(Integer key, String value) {
        SelectorItem item = new SelectorItem();
        item.setKey(key);
        item.setValue(value);
        return item;
    }

    /**
     * 构建咨询单分页列表（includeOrgInfo=true 时附带门店信息，vin 不脱敏）
     */
    private ConsultListSoOut buildConsultList(ConsultListSoIn soIn, boolean includeOrgInfo) {
        int pageNum = soIn.getPageNum() == null ? 1 : soIn.getPageNum();
        int pageSize = soIn.getPageSize() == null ? 10 : soIn.getPageSize();

        ConsultListGoIn goIn = buildConsultListGoIn(soIn, pageNum, pageSize);
        long total =  userConsultOrderGateway.countWebConsultPage(goIn);
        List<UserConsultOrderInfo> orderInfoList =userConsultOrderGateway.pageWebConsultOrders(goIn);
        ConsultListSoOut consultListSoOut = getConsultListSoOut(orderInfoList, total, pageNum, pageSize);
        if (!CollUtil.isEmpty(consultListSoOut.getDataList())) {
            consultListSoOut.getDataList().forEach(c->{
                c.setVid("");
            });
        }
        return consultListSoOut;

    }


    private ConsultListSoOut getConsultListSoOut(List<UserConsultOrderInfo> orderInfoList, long total, int pageNum, int pageSize) {
        Map<Long, CarEmployee> employeeMap = queryCreatorEmployeeMap(orderInfoList);
        Map<String, String> orgNameMap =   queryOrgNameMap(orderInfoList);
        Map<String, String> mrNoMap = new HashMap<>();
        Map<String, String> stNoMap = new HashMap<>();
        ComplaintRelationOrderListGoIn relationQuery = ComplaintRelationOrderListGoIn.builder()
                .complaintNoList(orderInfoList.stream().map(UserConsultOrderInfo::getConsultNo).collect(Collectors.toList()))
                .build();

        List<ComplaintRelationOrderGoOut> clist = complaintRelationOrderRepositoryGateway.findList(relationQuery);

        log.info("查询出的关联的内容为:req:{},res:{}",GsonUtil.toJson(relationQuery),GsonUtil.toJson(clist));
        if (!CollUtil.isEmpty(clist)){
            stNoMap = clist.stream().filter(c-> c.getBizType() == 2)
                    .collect(Collectors.toMap(
                            ComplaintRelationOrderGoOut::getComplaintNo,
                            ComplaintRelationOrderGoOut::getBizNo,
                            (old, newVal) -> newVal
                    ));
            List<MrOrderSimple> relationList =   mrOrderGateway.getSimpleMrOrderInfo(clist.stream().filter(c-> c.getBizType() == 2).map(ComplaintRelationOrderGoOut::getBizNo)
                    .collect(Collectors.toList()));
            log.info("查询出的关联的维保信息为::{}",GsonUtil.toJson(relationList));
            if(CollUtil.isNotEmpty(relationList)){
                relationList.forEach(r-> {
                    mrNoMap.put(r.getSuperTicketNo(),r.getMrNo());
                });
            }
        }

        Map<String, String> finalStNoMap = stNoMap;
        List<ConsultListItemSoOut> dataList = orderInfoList.stream()
                .map(info -> convertToListItem(mrNoMap, finalStNoMap,info, employeeMap, orgNameMap))
                .collect(Collectors.toList());
        ConsultListSoOut soOut = new ConsultListSoOut();
        soOut.setTotal(total);
        soOut.setPageNum(pageNum);
        soOut.setPageSize(pageSize);
        soOut.setDataList(dataList);
        return soOut;
    }

    /**
     * 构建咨询单列表网关查询入�?
     */
    private ConsultListGoIn buildConsultListGoIn(ConsultListSoIn req, int pageNum, int pageSize) {
        int pageOffset = (pageNum - 1) * pageSize;
        return ConsultListGoIn.builder()
                .consultNo(req.getConsultNo())
                .orgIdList(StringUtils.isEmpty(req.getOrgId())?Collections.emptyList():Collections.singletonList(req.getOrgId()))
                .consultStatus(req.getConsultStatus())
                .key(req.getKey())
                .pageOffset(pageOffset)
                .pageSize(pageSize)
                .consultType(req.getConsultType())
                .vin(req.getVin())
                .handleResult(req.getHandleResult())
                .urgencyLevel(req.getUrgencyLevel())
                .createTimeStart(req.getCreateTimeStart())
                .createTimeEnd(req.getCreateTimeEnd())
                .finishTimeStart(req.getFinishTimeStart())
                .finishTimeEnd(req.getFinishTimeEnd())
                .build();
    }
    private ConsultListGoIn buildPadConsultListGoIn(PadConsultListReq soIn, int pageNum, int pageSize) {

        int pageOffset = (pageNum - 1) * pageSize;
        return ConsultListGoIn.builder()
                .orgIdList(Collections.singletonList(soIn.getOrgId()))
                .consultStatus(soIn.getConsultStatus())
                .key(soIn.getKey())
                .pageOffset(pageOffset)
                .pageSize(pageSize)
                .operatorMid(soIn.getMid())
                .build();
    }

    /**
     * 批量查询创建人信息，返回 mid -> CarEmployee 映射
     */
    private Map<Long, CarEmployee> queryCreatorEmployeeMap(List<UserConsultOrderInfo> orderInfoList) {
        List<Long> midList = orderInfoList.stream()
                .filter(o -> o.getCreateMid() != null && o.getCreateMid() != 0)
                .map(UserConsultOrderInfo::getCreateMid)
                .distinct()
                .collect(Collectors.toList());
        return CollUtil.isEmpty(midList)
                ? Collections.emptyMap()
                : carEmployeeRemoteGateway.queryCarEmployee(midList);
    }

    /**
     * 批量查询门店名称，返�?orgId -> orgName 映射（仅 webList 调用�?
     */
    private Map<String, String> queryOrgNameMap(List<UserConsultOrderInfo> orderInfoList) {
        if (CollUtil.isEmpty(orderInfoList)) {
            return Collections.emptyMap();
        }
        List<String> distinctOrgIds = orderInfoList.stream()
                .map(UserConsultOrderInfo::getOrgId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(distinctOrgIds)) {
            return Collections.emptyMap();
        }
        try {
            return storeRemoteGateway.getStoreNameMap(distinctOrgIds);
        } catch (Exception e) {
            log.warn("queryOrgNameMap getStoreNameMap error", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 将单条咨询单记录转换为列表出�?
     */
    private ConsultListItemSoOut convertToListItem(Map<String, String> mrNoMap,Map<String, String> stNoMap,UserConsultOrderInfo info,
                                                   Map<Long, CarEmployee> employeeMap,
                                                   Map<String, String> orgNameMap) {


        ConsultListItemSoOut item = new ConsultListItemSoOut();
        item.setConsultNo(info.getConsultNo());
        item.setCreateTime(info.getCreateTime());
        item.setConsultType(ConsultTypeEnum.getDescByCode(
                info.getConsultType() != null ? info.getConsultType() : null));
        item.setVid(info.getVid());
        item.setCarVersion(info.getCarType());
        item.setExpectedCallbackTime(info.getExpectingBackTime());
        CarEmployee employee = employeeMap.get(info.getCreateMid());
        item.setCreator(employee != null ? employee.getName() : null);
        item.setRemindFlag(info.getReminderTimes() != null && info.getReminderTimes() > 0 ? 1 : 0);
        item.setCarNo(info.getCarNo());
        item.setHandleResult(info.getHandleResult());
        item.setHandleResultDesc(HandleResultEnum.getDescByCode(info.getHandleResult()));
        item.setUrgentFlag(info.getPriority() >= 8?1:0);
        item.setPriority(info.getPriority());
        item.setOrgId(info.getOrgId());
        item.setOrgName(orgNameMap.get(info.getOrgId()));
        item.setConsultStatus(info.getOrderStatus());
        item.setMrNo(mrNoMap.get(stNoMap.get(info.getConsultNo())));
        item.setStNo(stNoMap.get(info.getConsultNo()));
        return item;
    }

    /**
     * 构建咨询单详情（PAD端和Web端共用）
     */
    private ConsultDetailSoOut buildConsultDetail(ConsultDetailSoIn soIn) {
        String roleStr = RpcContext.getContext().getAttachment("$curr_role");
        String miID = RpcContext.getContext().getAttachment("$upc_miID");
        log.info("buildConsultDetail, miID={},roleStr={}", miID, roleStr);
        if (StringUtils.isBlank(soIn.getConsultNo())) {
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "consultNo不能为空");
        }

        // 查询咨询单信�?
        UserConsultOrderInfo orderInfo = userConsultOrderGateway.searchUserConsultOrderInfo(
                buildGoIn(soIn.getConsultNo()));
        if (orderInfo == null) {
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "咨询单不存在");
        }

        // 查询创建人姓名和处理人姓�?
        List<Long> midList = new ArrayList<>();
        if (orderInfo.getCreateMid() != null && orderInfo.getCreateMid() != 0) {
            midList.add(orderInfo.getCreateMid());
        }
        if (orderInfo.getOperatorMid() != null && orderInfo.getOperatorMid() != 0) {
            midList.add(orderInfo.getOperatorMid());
        }
        Map<Long, CarEmployee> employeeMap = CollUtil.isEmpty(midList)
                ? Collections.emptyMap()
                : carEmployeeRemoteGateway.queryCarEmployee(midList);

        // 查询门店名称
        String followStoreName = null;
        if (StringUtils.isNotBlank(orderInfo.getOrgId())) {
            try {
                Map<String, String> storeNameMap = storeRemoteGateway.getStoreNameMap(
                        Collections.singletonList(orderInfo.getOrgId()));
                followStoreName = storeNameMap.get(orderInfo.getOrgId());
            } catch (Exception e) {
                log.warn("buildConsultDetail getStoreNameMap error, orgId={}", orderInfo.getOrgId(), e);
            }
        }

        CarEmployee creator = employeeMap.get(orderInfo.getCreateMid());
        CarEmployee follower = employeeMap.get(orderInfo.getOperatorMid());
        ConsultDetailSoOut consultDetailSoOut = ComplaintConsultUtil.buildConsultDetailSoOut(orderInfo, creator, followStoreName, follower);

        // 填充用户按钮权限
        if(StringUtils.isNotBlank(roleStr) && StringUtils.isNotBlank(miID)){
            consultDetailSoOut.constructActionList(roleStr, Long.valueOf(miID), userAuthManager, orderInfo);
        }
        //判断是不是跟进人，特殊处�?
        if(StringUtils.isNotBlank(miID) && Long.valueOf(miID).equals(orderInfo.getOperatorMid())){
            if(ConsultStatusEnum.FINISH_PENDING.getCode().equals(orderInfo.getOrderStatus())){
                if (!consultDetailSoOut.getUserActionAuth().getButtons().contains("applyFinish")) {
                    consultDetailSoOut.getUserActionAuth().getButtons().add("applyFinish");
                }
                if (!consultDetailSoOut.getUserActionAuth().getActionsList().contains("applyFinish")) {
                    consultDetailSoOut.getUserActionAuth().getActionsList().add("applyFinish");
                }
            }

            if(ConsultStatusEnum.FIRST_RESPONSE_PENDING.getCode().equals(orderInfo.getOrderStatus())){
                if (!consultDetailSoOut.getUserActionAuth().getButtons().contains(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS)) {
                    consultDetailSoOut.getUserActionAuth().getButtons().add(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS);
                }
                if (!consultDetailSoOut.getUserActionAuth().getButtons().contains(ComplaintActionConst.APPOINTMENT_MR_ORDER)) {
                    consultDetailSoOut.getUserActionAuth().getButtons().add(ComplaintActionConst.APPOINTMENT_MR_ORDER);
                }
                if (!consultDetailSoOut.getUserActionAuth().getActionsList().contains(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS)) {
                    consultDetailSoOut.getUserActionAuth().getActionsList().add(ComplaintActionConst.ADD_FOLLOW_UP_RECORDS);
                }
                if (!consultDetailSoOut.getUserActionAuth().getActionsList().contains(ComplaintActionConst.APPOINTMENT_MR_ORDER)) {
                    consultDetailSoOut.getUserActionAuth().getActionsList().add(ComplaintActionConst.APPOINTMENT_MR_ORDER);
                }
            }

        }
        //填充咨询单信�?
        this.fillConsultOrderInfo(consultDetailSoOut, orderInfo,creator,followStoreName,follower);
        // 查询�?
        CompletableFuture<List<CarInfoGoOut>> carFuture = getCarFuture(Collections.singletonList(orderInfo.getVid()));
        List<CarInfoGoOut> carInfoGoOutList = carFuture.join();
        // 查询车主
        CompletableFuture<CarUserAggGoOut> carUserFuture = getCarUserFuture(orderInfo.getVid());
        CarUserAggGoOut carUserAgg = carUserFuture.join();
        //设置车牌�?
        consultDetailSoOut.setCarNo(carUserAgg.getCarNo());
        // 查询车辆动态信�?
        CompletableFuture<GetDynamicInfoResponseGoOut> carDynamicFuture = getDynamicInfoFuture(Collections.singletonList(orderInfo.getVid()));
        GetDynamicInfoResponseGoOut carDynamicInfo = carDynamicFuture.join();
        // 查询vip信息
        CompletableFuture<BatchMemberInfoBO> batchMemberInfoFuture = batchGetMemberByVidFuture(Collections.singletonList(orderInfo.getVid()));
        BatchMemberInfoBO memberInfoBO = batchMemberInfoFuture.join();
        // 填充车辆信息
        consultDetailSoOut.setVid(orderInfo.getVid());
        consultDetailSoOut.fillCarInfo(carInfoGoOutList, carUserAgg, carDynamicInfo, memberInfoBO);
        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture = getProcessLogFuture(orderInfo.getConsultNo());
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        // 填充状态bar
        consultDetailSoOut.constructStatusBar(followProcessGoOuts, orderInfo);
        //填充附件
        this.fillAttachments(consultDetailSoOut, followProcessGoOuts);
        // 查询tab信息
        List<ConsultDetailTabEnum> detailTabByStatus = getDetailTabByStatus(orderInfo);
        // 填充tab
        consultDetailSoOut.fillDetailTab(detailTabByStatus, followProcessGoOuts);
        //填充作业单信�?
        this.fillWorkOrder(orderInfo, consultDetailSoOut);
        //填充结案信息
        this.fillCompleteInfo(orderInfo, consultDetailSoOut, followProcessGoOuts);
        return consultDetailSoOut;
    }

    private void fillConsultOrderInfo(ConsultDetailSoOut consultDetailSoOut, UserConsultOrderInfo orderInfo,CarEmployee creator,String followStoreName,CarEmployee follower) {
        ConsultDetailSoOut.ConsultOrderInfo consultOrderInfo = new ConsultDetailSoOut.ConsultOrderInfo();
        consultOrderInfo.setConsultNo(orderInfo.getConsultNo());
        consultOrderInfo.setCreator(creator != null ? creator.getName() : null);
        consultOrderInfo.setCreateTime(DateUtil.getTimeStrByDate(orderInfo.getCreateTime()));
        consultOrderInfo.setFollowStore(followStoreName);
        consultOrderInfo.setFollower(follower != null ? follower.getName() : null);
        consultDetailSoOut.setConsultOrderInfo(consultOrderInfo);
    }

    private void fillAttachments(ConsultDetailSoOut consultDetailSoOut, List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        Map<Long, FileInfoGoOut> editAttachments = getProcessRecordsAttachments(followProcessGoOuts.stream().filter(process -> ProcessTypeEnum.ZX_INFO_UPDATE.getProcessCode().equals(process.getProcessType())).collect(Collectors.toList()));
        Map<Long, FileInfoGoOut> createAttachments = getProcessRecordsAttachments(followProcessGoOuts.stream().filter(process -> ProcessTypeEnum.CREATE_ORDER.getProcessCode().equals(process.getProcessType())).collect(Collectors.toList()));
        if(CollUtil.isNotEmpty(editAttachments)){
            consultDetailSoOut.setAttachmentList(editAttachments.values().stream().map(
                    attachmentGoOut -> new Attachment(
                            attachmentGoOut.getFileId(),
                            attachmentGoOut.getFileName(),
                            attachmentGoOut.getFileUrl(),
                            null,
                            null
                    )
            ).collect(Collectors.toList()));
        }else if(CollUtil.isNotEmpty(createAttachments)){
            consultDetailSoOut.setAttachmentList(createAttachments.values().stream().map(
                    attachmentGoOut -> new Attachment(
                            attachmentGoOut.getFileId(),
                            attachmentGoOut.getFileName(),
                            attachmentGoOut.getFileUrl(),
                            null,
                            null
                    )
            ).collect(Collectors.toList()));
        }
    }

    private void fillWorkOrder(UserConsultOrderInfo orderInfo, ConsultDetailSoOut consultDetailSoOut) {
        consultDetailSoOut.setPriority(orderInfo.getPriority());
        consultDetailSoOut.setOrgId(orderInfo.getOrgId());
        consultDetailSoOut.setSoNo(orderInfo.getSoNo());
        consultDetailSoOut.setCreateMid(orderInfo.getCreateMid());
        consultDetailSoOut.setConsultTypeName(ConsultTypeEnum.getDescByCode(orderInfo.getConsultType()));
        consultDetailSoOut.setCallbackTime(DateUtil.isDefaultTime(orderInfo.getExpectingBackTime()) ? null : DateUtil.getTimeStrByDate(orderInfo.getExpectingBackTime()));
        consultDetailSoOut.setConsultStatus(orderInfo.getOrderStatus());
        //填充关联维保单号
        ComplaintRelationOrderListGoIn goIn = new ComplaintRelationOrderListGoIn();
        goIn.setComplaintNoList(Collections.singletonList(orderInfo.getConsultNo()));
        List<ComplaintRelationOrderGoOut> relationList = complaintRelationOrderRepositoryGateway.findList(goIn);
        if(CollUtil.isNotEmpty(relationList)){
            ComplaintRelationOrderGoOut relation = relationList.stream().filter(r -> r.getBizType() == 2).findFirst().orElse(null);
            if(relation != null && StringUtils.isNotBlank(relation.getBizNo())){
                consultDetailSoOut.setMrSuperTicketNo(relation.getBizNo());
                List<MrOrderSimple> mrOrderSimples = mrOrderGateway.getSimpleMrOrderInfo(Collections.singletonList(relation.getBizNo()));
                if(CollUtil.isNotEmpty(mrOrderSimples)){
                    consultDetailSoOut.setMrNo(mrOrderSimples.get(0).getMrNo());
                }
            }
        }
        boolean hasLinkedOrder = StringUtils.isNotBlank(consultDetailSoOut.getMrSuperTicketNo());
        consultDetailSoOut.setIsLinkedMrOrder(hasLinkedOrder ? "1" : "0");
    }

    private void fillCompleteInfo(UserConsultOrderInfo orderInfo,ConsultDetailSoOut consultDetailSoOut, List<ComplaintFollowProcessGoOut> followProcessGoOuts) {
        if(orderInfo.getOrderStatus() == 4){
            ComplaintFollowProcessGoOut finishProcess = null;
            if(CollUtil.isNotEmpty(followProcessGoOuts)){
                finishProcess = followProcessGoOuts.stream()
                        .filter(process -> ProcessTypeEnum.ZX_FINISH.getProcessCode().equals(process.getProcessType()))
                        .findFirst()
                        .orElse(null);
            }
            if(finishProcess != null){
                String processContent = finishProcess.getProcessContent();
                //将processContent转成RecordInfoGoIn
                RecordInfoGoIn recordInfoGoIn = GsonUtil.fromJson(processContent, RecordInfoGoIn.class);
                consultDetailSoOut.setCompleteInfo(ConsultDetailSoOut.CompleteInfo.builder()
                        .completeTime(recordInfoGoIn.getOperateTime())
                        .completeUser(recordInfoGoIn.getOperateMid())
                        .completeUserName(recordInfoGoIn.getOperateName())
                        .completeResult(HandleResultEnum.getDescByCode(recordInfoGoIn.getHandleType()))
                        .solution(recordInfoGoIn.getFinishDesc())
                        .build());
            }
        }
    }


    private List<ConsultDetailTabEnum> getDetailTabByStatus(UserConsultOrderInfo orderInfo) {
        List<ConsultDetailTabEnum> detailTabEnums = ConsultDetailTabEnum.listTab(0, orderInfo.getOrderStatus());
        return detailTabEnums;
    }


    private CompletableFuture<List<ComplaintFollowProcessGoOut>> getProcessLogFuture(String consultNo) {
        return CompletableFuture.supplyAsync(() -> processRepositoryGateway.getProcessListByNo(consultNo), commonThreadPoolExecutor);
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



    private CompletableFuture<GetDynamicInfoResponseGoOut> getDynamicInfoFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getDynamicInfo(vidList), commonThreadPoolExecutor);
    }


    private CompletableFuture<CarUserAggGoOut> getCarUserFuture(String vid) {
        return CompletableFuture.supplyAsync(() -> carUserRemoteGateway.userAggQuery(CarUserAggGoIn.builder().vid(vid).build()), commonThreadPoolExecutor);
    }


    private CompletableFuture<List<CarInfoGoOut>> getCarFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getCarSimpleInfo(vidList, null), commonThreadPoolExecutor);
    }


    /**
     * PAD端列表VIn脱敏：已完成状态只显示�?位，其余全显
     */
    private String maskVidForList(String vin, Integer orderStatus) {
        if (StringUtils.isBlank(vin)) {
            return vin;
        }
        if (orderStatus != null
                && orderStatus.intValue() == ConsultOrderStatusEnum.COMPLETED.getCode()) {
            // 已完成：脱敏后六�?
            int len = vin.length();
            if (len <= VIN_LAST_DIGITS) {
                return vin;
            }
            return VIN_MASK_PREFIX + vin.substring(len - VIN_LAST_DIGITS);
        }
        return vin;
    }

    /**
     * 构建咨询单查询入�?
     */
    private UcConsultOrderGoIn buildGoIn(String consultNo) {
        UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
        goIn.setConsultNo(consultNo);
        return goIn;
    }
    private CompletableFuture<List<EmployeeInfoGoOut>> getEmployInfoByStoreFuture(List<Integer> positionIdList, String orgId) {
        return CompletableFuture.supplyAsync(() -> eiamRemoteGateway.queryEmployeeByStore(StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build()), commonThreadPoolExecutor);
    }
    private CompletableFuture<List<UserConsultOrderInfo>> getOrderListFuture(String orgId, List<Integer> statusList) {
        return CompletableFuture.supplyAsync(() -> userConsultOrderGateway.findList(ConsultListGoIn.builder().orgIdList(Collections.singletonList(orgId)).consultStatusList(statusList).build()), commonThreadPoolExecutor);
    }

    @Override
    public ComplaintProcessListSoOut getComplaintProcessRecords(ComplaintProcessSoIn soIn) {
        ComplaintProcessListSoOut soOut = new ComplaintProcessListSoOut();
        UserConsultOrderInfo orderInfo = null;
        if(StringUtils.isNotBlank(soIn.getConsultSuperTicketNo())) {
            UcConsultOrderGoIn goIn = new UcConsultOrderGoIn();
            goIn.setSuperTicketNo(soIn.getConsultSuperTicketNo());
            orderInfo = userConsultOrderGateway.searchUserConsultOrderInfo(goIn);
        }else{
           orderInfo = userConsultOrderGateway.searchUserConsultOrderInfo(
                    buildGoIn(soIn.getConsultNo()));
        }
        if (StringUtils.isBlank(orderInfo.getConsultNo())) {
            log.info("未找到咨询单信息,consultNo={}", soIn.getConsultNo());
            throw new BusinessException(ErrorCodeEnums.COMPLAINT_ORDER_NOT_FOUND);
        }

        // 查询跟进记录信息
        CompletableFuture<List<ComplaintFollowProcessGoOut>> processLogFuture = getProcessLogFuture(orderInfo.getConsultNo());
        List<ComplaintFollowProcessGoOut> followProcessGoOuts = processLogFuture.join();
        //零售通或者售后工作台ProcessTypeEnum.CREATE_ORDER和ProcessTypeEnum.COMPLAINT_INFO_UPDATE不需要展�?
        if("PAD_DETAIL".equals(soIn.getSource()) || "AFTER_SALE_WORKBENCH".equals(soIn.getSource())) {
            followProcessGoOuts = followProcessGoOuts.stream().filter(e -> !ProcessTypeEnum.CREATE_ORDER.getProcessCode().equals(e.getProcessType()) && !ProcessTypeEnum.ZX_INFO_UPDATE.getProcessCode().equals(e.getProcessType())).collect(Collectors.toList());
        }

        Map<Long, FileInfoGoOut> processRecordsAttachments = getProcessRecordsAttachments(followProcessGoOuts);
        soOut.fillProcessList(followProcessGoOuts, processRecordsAttachments);
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
                    //ID需要判空，避免空指针异�?
                    List<Long> tempFileIdUrl = recordInfoGoIn.getAttachments().stream().map(AttachmentGoIn::getId).filter(Objects::nonNull).collect(Collectors.toList());
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

    private Map<String, String> genVidToVinMap(List<String> vidList) {
        List<CarInfoGoOut> carInfoGoOuts = carRemoteGateway.getCarSimpleInfo(vidList, null);
        Map<String, String> vidToVinMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(carInfoGoOuts)) {
            vidToVinMap = carInfoGoOuts
                    .stream()
                    .collect(Collectors.toMap(CarInfoGoOut::getVid, CarInfoGoOut::getVin,
                            (o1, o2) -> o1));
        }
        return vidToVinMap;
    }
}
