package com.wt.complaint.manage.infrastructure.gatewayimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.GenderEnum;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.ServiceSceneEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarUserRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcExpandOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderExpandGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderExpandGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListFillDataSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchInfo;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.model.UserComplaintExpandInfo;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.converter.ComplaintOrderConverter;
import com.wt.complaint.manage.infrastructure.converter.UcOrderExpandConverter;
import com.wt.complaint.manage.infrastructure.mapper.UserComplaintExpandMapper;
import com.wt.complaint.manage.infrastructure.mapper.UserComplaintOrderMapper;
import com.wt.complaint.manage.infrastructure.model.UserComplaintExpandDO;
import com.wt.complaint.manage.infrastructure.model.UserComplaintOrderDO;
import com.wt.complaint.manage.infrastructure.model.UserComplaintOrderDetailDO;
import com.wt.complaint.manage.infrastructure.model.param.UcExpandOrderSearchParam;
import com.wt.complaint.manage.infrastructure.model.param.UserComplaintExpandUpdateParam;
import com.wt.complaint.manage.infrastructure.model.param.UserComplaintOrderSearchParam;
import com.wt.complaint.manage.infrastructure.model.param.UserComplaintOrderUpdateParam;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author linjiehong
 * @date 2025/5/21 15:43
 */
@Slf4j
@Component
public class UserComplaintOrderGatewayImpl implements UserComplaintOrderGateway {
    @Resource
    private UserComplaintOrderMapper userComplaintOrderMapper;

    @Resource
    private UserComplaintExpandMapper userComplaintExpandMapper;

    @Resource
    private MoneThreadPoolExecutor userComplaintOrderListExecutor;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Resource
    private CarUserRemoteGateway carUserRemoteGateway;

    @Resource
    private EiamRemoteGateway eiamRemoteGateway;

    @Resource
    private StoreRemoteGateway storeRemoteGateway;

    @Override
    public int createUserComplaintOrder(UcOrderInfoGoIn param) {
        UserComplaintOrderDO userComplaintOrderDO = Convert.convert(UserComplaintOrderDO.class, param);
        return userComplaintOrderMapper.insertSelective(userComplaintOrderDO);
    }

    @Override
    public int createUserComplaintOrderExpand(UcOrderExpandGoIn param) {
        UserComplaintExpandDO userComplaintExpandDO = UcOrderExpandConverter.INSTANCE.toDO(param);
        return userComplaintExpandMapper.insertSelective(userComplaintExpandDO);
    }

    @Override
    public UserComplaintListSearchSoOut searchUserComplaintList(UserComplaintListSearchGoIn goIn) {
        // 分页查询投诉单表
        PageHelper.startPage(goIn.getPageNum(), goIn.getPageSize());
        List<UserComplaintOrderDetailDO> userComplaintOrderDetailDOList =
                userComplaintOrderMapper.selectPageByParam(goIn);
        PageInfo<UserComplaintOrderDetailDO> pageInfo = new PageInfo<>(userComplaintOrderDetailDOList);
        UserComplaintListSearchSoOut result = new UserComplaintListSearchSoOut();
        result.setTotal(pageInfo.getTotal());
        List<UserComplaintListSearchInfo> searchInfoList =
                ComplaintOrderConverter.INSTANCE.toSearchInfoList(userComplaintOrderDetailDOList);
        result.setDataList(searchInfoList);
        if (CollectionUtil.isNotEmpty(userComplaintOrderDetailDOList)) {
            // 获取vid
            List<String> vidList = userComplaintOrderDetailDOList.stream()
                    .map(UserComplaintOrderDetailDO::getVid)
                    .collect(Collectors.toList());
            // 获取orgId
            List<String> orgIdList = userComplaintOrderDetailDOList.stream()
                    .map(UserComplaintOrderDetailDO::getOrgId)
                    .collect(Collectors.toList());
            // 多线程并行获取数�?
            List<CompletableFuture<Void>> completableFutureList = Lists.newArrayList();
            UserComplaintListFillDataSoOut userComplaintListFillDataSoOut = new UserComplaintListFillDataSoOut();
            completableFutureList.add(CompletableFuture.runAsync(
                    () -> userComplaintListFillDataSoOut.setGetDynamicInfoResponseGoOut(
                            carRemoteGateway.getDynamicInfo(vidList)), userComplaintOrderListExecutor));
            completableFutureList.add(CompletableFuture.runAsync(
                    () -> userComplaintListFillDataSoOut.setStoreInfoGoOutList(
                            storeRemoteGateway.getStoreListInfo(orgIdList)),
                    userComplaintOrderListExecutor));
            List<Long> userMids = getUserMids(searchInfoList);
            completableFutureList.add(CompletableFuture.runAsync(
                    () -> userComplaintListFillDataSoOut.setMidNameMap(eiamRemoteGateway.getNameByMid(userMids)),
                    userComplaintOrderListExecutor));
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
            // 填充用户mid名称
            fillUserMidName(searchInfoList, userComplaintListFillDataSoOut.getMidNameMap());
            // 填充vin数据
            fillVinData(searchInfoList, userComplaintListFillDataSoOut.getGetDynamicInfoResponseGoOut());
            // 填充门店信息
            fillStoreInfoData(searchInfoList, userComplaintListFillDataSoOut.getStoreInfoGoOutList());
            // 填充基础信息
            fillBasicInfo(searchInfoList);
        }
        return result;
    }

    /**
     * 获取用户mid列表
     */
    private static List<Long> getUserMids(List<UserComplaintListSearchInfo> searchInfoList) {
        return searchInfoList.stream().map(UserComplaintListSearchInfo::getOperatorMid).distinct()
                .collect(Collectors.toList());
    }

    /**
     * 填充用户mid名称
     */
    private static void fillUserMidName(List<UserComplaintListSearchInfo> searchInfoList, Map<Long, String> nameByMid) {
        if (nameByMid != null) {
            searchInfoList.forEach(complaintListSearchInfo -> {
                if (complaintListSearchInfo.getOperatorMid() != null) {
                    complaintListSearchInfo.setOperatorName(nameByMid.get(complaintListSearchInfo.getOperatorMid()));
                }
            });
        }
    }

    /**
     * 填充vin数据
     */
    private static void fillVinData(List<UserComplaintListSearchInfo> searchInfoList,
                                    GetDynamicInfoResponseGoOut getDynamicInfoResponseGoOut) {
        if (getDynamicInfoResponseGoOut != null && CollectionUtil.isNotEmpty(getDynamicInfoResponseGoOut.getItems())) {
            List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto> getDynamicInfoResponseGoOutItems =
                    getDynamicInfoResponseGoOut.getItems();
            Map<String, List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto>> dynamicInfoMap =
                    getDynamicInfoResponseGoOutItems.stream()
                            .collect(Collectors.groupingBy(GetDynamicInfoResponseGoOut.DynamicInfoItemDto::getVid));
            searchInfoList.forEach(complaintListSearchInfo -> {
                List<GetDynamicInfoResponseGoOut.DynamicInfoItemDto> dynamicInfoItemDtoList =
                        dynamicInfoMap.get(complaintListSearchInfo.getVid());
                if (!ObjectUtils.isEmpty(dynamicInfoItemDtoList)) {
                    complaintListSearchInfo.setVin(dynamicInfoItemDtoList.get(0).getVin());
                }
            });
        }
    }

    /**
     * 填充门店信息
     */
    private static void fillStoreInfoData(List<UserComplaintListSearchInfo> searchInfoList,
                                          List<StoreInfoGoOut> storeInfoGoOutList) {
        if (CollectionUtil.isNotEmpty(storeInfoGoOutList)) {
            Map<String, List<StoreInfoGoOut>> storeInfoMap =
                    storeInfoGoOutList.stream().collect(Collectors.groupingBy(StoreInfoGoOut::getOrgId));
            searchInfoList.forEach(complaintListSearchInfo -> {
                List<StoreInfoGoOut> storeInfoGoOuts = storeInfoMap.get(complaintListSearchInfo.getOrgId());
                if (!ObjectUtils.isEmpty(storeInfoGoOuts)) {
                    complaintListSearchInfo.setOrgName(storeInfoGoOuts.get(0).getOrgName());
                }
            });
        }
    }

    /**
     * 填充基础信息
     */
    private static void fillBasicInfo(List<UserComplaintListSearchInfo> searchInfoList) {
        searchInfoList.forEach(complaintListSearchInfo -> {
            // 投诉单状态名�?
            if (complaintListSearchInfo.getOrderStatus() != null) {
                complaintListSearchInfo.setOrderStatusName(
                        ReportOrderStatusEnum.getDescByCode(complaintListSearchInfo.getOrderStatus()));
            }
            // 联系人电�?
            if (!StringUtils.isEmpty(complaintListSearchInfo.getContactPhoneC())) {
                complaintListSearchInfo.setContactPhone(
                        KeyCenterUtil.decrypt(complaintListSearchInfo.getContactPhoneC()));
            }
            // 联系人姓�?
            if (!StringUtils.isEmpty(complaintListSearchInfo.getContactNameC())) {
                complaintListSearchInfo.setContactName(
                        KeyCenterUtil.decrypt(complaintListSearchInfo.getContactNameC()));
            }
            // mid处理
            if (complaintListSearchInfo.getOperatorMid() != null && complaintListSearchInfo.getOperatorMid() == 0L) {
                complaintListSearchInfo.setOperatorMid(null);
            }
            if (!StringUtils.isEmpty(complaintListSearchInfo.getFinishTime())) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date finishTime = fmt.parse(complaintListSearchInfo.getFinishTime());
                    if (DateUtil.isDefaultTime(finishTime)) {
                        complaintListSearchInfo.setFinishTime("");
                    }
                } catch (Exception e) {
                    log.info("complaintListSearchInfo.FinishTime :{}, exception:",
                            com.xiaomi.newretail.common.tools.utils.GsonUtil.toJsonStr(
                                    complaintListSearchInfo.getFinishTime()), e);
                }
            }
            // 根据性别处理xx先生,xx女士显示
            if (!StringUtils.isEmpty(complaintListSearchInfo.getContactName())) {
                if (GenderEnum.MALE.getCode().equals(complaintListSearchInfo.getContactGender())) {
                    complaintListSearchInfo.setContactName(
                            complaintListSearchInfo.getContactName().charAt(0) + GenderEnum.MALE.getExtend());
                } else if (GenderEnum.FEMALE.getCode().equals(complaintListSearchInfo.getContactGender())) {
                    complaintListSearchInfo.setContactName(
                            complaintListSearchInfo.getContactName().charAt(0) + GenderEnum.FEMALE.getExtend());
                } else {
                    complaintListSearchInfo.setContactName(
                            complaintListSearchInfo.getContactName().charAt(0) + GenderEnum.UNKNOWN.getExtend());
                }
            }
            // 转换举报场景
            if (StrUtil.isNotBlank(complaintListSearchInfo.getServiceScene())) {
                String[] serviceSceneCodes = complaintListSearchInfo.getServiceScene().split(",");
                List<String> serviceSceneDescs = new ArrayList<>();
                for (String codeStr : serviceSceneCodes) {
                    try {
                        int code = Integer.parseInt(codeStr.trim());
                        ServiceSceneEnum sceneEnum = ServiceSceneEnum.getByCode(code);
                        if (sceneEnum != null) {
                            serviceSceneDescs.add(sceneEnum.getDesc());
                        }
                    } catch (NumberFormatException e) {
                        log.error("UserComplaintOrderGatewayImpl#fillBasicInfo business error,req:{},e:{}",
                                RetailJsonUtil.toJson(searchInfoList), e.getMessage());
                    }
                }
                String serviceSceneDescStr = String.join(",", serviceSceneDescs);
                complaintListSearchInfo.setServiceScene(serviceSceneDescStr);
            }
        });
    }

    @Override
    public UserComplaintOrderDetailSoOut selectDetailByUcNo(String ucNo) {
        if (StrUtil.isBlank(ucNo)) {
            return new UserComplaintOrderDetailSoOut();
        }
        return userComplaintOrderMapper.selectDetailByUcNo(ucNo);
    }

    @Override
    public int updateOrderSelective(UcOrderUpdateGoIn updateGoIn) {
        if (updateGoIn == null || updateGoIn.getUcNo() == null) {
            log.info("updateOrder param is null, updateGoIn: {} ", GsonUtil.toJson(updateGoIn));
            return 0;
        }
        UserComplaintOrderUpdateParam param = Convert.convert(UserComplaintOrderUpdateParam.class, updateGoIn);

        return userComplaintOrderMapper.updateByParam(updateGoIn.getUcNo(), param);
    }

    @Override
    public UserComplaintOrderMainGoOut searchUserComplaintMainData(UcOrderInfoGoIn goIn) {
        // 参数转换
        UserComplaintOrderSearchParam param = Convert.convert(UserComplaintOrderSearchParam.class, goIn);

        // 查询数据
        List<UserComplaintOrderDO> orderList = Optional.ofNullable(userComplaintOrderMapper.selectByParam(param))
                .orElse(Collections.emptyList());

        // 数据转换并构建返回对�?
        UserComplaintOrderMainGoOut result = new UserComplaintOrderMainGoOut();
        result.setUserComplaintOrderInfoList(orderList.stream()
                .map(order -> Convert.convert(UserComplaintOrderInfo.class, order))
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public UserComplaintOrderExpandGoOut searchUserComplaintExpandData(UcOrderExpandGoIn goIn) {
        // 参数转换
        UcExpandOrderSearchParam param = Convert.convert(UcExpandOrderSearchParam.class, goIn);

        // 查询数据
        List<UserComplaintExpandDO> orderList = Optional.ofNullable(userComplaintExpandMapper.selectByParam(param))
                .orElse(Collections.emptyList());

        // 数据转换并构建返回对�?
        UserComplaintOrderExpandGoOut result = new UserComplaintOrderExpandGoOut();
        result.setUserComplaintExpandInfoList(orderList.stream()
                .map(order -> Convert.convert(UserComplaintExpandInfo.class, order))
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public int updateExpandSelective(UcExpandOrderGoIn updateGoIn) {
        if (updateGoIn == null || updateGoIn.getUcNo() == null) {
            log.info("updateExpandOrder param is null, updateGoIn: {} ", GsonUtil.toJson(updateGoIn));
            return 0;
        }
        UserComplaintExpandUpdateParam param = Convert.convert(UserComplaintExpandUpdateParam.class, updateGoIn);

        return userComplaintExpandMapper.updateByParam(updateGoIn.getUcNo(), param);
    }

    @Override
    public int batchUpdateByUcNo(List<UcExpandOrderGoIn> updateGoIn) {
        if (CollUtil.isEmpty(updateGoIn)) {
            log.info("updateExpandOrder param is null, updateGoIn: {} ", GsonUtil.toJson(updateGoIn));
            return 0;
        }
        List<UserComplaintExpandUpdateParam> param = updateGoIn.stream()
                .map(order -> Convert.convert(UserComplaintExpandUpdateParam.class, order))
                .collect(Collectors.toList());
        log.info("batchUpdateByUcNo param: {} ", GsonUtil.toJson(param));
        return userComplaintExpandMapper.batchUpdateByUcNo(param);
    }
}
