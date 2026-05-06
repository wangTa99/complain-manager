package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.collection.CollUtil.isEmpty;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.com.google.common.reflect.TypeToken;
import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.api.model.resp.ZoneDTO;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintListSearchInfo;
import com.wt.complaint.manage.api.model.resp.retail.RetailUserActionAuth;
import com.wt.complaint.manage.domain.api.enums.CarChannelTypeEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PermissionTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.enums.RetailTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintExpandGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClueGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateFieldSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailHasFirstResponseRecordFlagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.AttachmentSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.DetailFieldSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.TemplateStructSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetaiSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintDetailFrameSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.RetailHasFirstResposeRecordFlagSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.StaticTabCountSoOut;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.BubbleCountSoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintViewService;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailAuthSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintDetailSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.RetailComplaintListSearchSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.retail.StaticRetailCountSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.CarEmployeeInfoSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.constant.RetailActionConst;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeePrivilegeStateEnum;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.RetailAuthManager;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.domain.utils.ParseComplaintContentUtil;
import com.wt.maindatacommon.enums.ZoneScopeEnums;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 零售投诉视图服务
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Slf4j
@Service
public class RetailComplaintViewServiceImpl implements RetailComplaintViewService {

    @Autowired
    private RetailComplaintGateway retailComplaintGateway;

    @Autowired
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Autowired
    private StoreRemoteGateway storeRemoteGateway;

    @Autowired
    private RetailAuthManager retailAuthManager;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private ComplaintFollowProcessRepositoryGateway processRepositoryGateway;

    @Autowired
    private ClueGateway clueGateway;

    @Autowired
    private DeliverComplaintExpandGateway deliverComplaintExpandGateway;

    @Resource
    private MoneThreadPoolExecutor commonThreadPoolExecutor;

    private static final String DR_NO_PREFIX = "RC";

    /**
     * 根据mid获取员工信息
     *
     * @param mid 员工id
     * @return 员工职位
     */
    public CarEmployeeInfoSoOut getEmployeeInfoByMid(String mid) {
        if (StrUtil.isBlank(mid)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "mid为空");
        }
        //获取汽车员工信息和岗位信�?
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(Long.valueOf(mid), CarChannelTypeEnum.CAR_SALE.getCode());
        log.info("RetailComplaintViewServiceImpl.getEmployeeInfoByMid carEmployeeInfoGoOut:{}",
                RetailJsonUtil.toJson(carEmployeeInfoGoOut));
        // 总部岗位
        List<CarEmployeeInfoGoOut.ChannelPositionInfo> headPositionInfoList =
                carEmployeeInfoGoOut.getHeadPositionsInfoList();
        // 渠道岗位
        List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositionInfoList =
                carEmployeeInfoGoOut.getChannelPositionInfoList();
        // 大区岗位
        List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList =
                carEmployeeInfoGoOut.getBigZonePositionsInfoList();
        // 小区岗位
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList =
                carEmployeeInfoGoOut.getLittleZonePositionsInfoList();
        // 门店岗位
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList =
                carEmployeeInfoGoOut.getStorePositionInfoList();
        // 是否有零售运营岗
        boolean hasCarRetailOperation = channelPositionInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_RETAIL_OPERATION.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有省分车业务负责人岗
        boolean hasCarBusinessManagerProvincial = bigZonePositionsInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有城市经理岗
        boolean hasCarManagerCity = littleZonePositionsInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_MANAGER_CITY.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有门店店长岗
        boolean hasCarStoreManager = storePositionInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_STORE_MANAGER.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有门店主管岗
        boolean hasCarStoreOA = storePositionInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_STORE_OA.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 当前用户列表最大数据查看权�?若有多个岗位，岗位取优先级为：运营检�?> 区域运营管理 > 城市服务经理
        PositionEnum positionEnum = null;
        if (hasCarRetailOperation) {
            positionEnum = PositionEnum.CAR_RETAIL_OPERATION;
        } else if (hasCarBusinessManagerProvincial) {
            positionEnum = PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL;
        } else if (hasCarManagerCity) {
            positionEnum = PositionEnum.CAR_MANAGER_CITY;
        } else if (hasCarStoreManager) {
            positionEnum = PositionEnum.CAR_STORE_MANAGER;
        } else if (hasCarStoreOA) {
            positionEnum = PositionEnum.CAR_STORE_OA;
        }
        return CarEmployeeInfoSoOut.builder().bigZonePositionsInfoList(bigZonePositionsInfoList)
                .littleZonePositionsInfoList(littleZonePositionsInfoList)
                .headPositionInfoList(headPositionInfoList)
                .channelPositionInfoList(channelPositionInfoList)
                .storePositionInfoList(storePositionInfoList)
                .positionEnum(positionEnum).build();
    }

    /**
     * 构建权限信息
     *
     * @param goIn                 入参
     * @param carEmployeeInfoSoOut 员工信息
     */
    public static void buildCountAuth(StaticRetailCountSoIn goIn, CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        // 参数检查，确保入参不为 null
        validateInput(goIn, carEmployeeInfoSoOut);
        // 填充查询参数
        StaticRetailCountSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                createPermissionGroup(carEmployeeInfoSoOut);
        goIn.setAfterSaleWorkbenchPermissionGroup(permissionGroup);
        // 使用 Map 简化条件判�?
        Map<Integer, Integer> permissionMap = createPermissionMap();
        Integer positionCode = carEmployeeInfoSoOut.getPositionEnum().getCode();
        Integer permissionType = permissionMap.get(positionCode);
        // 设置权限类型
        if (permissionType != null) {
            permissionGroup.setAfterSaleWorkbenchPermissionType(permissionType);
        }
    }

    /**
     * 验证输入参数是否合法
     *
     * @param goIn                 入参
     * @param carEmployeeInfoSoOut 员工信息
     */
    private static void validateInput(StaticRetailCountSoIn goIn, CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        if (Objects.isNull(goIn) || Objects.isNull(carEmployeeInfoSoOut) ||
                Objects.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
            throw new IllegalArgumentException("goIn、carEmployeeInfoSoOut 及其岗位信息不能�?null");
        }
    }

    /**
     * 创建权限组对象并填充岗位信息
     *
     * @param carEmployeeInfoSoOut 员工信息
     * @return 权限组对�?
     */
    private static StaticRetailCountSoIn.AfterSaleWorkbenchPermissionGroup createPermissionGroup(
            CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        StaticRetailCountSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                new StaticRetailCountSoIn.AfterSaleWorkbenchPermissionGroup();
        permissionGroup.setBigZonePositionsInfoList(carEmployeeInfoSoOut.getBigZonePositionsInfoList());
        permissionGroup.setLittleZonePositionsInfoList(carEmployeeInfoSoOut.getLittleZonePositionsInfoList());
        permissionGroup.setStorePositionInfoList(carEmployeeInfoSoOut.getStorePositionInfoList());
        return permissionGroup;
    }

    /**
     * 创建岗位代码到权限类型的映射
     *
     * @return 映射关系 Map
     */
    private static Map<Integer, Integer> createPermissionMap() {
        Map<Integer, Integer> permissionMap = new HashMap<>();
        permissionMap.put(PositionEnum.CAR_RETAIL_OPERATION.getCode(), PermissionTypeEnum.ALL.getCode());
        permissionMap.put(PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL.getCode(),
                PermissionTypeEnum.BIG_ZONE.getCode());
        permissionMap.put(PositionEnum.CAR_MANAGER_CITY.getCode(), PermissionTypeEnum.LITTLE_ZONE.getCode());
        permissionMap.put(PositionEnum.CAR_STORE_MANAGER.getCode(), PermissionTypeEnum.STORE.getCode());
        permissionMap.put(PositionEnum.CAR_STORE_OA.getCode(), PermissionTypeEnum.STORE.getCode());
        return permissionMap;
    }

    /**
     * 获取下拉框基础数据
     *
     * @param miID 小米ID
     * @return 下拉框基础数据响应结果
     */
    @Override
    public GetSelectBasicDataSoOut getSelectBasicData(String miID) {
        // 获取汽车员工零售岗位信息
        log.info("RetailComplaintViewServiceImpl.getSelectBasicData miID:{}", miID);
        CarEmployeeInfoGoOut carEmployeeInfoGoOut =
                carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(Long.valueOf(miID), CarChannelTypeEnum.CAR_SALE.getCode());
        log.info("RetailComplaintViewServiceImpl.getSelectBasicData getEmployeeInfoV2ByChannelType:{}",
                RetailJsonUtil.toJson(carEmployeeInfoGoOut));
        if (ObjectUtil.isNull(carEmployeeInfoGoOut)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取汽车员工零售岗位信息失败");
        }
        // 提取岗位信息
        List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositionInfoList =
                carEmployeeInfoGoOut.getChannelPositionInfoList();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList =
                carEmployeeInfoGoOut.getBigZonePositionsInfoList();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList =
                carEmployeeInfoGoOut.getLittleZonePositionsInfoList();
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList =
                carEmployeeInfoGoOut.getStorePositionInfoList();
        // 获取用户最大权限岗�?
        PositionEnum positionEnum =
                getMaxPriorityPosition(channelPositionInfoList, bigZonePositionsInfoList, littleZonePositionsInfoList,
                        storePositionInfoList);
        log.info("RetailComplaintViewServiceImpl.getSelectBasicData positionEnum:{}", RetailJsonUtil.toJson(positionEnum));
        // 判断最大岗位是否为�?
        if (ObjectUtil.isNull(positionEnum)) {
            log.warn("RetailComplaintViewServiceImpl.getSelectBasicData 当前岗位无权限，miID:{},getSelectBasicDataGoOut:{}",
                    miID, RetailJsonUtil.toJson(carEmployeeInfoGoOut));
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "当前岗位无权�?);
        }
        GetSelectBasicDataSoOut soOut = buildSelectBasicDataSoOut(positionEnum, bigZonePositionsInfoList, littleZonePositionsInfoList,
                storePositionInfoList);
        log.info("RetailComplaintViewServiceImpl.getSelectBasicData soOut:{}", RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 获取用户最大权限岗�?
     *
     * @param channelPositionInfoList     总部岗位列表
     * @param bigZonePositionsInfoList    大区岗位列表
     * @param littleZonePositionsInfoList 小区岗位列表
     * @param storePositionInfoList       门店岗位列表
     * @return 最大权限岗位枚�?
     */
    private PositionEnum getMaxPriorityPosition(
            List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositionInfoList,
            List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList,
            List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList,
            List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList) {
        if (hasPosition(channelPositionInfoList, PositionEnum.CAR_RETAIL_OPERATION)) {
            return PositionEnum.CAR_RETAIL_OPERATION;
        } else if (hasPosition(bigZonePositionsInfoList, PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL)) {
            return PositionEnum.CAR_BUSINESS_MANAGER_PROVINCIAL;
        } else if (hasPosition(littleZonePositionsInfoList, PositionEnum.CAR_MANAGER_CITY)) {
            return PositionEnum.CAR_MANAGER_CITY;
        } else if (hasPosition(storePositionInfoList, PositionEnum.CAR_STORE_MANAGER)) {
            return PositionEnum.CAR_STORE_MANAGER;
        } else if (hasPosition(storePositionInfoList, PositionEnum.CAR_STORE_OA)) {
            return PositionEnum.CAR_STORE_OA;
        }
        return null;
    }

    /**
     * 判断是否有特定岗�?
     *
     * @param positionInfoList 岗位信息列表
     * @param positionEnum     岗位枚举
     * @param <T>              岗位信息类型
     * @return 是否有特定岗�?
     */
    private <T> boolean hasPosition(List<T> positionInfoList, PositionEnum positionEnum) {
        if (CollUtil.isEmpty(positionInfoList)) {
            return false;
        }
        return positionInfoList.stream()
                .anyMatch(info -> {
                    if (info instanceof CarEmployeeInfoGoOut.ChannelPositionInfo) {
                        return positionEnum.getCode()
                                .equals(((CarEmployeeInfoGoOut.ChannelPositionInfo) info).getPositionId());
                    } else if (info instanceof CarEmployeeInfoGoOut.ZonePositionInfo) {
                        return positionEnum.getCode()
                                .equals(((CarEmployeeInfoGoOut.ZonePositionInfo) info).getPositionId());
                    } else if (info instanceof CarEmployeeInfoGoOut.StorePositionInfo) {
                        return positionEnum.getCode()
                                .equals(((CarEmployeeInfoGoOut.StorePositionInfo) info).getPositionId());
                    }
                    return false;
                });
    }

    /**
     * 构建下拉框基础数据响应结果
     *
     * @param positionEnum                岗位枚举
     * @param bigZonePositionsInfoList    大区岗位列表
     * @param littleZonePositionsInfoList 小区岗位列表
     * @param storePositionInfoList       门店岗位列表
     * @return 下拉框基础数据响应结果
     */
    private GetSelectBasicDataSoOut buildSelectBasicDataSoOut(PositionEnum positionEnum,
                                                              List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList,
                                                              List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList,
                                                              List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList) {
        switch (positionEnum) {
            // 零售运营
            case CAR_RETAIL_OPERATION:
                ZoneListGoIn zoneListGoIn = ZoneListGoIn.builder()
                        .zoneScopeList(Collections.singletonList(ZoneScopeEnums.SALE.getCode()))
                        .enabled(CarEmployeePrivilegeStateEnum.VALID.getCode())
                        .build();
                List<ZoneDTO> zoneList = storeRemoteGateway.getZoneList(zoneListGoIn);
                return GetSelectBasicDataSoOut.builder()
                        .type(RetailTypeEnum.ZONE.getCode())
                        .selectDataList(zoneList.stream()
                                .map(zoneDTO -> GetSelectBasicDataSoOut.SelectData.builder()
                                        .value(String.valueOf(zoneDTO.getZoneId()))
                                        .label(zoneDTO.getZoneName()).build()).collect(Collectors.toList()))
                        .build();
            // 省分车业务负责人
            case CAR_BUSINESS_MANAGER_PROVINCIAL:
                return GetSelectBasicDataSoOut.builder()
                        .type(RetailTypeEnum.ZONE.getCode())
                        .selectDataList(bigZonePositionsInfoList.stream()
                                .filter(zonePositionInfo -> zonePositionInfo.getPositionId().equals(positionEnum.getCode()))
                                .map(zonePositionInfo -> GetSelectBasicDataSoOut.SelectData.builder()
                                        .value(String.valueOf(zonePositionInfo.getZoneId()))
                                        .label(zonePositionInfo.getZoneName()).build()).collect(Collectors.toList()))
                        .build();
            // 城市经理
            case CAR_MANAGER_CITY:
                return GetSelectBasicDataSoOut.builder()
                        .type(RetailTypeEnum.LITTLE_ZONE.getCode())
                        .selectDataList(littleZonePositionsInfoList.stream()
                                .filter(zonePositionInfo -> zonePositionInfo.getPositionId().equals(positionEnum.getCode()))
                                .map(zonePositionInfo -> GetSelectBasicDataSoOut.SelectData.builder()
                                        .value(String.valueOf(zonePositionInfo.getZoneId()))
                                        .label(zonePositionInfo.getZoneName()).build()).collect(Collectors.toList()))
                        .build();
            // 门店店长，门店主�?
            case CAR_STORE_MANAGER:
            case CAR_STORE_OA:
                return GetSelectBasicDataSoOut.builder()
                        .type(RetailTypeEnum.STORE.getCode())
                        .selectDataList(storePositionInfoList.stream()
                                .filter(zonePositionInfo -> zonePositionInfo.getPositionId().equals(positionEnum.getCode()))
                                .map(storePositionInfo -> GetSelectBasicDataSoOut.SelectData.builder()
                                        .value(storePositionInfo.getOrgId())
                                        .label(storePositionInfo.getStoreName()).build()).collect(Collectors.toList()))
                        .build();
            default:
                return new GetSelectBasicDataSoOut();
        }
    }

    /**
     * 获取气泡数量
     *
     * @param miID 小米ID
     */
    @Override
    public BubbleCountSoOut getBubbleCount(String miID) {
        // 根据mid获取员工信息
        CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(miID);
        log.info("RetailComplaintViewServiceImpl.getBubbleCount carEmployeeInfoSoOut:{}",
                RetailJsonUtil.toJson(carEmployeeInfoSoOut));
        // 如果用户没有配置汽车岗位,直接返回空列�?
        if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
            return BubbleCountSoOut.builder().remindCount(0).firstResponsePendingCount(0).build();
        }
        // 构建权限信息
        StaticRetailCountSoIn soIn = StaticRetailCountSoIn.builder().build();
        buildCountAuth(soIn, carEmployeeInfoSoOut);
        log.info("RetailComplaintViewServiceImpl.getBubbleCount soIn:{}", RetailJsonUtil.toJson(soIn));
        BubbleCountGoOut bubbleCountGoOut =
                retailComplaintGateway.getBubbleCount(Convert.convert(StaticRetailCountGoIn.class, soIn));
        BubbleCountSoOut soOut = Convert.convert(BubbleCountSoOut.class, bubbleCountGoOut);
        log.info("RetailComplaintViewServiceImpl.getBubbleCount soOut:{}",
                RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 获取气泡数量V2
     *
     * @param miID    小米ID
     * @param orgCode 组织编码
     * @return 气泡数量响应结果
     */
    @Override
    public BubbleCountSoOut getBubbleCountV2(String miID, String orgCode) {
        // 构建权限信息
        StaticRetailCountSoIn soIn = StaticRetailCountSoIn.builder().build();
        // 下钻门店编码不为�?
        if (CharSequenceUtil.isNotBlank(orgCode)) {
            soIn.setOrgId(orgCode);
        } else {
            // 根据mid获取员工信息
            CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(miID);
            log.info("RetailComplaintViewServiceImpl.getBubbleCountV2 carEmployeeInfoSoOut:{}",
                    RetailJsonUtil.toJson(carEmployeeInfoSoOut));
            // 如果用户没有配置汽车岗位,直接返回空列�?
            if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
                return BubbleCountSoOut.builder().remindCount(0).firstResponsePendingCount(0).build();
            }
            buildCountAuth(soIn, carEmployeeInfoSoOut);
        }
        log.info("RetailComplaintViewServiceImpl.getBubbleCountV2 soIn:{}", RetailJsonUtil.toJson(soIn));
        BubbleCountGoOut bubbleCountGoOut =
                retailComplaintGateway.getBubbleCount(Convert.convert(StaticRetailCountGoIn.class, soIn));
        BubbleCountSoOut soOut = Convert.convert(BubbleCountSoOut.class, bubbleCountGoOut);
        log.info("RetailComplaintViewServiceImpl.getBubbleCountV2 soOut:{}",
                RetailJsonUtil.toJson(soOut));
        return soOut;
    }


    /**
     * 统计TAB数量
     *
     * @param soIn 统计TAB数量请求参数
     * @return 统计TAB数量响应结果
     */
    @SuppressWarnings("checkstyle:WhitespaceAfter")
    @Override
    public StaticTabCountSoOut staticTabCount(StaticRetailCountSoIn soIn) {
        // 下钻门店编码不为�?
        if (CharSequenceUtil.isNotBlank(soIn.getOrgCode())) {
            soIn.setOrgId(soIn.getOrgCode());
        } else {
            CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(soIn.getMid());
            log.info("RetailComplaintViewServiceImpl.staticTabCount carEmployeeInfoSoOut:{}",
                    RetailJsonUtil.toJson(carEmployeeInfoSoOut));
            // 如果用户没有配置汽车岗位,直接返回空列�?
            if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
                return StaticTabCountSoOut.builder().tabDataList(Collections.emptyList()).build();
            }
            // 构建权限信息
            buildCountAuth(soIn, carEmployeeInfoSoOut);
            Integer type = soIn.getType();
            // 大区下拉数据
            if (Objects.equals(RetailTypeEnum.ZONE.getCode(), type)) {
                soIn.setZoneId(soIn.getValue());
                // 小区下拉数据
            } else if (Objects.equals(RetailTypeEnum.LITTLE_ZONE.getCode(), type)) {
                soIn.setLittleZoneId(soIn.getValue());
                // 门店下拉数据
            } else if (Objects.equals(RetailTypeEnum.STORE.getCode(), type)) {
                soIn.setOrgId(soIn.getValue());
            }
        }
        // 搜索条件
        if (StrUtil.isNotBlank(soIn.getSearchTerm())) {
            if (soIn.getSearchTerm().startsWith(DR_NO_PREFIX)) {
                // 投诉单号
                soIn.setDrNo(soIn.getSearchTerm());
            } else {
                // 联系人手机号
                soIn.setContactPhoneMd5(KeyCenterUtil.md5(soIn.getSearchTerm()));
            }
        }
        log.info("RetailComplaintViewServiceImpl.staticTabCount soIn:{}",
                RetailJsonUtil.toJson(soIn));
        StaticTabCountGoOut staticTabCountGoOut =
                retailComplaintGateway.staticTabCount(Convert.convert(StaticRetailCountGoIn.class, soIn));
        StaticTabCountSoOut soOut = Convert.convert(StaticTabCountSoOut.class, staticTabCountGoOut);
        log.info("RetailComplaintViewServiceImpl.staticTabCount soOut:{}",
                RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 搜索投诉列表
     *
     * @param soIn 搜索请求参数
     * @return 搜索响应结果
     */
    @SuppressWarnings("checkstyle:WhitespaceAfter")
    @Override
    public RetailComplaintListSearchSoOut searchRetailComplaintList(
            RetailComplaintListSearchSoIn soIn) {
        // 下钻门店编码不为�?
        if (CharSequenceUtil.isNotBlank(soIn.getOrgCode())) {
            // 校验下钻权限
            checkDrillDownAuth(soIn.getMid());
            soIn.setOrgId(soIn.getOrgCode());
        } else {
            CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(soIn.getMid());
            log.info("RetailComplaintViewServiceImpl.searchRetailComplaintList carEmployeeInfoSoOut:{}",
                    RetailJsonUtil.toJson(carEmployeeInfoSoOut));
            // 如果用户没有配置汽车岗位,直接返回空列�?
            if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
                return RetailComplaintListSearchSoOut.builder().total(0L).dataList(Collections.emptyList()).build();
            }
            // 构建权限信息
            buildListAuth(soIn, carEmployeeInfoSoOut);
            Integer type = soIn.getType();
            // 大区下拉数据
            if (Objects.equals(RetailTypeEnum.ZONE.getCode(), type)) {
                soIn.setZoneId(soIn.getValue());
                // 小区下拉数据
            } else if (Objects.equals(RetailTypeEnum.LITTLE_ZONE.getCode(), type)) {
                soIn.setLittleZoneId(soIn.getValue());
                // 门店下拉数据
            } else if (Objects.equals(RetailTypeEnum.STORE.getCode(), type)) {
                soIn.setOrgId(soIn.getValue());
            }
        }
        // 搜索条件
        if (StrUtil.isNotBlank(soIn.getSearchTerm())) {
            if (soIn.getSearchTerm().startsWith(DR_NO_PREFIX)) {
                // 投诉单号
                soIn.setDrNo(soIn.getSearchTerm());
            } else {
                // 联系人手机号
                soIn.setContactPhoneMd5(KeyCenterUtil.md5(soIn.getSearchTerm()));
            }
        }
        log.info("RetailComplaintViewServiceImpl.searchRetailComplaintList soIn:{}",
                RetailJsonUtil.toJson(soIn));
        RetailComplaintListSearchGoOut goOut =
                retailComplaintGateway.searchRetailComplaintList(
                        Convert.convert(RetailComplaintListSearchGoIn.class, soIn));
        // 填充基础信息
        fillBasicInfo(goOut);
        RetailComplaintListSearchSoOut soOut = Convert.convert(RetailComplaintListSearchSoOut.class, goOut);
        log.info("RetailComplaintViewServiceImpl.searchRetailComplaintList soOut:{}",
                RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 填充基础信息
     *
     * @param goOut 查询列表
     */
    private static void fillBasicInfo(RetailComplaintListSearchGoOut goOut) {
        List<RetailComplaintListSearchInfo> retailComplaintListSearchInfoList = goOut.getDataList();
        retailComplaintListSearchInfoList.forEach(searchInfo -> {
            // 联系人姓名密�?
            if (StrUtil.isNotBlank(searchInfo.getContactNameC())) {
                searchInfo.setContactName(KeyCenterUtil.decrypt(searchInfo.getContactNameC()));
            }
            // 投诉类型名称
            if (searchInfo.getComplaintType() != null) {
                searchInfo.setComplaintTypeName(ComplaintTypeEnum.getDescByCode(searchInfo.getComplaintType()));
            }
            // 零售客诉单状态名�?
            if (searchInfo.getOrderStatus() != null) {
                searchInfo.setOrderStatusName(
                        RetailComplaintOrderStatusEnum.getDescByCode(searchInfo.getOrderStatus()));
            }
            // 风险等级名称
            if (searchInfo.getRiskLevel() != null) {
                searchInfo.setRiskLevelName(RiskLevelEnum.getDescByCode(searchInfo.getRiskLevel()));
            }
        });
    }

    /**
     * 构建权限信息
     *
     * @param soIn                 入参
     * @param carEmployeeInfoSoOut 返回权限信息
     */
    public static void buildListAuth(RetailComplaintListSearchSoIn soIn, CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        // 防御性检查，确保入参不为 null
        validateListAuthInput(soIn, carEmployeeInfoSoOut);
        // 填充查询参数
        RetailComplaintListSearchSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                createListAuthPermissionGroup(carEmployeeInfoSoOut);
        soIn.setAfterSaleWorkbenchPermissionGroup(permissionGroup);
        // 使用 Map 简化条件判�?
        Map<Integer, Integer> permissionMap = createPermissionMap();
        Integer positionCode = carEmployeeInfoSoOut.getPositionEnum().getCode();
        Integer permissionType = permissionMap.get(positionCode);
        // 设置权限类型
        if (permissionType != null) {
            permissionGroup.setAfterSaleWorkbenchPermissionType(permissionType);
        }
    }

    /**
     * 验证 buildListAuth 方法的输入参数是否合�?
     *
     * @param soIn                 入参
     * @param carEmployeeInfoSoOut 员工信息
     */
    private static void validateListAuthInput(RetailComplaintListSearchSoIn soIn,
                                              CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        if (soIn == null || carEmployeeInfoSoOut == null || carEmployeeInfoSoOut.getPositionEnum() == null) {
            throw new IllegalArgumentException("soIn、carEmployeeInfoSoOut 及其岗位信息不能�?null");
        }
    }

    /**
     * 创建 buildListAuth 方法所需的权限组对象并填充岗位信�?
     *
     * @param carEmployeeInfoSoOut 员工信息
     * @return 权限组对�?
     */
    private static RetailComplaintListSearchSoIn.AfterSaleWorkbenchPermissionGroup createListAuthPermissionGroup(
            CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        RetailComplaintListSearchSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                new RetailComplaintListSearchSoIn.AfterSaleWorkbenchPermissionGroup();
        permissionGroup.setBigZonePositionsInfoList(carEmployeeInfoSoOut.getBigZonePositionsInfoList());
        permissionGroup.setLittleZonePositionsInfoList(carEmployeeInfoSoOut.getLittleZonePositionsInfoList());
        permissionGroup.setStorePositionInfoList(carEmployeeInfoSoOut.getStorePositionInfoList());
        return permissionGroup;
    }

    /**
     * 创建 buildDetailAuth 方法所需的权限组对象并填充岗位信�?
     *
     * @param carEmployeeInfoSoOut 员工信息
     * @return 权限组对�?
     */
    private static RetailComplaintDetailSoIn.AfterSaleWorkbenchPermissionGroup createDetailAuthPermissionGroup(
            CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        RetailComplaintDetailSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                new RetailComplaintDetailSoIn.AfterSaleWorkbenchPermissionGroup();
        permissionGroup.setBigZonePositionsInfoList(carEmployeeInfoSoOut.getBigZonePositionsInfoList());
        permissionGroup.setLittleZonePositionsInfoList(carEmployeeInfoSoOut.getLittleZonePositionsInfoList());
        permissionGroup.setStorePositionInfoList(carEmployeeInfoSoOut.getStorePositionInfoList());
        return permissionGroup;
    }

    /**
     * 获取投诉详情框架信息
     *
     * @param soIn 详情框架请求参数
     * @return 详情框架响应结果
     */
    @Override
    public RetailComplaintDetailFrameSoOut getRetailComplaintDetailAuth(
            RetailComplaintDetailAuthSoIn soIn) {
        RetailComplaintDetaiGoOut retailComplaintDetaiGoOut = retailComplaintGateway.getRetailComplaintDetail(
                RetailComplaintDetailGoIn.builder().drNo(soIn.getDrNo()).build());
        if (ObjectUtil.isNull(retailComplaintDetaiGoOut)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取客诉单详情失�?);
        }
        // 根据mid获取员工信息
        CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(soIn.getMid());
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList =
                carEmployeeInfoSoOut.getStorePositionInfoList();
        Set<String> totalDetailActionAuth = new HashSet<>();
        // 是否有门店店长岗
        boolean hasCarStoreManager = storePositionInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_STORE_MANAGER.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有门店主管岗
        boolean hasCarStoreOA = storePositionInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CAR_STORE_OA.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 门店店长�?
        if (hasCarStoreManager) {
            List<String> detailActionAuth =
                    retailAuthManager.getDetailActionAuth(PositionEnum.CAR_STORE_MANAGER, retailComplaintDetaiGoOut);
            totalDetailActionAuth.addAll(detailActionAuth);
        }
        // 门店主管�?
        if (hasCarStoreOA) {
            List<String> detailActionAuth =
                    retailAuthManager.getDetailActionAuth(PositionEnum.CAR_STORE_OA, retailComplaintDetaiGoOut);
            totalDetailActionAuth.addAll(detailActionAuth);
        }
        // 指派人也要具备和门店店长/门店主管一样的权限
        if (ObjectUtil.isNotNull(retailComplaintDetaiGoOut.getOperatorMid())) {
            // 待首�?
            if (RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode()
                    .equals(retailComplaintDetaiGoOut.getOrderStatus())) {
                totalDetailActionAuth.add(RetailActionConst.REASSIGNMENT_STORES);
                totalDetailActionAuth.add(RetailActionConst.ADD_FOLLOW_UP_RECORDS);
                totalDetailActionAuth.add(RetailActionConst.APPLICATION_FOR_CLOSURE);
            } else if (RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode()
                    .equals(retailComplaintDetaiGoOut.getOrderStatus())) {
                totalDetailActionAuth.add(RetailActionConst.ADD_FOLLOW_UP_RECORDS);
                totalDetailActionAuth.add(RetailActionConst.APPLICATION_FOR_CLOSURE);
            }
        }
        // 如果改派次数超过1�?不允许再改派
        if (retailComplaintDetaiGoOut.getReassignmentTimes() >= 1) {
            totalDetailActionAuth.removeIf(RetailActionConst.REASSIGNMENT_STORES::equals);
        }
        RetailUserActionAuth retailUserActionAuth = new RetailUserActionAuth();
        retailUserActionAuth.setActionsList(totalDetailActionAuth);
        return RetailComplaintDetailFrameSoOut.builder().retailUserActionAuth(retailUserActionAuth).build();
    }

    @Override
    public RetailComplaintDetaiSoOut getRetailComplaintDetail(RetailComplaintDetailSoIn soIn) {
        log.info("RetailComplaintViewServiceImpl.getRetailComplaintDetail soIn:{}",
                RetailJsonUtil.toJson(soIn));
        // 下钻门店编码不为�?
        if (CharSequenceUtil.isNotBlank(soIn.getOrgCode())) {
            // 校验下钻权限
            checkDrillDownAuth(soIn.getMid());
            // 查询下钻门店数据
            soIn.setOrgId(soIn.getOrgCode());
        } else {
            CarEmployeeInfoSoOut carEmployeeInfoSoOut = getEmployeeInfoByMid(soIn.getMid());
            // 校验权限
            checkDetailAuth(carEmployeeInfoSoOut);
            // 构建权限信息
            buildDetailAuth(soIn, carEmployeeInfoSoOut);
        }
        RetailComplaintDetaiGoOut goOut =
                retailComplaintGateway.getRetailComplaintDetail(
                        Convert.convert(RetailComplaintDetailGoIn.class, soIn));
        if (ObjectUtil.isNull(goOut)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "获取客诉单详情失�?);
        }
        // 获取客诉扩展表数�?
        DeliverComplaintExpandGoOut expandGoOut = deliverComplaintExpandGateway.selectDetailByDrNo(goOut.getDrNo());
        // 查询投诉内容
        String complaintContent = goOut.getComplaintContent();
        List<TemplateStructSoIn> complaintStructList = new ArrayList<>();
        if (StrUtil.isNotBlank(complaintContent)) {
            complaintStructList = GsonUtil.fromJson(complaintContent, new TypeToken<List<TemplateStructSoIn>>() {
            }.getType());
        }
        // 获取举报信息中的文件id
        List<Long> fileIdFromStruct = getFileIdFromStruct(complaintStructList);
        // 查询投诉人员信息（举报门店处理人�?
        CompletableFuture<List<EmployeeInfoGoOut>> employInfoFuture = getEmployInfoFuture(
                Collections.singletonList(goOut.getOperatorMid()));
        // 查询门店信息
        CompletableFuture<StoreInfoGoOut> storeInfoFuture =
                getStoreInfoFuture(goOut.getOrgId());
        // 查询文件信息
        CompletableFuture<List<FileInfoGoOut>> fileFuture = getFileFuture(fileIdFromStruct);
        // 补充投诉信息数据
        // 获取请求数据
        List<EmployeeInfoGoOut> employeeInfoList = employInfoFuture.join();
        StoreInfoGoOut storeInfo = storeInfoFuture.join();
        List<FileInfoGoOut> fileInfoList = fileFuture.join();
        // 填充基本信息
        fillBaseInfo(goOut);
        // 填充门店及人员信�?
        fillStoreUserInfo(goOut, employeeInfoList, storeInfo);
        // 填充举报信息详情，文件url
        fillAttachmentInfo(goOut, complaintStructList, fileInfoList);
        // 填充用户诉求
        extractUserRemandInfo(goOut, complaintStructList);
        // 填充线索信息
        fillClueInfo(goOut, expandGoOut);
        RetailComplaintDetaiSoOut soOut = Convert.convert(RetailComplaintDetaiSoOut.class, goOut);
        log.info("RetailComplaintViewServiceImpl.getRetailComplaintDetail soOut:{}",
                RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 校验详情权限
     *
     * @param carEmployeeInfoSoOut 员工信息
     */
    private static void checkDetailAuth(CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        log.info("RetailComplaintViewServiceImpl.getRetailComplaintDetail carEmployeeInfoSoOut:{}",
                RetailJsonUtil.toJson(carEmployeeInfoSoOut));
        // 如果用户没有配置汽车岗位
        if (ObjectUtil.isNull(carEmployeeInfoSoOut) || ObjectUtil.isNull(carEmployeeInfoSoOut.getPositionEnum())) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "无岗位权�?无法查看数据");
        }
    }

    /**
     * 校验下钻权限
     *
     * @param mid 入参
     */
    private void checkDrillDownAuth(String mid) {
        if (CharSequenceUtil.isBlank(mid)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "mid不能为空");
        }
        Map<Long, CarEmployee> longCarEmployeeMap = carEmployeeRemoteGateway.queryCarEmployee(
                CollUtil.newArrayList(Long.valueOf(mid))
        );
        log.info("RetailComplaintViewServiceImpl.getRetailComplaintDetail longCarEmployeeMap:{}", GsonUtil.toJson(longCarEmployeeMap));
        CarEmployee carEmployee = longCarEmployeeMap == null ? null : longCarEmployeeMap.get(Long.valueOf(mid));
        if (carEmployee == null) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "未查询到员工信息");
        }
    }

    /**
     * 构建权限信息
     *
     * @param soIn                 入参
     * @param carEmployeeInfoSoOut 返回权限信息
     */
    public static void buildDetailAuth(RetailComplaintDetailSoIn soIn, CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        // 防御性检查，确保入参不为 null
        validateDetailAuthInput(soIn, carEmployeeInfoSoOut);
        // 填充查询参数
        RetailComplaintDetailSoIn.AfterSaleWorkbenchPermissionGroup permissionGroup =
                createDetailAuthPermissionGroup(carEmployeeInfoSoOut);
        soIn.setAfterSaleWorkbenchPermissionGroup(permissionGroup);
        // 使用 Map 简化条件判�?
        Map<Integer, Integer> permissionMap = createPermissionMap();
        Integer positionCode = carEmployeeInfoSoOut.getPositionEnum().getCode();
        Integer permissionType = permissionMap.get(positionCode);
        // 设置权限类型
        if (permissionType != null) {
            permissionGroup.setAfterSaleWorkbenchPermissionType(permissionType);
        }
    }

    /**
     * 验证 buildDetailAuth 方法的输入参数是否合�?
     *
     * @param soIn                 入参
     * @param carEmployeeInfoSoOut 员工信息
     */
    private static void validateDetailAuthInput(RetailComplaintDetailSoIn soIn,
                                                CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        if (soIn == null || carEmployeeInfoSoOut == null || carEmployeeInfoSoOut.getPositionEnum() == null) {
            throw new IllegalArgumentException("soIn、carEmployeeInfoSoOut 及其岗位信息不能�?null");
        }
    }

    /**
     * 获取投诉单是否有首响记录标识
     *
     * @param soIn 获取首响记录标识请求参数
     * @return 获取首响记录标识响应结果
     */
    @SuppressWarnings("checkstyle:WhitespaceAfter")
    @Override
    public RetailHasFirstResposeRecordFlagSoOut getRetailHasFirstResposeRecordFlag(
            RetailHasFirstResponseRecordFlagSoIn soIn) {
        log.info("RetailComplaintViewServiceImpl.getRetailHasFirstResposeRecordFlag soIn:{}", RetailJsonUtil.toJson(soIn));
        RetailHasFirstResposeRecordFlagGoOut retailHasFirstResposeRecordFlagGoOut =
                retailComplaintGateway.getRetailHasFirstResposeRecordFlag(Convert.convert(
                        RetailHasFirstResponseRecordFlagGoIn.class, soIn));
        RetailHasFirstResposeRecordFlagSoOut soOut = Convert.convert(RetailHasFirstResposeRecordFlagSoOut.class,
                retailHasFirstResposeRecordFlagGoOut);
        log.info("RetailComplaintViewServiceImpl.getRetailHasFirstResposeRecordFlag soOut:{}", RetailJsonUtil.toJson(soOut));
        return soOut;
    }

    /**
     * 填充基本信息
     *
     * @param soOut 客诉详情参数
     */
    public void fillBaseInfo(RetailComplaintDetaiGoOut soOut) {
        // 联系人电话密�?
        if (StrUtil.isNotBlank(soOut.getContactPhoneC())) {
            soOut.setContactPhone(KeyCenterUtil.decrypt(soOut.getContactPhoneC()));
        }
        // 联系人姓名密�?
        if (StrUtil.isNotBlank(soOut.getContactNameC())) {
            soOut.setContactName(KeyCenterUtil.decrypt(soOut.getContactNameC()));
        }
        // 投诉类型名称
        if (soOut.getComplaintType() != null) {
            soOut.setComplaintTypeName(ComplaintTypeEnum.getDescByCode(soOut.getComplaintType()));
        }
        // 风险等级名称
        if (soOut.getRiskLevel() != null) {
            soOut.setRiskLevelName(RiskLevelEnum.getDescByCode(soOut.getRiskLevel()));
        }
        // 零售客诉单状态名�?
        if (soOut.getOrderStatus() != null) {
            soOut.setOrderStatusName(RetailComplaintOrderStatusEnum.getDescByCode(soOut.getOrderStatus()));
        }
        // 处理时间
        // 检�?createTime 是否不为�?
        if (StrUtil.isNotBlank(soOut.getCreateTime())) {
            // �?createTime 转换�?Date 对象
            Date createDate = DateUtil.parse(soOut.getCreateTime());
            // �?Date 对象格式化为年月日时分秒的字符串
            String formattedDate = DateUtil.format(createDate, "yyyy-MM-dd HH:mm:ss");
            // 设置转换后的日期字符�?
            soOut.setCreateTime(formattedDate);
        }
        // 投诉场景
        if (StrUtil.isNotBlank(soOut.getComplaintContent())) {
            soOut.setComplaintScene(ParseComplaintContentUtil.parseComplaintScene(soOut.getComplaintContent()));
        }
    }

    public void fillAttachmentInfo(RetailComplaintDetaiGoOut goOut, List<TemplateStructSoIn> complaintStructList,
                                   List<FileInfoGoOut> fileInfoList) {
        if (isEmpty(complaintStructList)) {
            return;
        }
        List<TemplateStructSoOut> templateStructSoOut =
                OrderViewConverter.INSTANCE.toTemplateStructSoOut(complaintStructList);
        Map<Long, FileInfoGoOut> fileMap = Optional.ofNullable(fileInfoList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(FileInfoGoOut::getFileId, e -> e, (k1, k2) -> k1));
        // 解析附件
        extractAttachmentList(goOut, templateStructSoOut, fileMap);
    }

    /**
     * 解析出投诉内容里面的附件
     * 附件信息返回到soOut�?
     *
     * @param goOut               零售客诉细节出参
     * @param templateStructSoOut 模板内容
     * @param fileMap             文件信息
     */
    private void extractAttachmentList(RetailComplaintDetaiGoOut goOut,
                                       List<TemplateStructSoOut> templateStructSoOut,
                                       Map<Long, FileInfoGoOut> fileMap) {
        List<AttachmentGoOut> attachmentList = new ArrayList<>();

        for (TemplateStructSoOut structSoOut : templateStructSoOut) {
            for (DetailFieldSoOut field : structSoOut.getFields()) {
                // 补充附件url信息
                if (field.getFieldType() == 5 && isNotEmpty(field.getAttachments())) {
                    for (AttachmentSoOut attachment : field.getAttachments()) {
                        populateAttachmentInfo(attachment, fileMap);
                        attachmentList.add(Convert.convert(AttachmentGoOut.class, attachment));
                    }
                }
            }
        }
        goOut.setAttachmentList(attachmentList);
    }

    /**
     * 填充附件的URL和文件名信息
     *
     * @param attachment 附件对象
     * @param fileMap    文件信息映射�?
     */
    private void populateAttachmentInfo(AttachmentSoOut attachment, Map<Long, FileInfoGoOut> fileMap) {
        FileInfoGoOut fileInfo = fileMap.get(attachment.getId());
        if (fileInfo != null) {
            attachment.setUrl(fileInfo.getFileUrl());
            attachment.setFileName(fileInfo.getFileName());
        } else {
            attachment.setUrl("");
            attachment.setFileName("");
        }
    }

    /**
     * 解析用户诉求
     *
     * @param complaintStructList 模板内容
     * @param soOut               客诉单信�?
     */
    private void extractUserRemandInfo(RetailComplaintDetaiGoOut soOut, List<TemplateStructSoIn> complaintStructList) {
        for (TemplateStructSoIn templateStructSoIn : complaintStructList) {
            for (TemplateFieldSoIn field : templateStructSoIn.getFields()) {
                if (ComplaintInfoConstant.USER_DEMAND.equals(field.getFieldCode()) &&
                        Objects.nonNull(field.getValue())) {
                    List<FieldValueSoIn> value = field.getValue();
                    if (CollUtil.isNotEmpty(value)) {
                        soOut.setUserDemand(value.get(0).getDesc());
                    }
                }
            }
        }
    }

    /**
     * 获取举报信息中的文件id
     *
     * @param complaintStructList 举报信息
     * @return 文件id列表
     */
    private List<Long> getFileIdFromStruct(List<TemplateStructSoIn> complaintStructList) {
        List<Long> fileIdList = new ArrayList<>();
        if (isNotEmpty(complaintStructList)) {
            for (TemplateStructSoIn templateStructSoIn : complaintStructList) {
                List<Long> tempFileIdList =
                        templateStructSoIn.getFields().stream().filter(e -> isNotEmpty(e.getAttachmentList()))
                                .flatMap(e -> e.getAttachmentList().stream()).map(AttachmentSoIn::getId)
                                .collect(Collectors.toList());
                fileIdList.addAll(tempFileIdList);
            }
        }
        return fileIdList;
    }

    /**
     * 查询文件信息
     *
     * @param fileIds 文件id列表
     * @return 文件信息列表
     */
    private CompletableFuture<List<FileInfoGoOut>> getFileFuture(List<Long> fileIds) {
        return CompletableFuture.supplyAsync(() -> fileRemoteGateway.getFileList(fileIds, null),
                commonThreadPoolExecutor);
    }

    /**
     * 填充门店及人员信�?
     *
     * @param soOut            客诉详情参数
     * @param employeeInfoList 员工信息列表
     * @param storeInfo        门店信息
     */
    public void fillStoreUserInfo(RetailComplaintDetaiGoOut soOut,
                                  List<EmployeeInfoGoOut> employeeInfoList,
                                  StoreInfoGoOut storeInfo) {
        soOut.setOrgName(Objects.nonNull(storeInfo) ? storeInfo.getOrgName() : "");
        if (isEmpty(employeeInfoList)) {
            log.warn("工单处理人信息为�?);
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap =
                employeeInfoList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, e -> e, (k1, k2) -> k1));
        soOut.setHandleName(employeeMap.containsKey(soOut.getOperatorMid()) ?
                employeeMap.get(soOut.getOperatorMid()).getName() : "");
    }

    /**
     * 填充线索信息
     *
     * @param soOut 客诉详情参数
     */
    public void fillClueInfo(RetailComplaintDetaiGoOut soOut, DeliverComplaintExpandGoOut expandGoOut) {
        // 优先获取数据库中存的线索id,没有获取实时查询的线索id
        if (ObjectUtil.isNotNull(expandGoOut) && ObjectUtil.isNotNull(expandGoOut.getClueId()) && expandGoOut.getClueId() != 0) {
            soOut.setClueId(expandGoOut.getClueId());
        } else {
            // 查询线索信息
            String phone = KeyCenterUtil.decrypt(soOut.getContactPhoneC());
            if (StrUtil.isNotBlank(phone)) {
                GetCLueInfoByPhoneGoOut clueInfo = clueGateway.getClueInfoByPhone(
                        GetClueInfoByPhoneGoIn.builder().phone(phone).build());
                if (ObjectUtil.isNotNull(clueInfo) && ObjectUtil.isNotNull(clueInfo.getClueId())) {
                    soOut.setClueId(clueInfo.getClueId());
                }
            }
        }
    }

    /**
     * 查询客诉人员信息
     *
     * @param midList mid列表
     * @return 客诉人员信息
     */
    public CompletableFuture<List<EmployeeInfoGoOut>> getEmployInfoFuture(List<Long> midList) {
        return CompletableFuture.supplyAsync(() -> eiamRemoteGateway.getEmployeeList(
                EmployeeListGoIn.builder().miIdList(midList).build()), commonThreadPoolExecutor);
    }

    /**
     * 查询门店信息
     *
     * @param orgId 门店id
     * @return 门店信息
     */
    public CompletableFuture<StoreInfoGoOut> getStoreInfoFuture(String orgId) {
        return CompletableFuture.supplyAsync(() -> storeRemoteGateway.getStoreInfo(orgId), commonThreadPoolExecutor);
    }
}
