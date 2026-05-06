package com.wt.complaint.manage.domain.strategy.view;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wt.complaint.manage.api.model.enums.ReportDetailTabEnum;
import com.wt.complaint.manage.domain.api.enums.PermissionTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarUserRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.ClubRpcGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.WarrantyInfoGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.CarUserAggGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.EmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BatchMemberInfoBO;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarUserAggGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintFollowProcessGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.WarrantyPeriodGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.AttachmentSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.UserComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.CarEmployeeInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.manager.ReportAuthManager;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
public abstract class AbstractSearch implements UserComplaintListStrategy {

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
    private WarrantyInfoGateway warrantyInfoGateway;

    @Resource
    private ClubRpcGateway clubRpcGateway;

    @Resource
    private FileRemoteGateway fileRemoteGateway;

    @Resource
    private ReportAuthManager reportAuthManager;

    @Resource
    private CarRemoteGateway carRemoteGateway;

    @Autowired
    private CarEmployeeRemoteGateway carEmployeeRemoteGataway;

    /**
     * 构建权限信息
     *
     * @param goIn 入参
     * @param carEmployeeInfoSoOut 返回权限信息
     */
    public static void buildAuth(UserComplaintListSearchGoIn goIn, CarEmployeeInfoSoOut carEmployeeInfoSoOut) {
        // 填充查询参数
        UserComplaintListSearchGoIn.AfterSaleWorkbenchPermissionGroup afterSaleWorkbenchPermissionGroup =
                new UserComplaintListSearchGoIn.AfterSaleWorkbenchPermissionGroup();
        afterSaleWorkbenchPermissionGroup.setBigZonePositionsInfoList(
                carEmployeeInfoSoOut.getBigZonePositionsInfoList());
        afterSaleWorkbenchPermissionGroup.setLittleZonePositionsInfoList(
                carEmployeeInfoSoOut.getLittleZonePositionsInfoList());
        goIn.setAfterSaleWorkbenchPermissionGroup(afterSaleWorkbenchPermissionGroup);
        // 运营检�?全国举报�?/ 区域运营管理 所管理大区举报�?/ 城市服务经理 所管理城市举报�?
        if (PositionEnum.OPERATIONAL_VERIFICATION.getCode().equals(carEmployeeInfoSoOut.getPositionEnum().getCode())) {
            goIn.getAfterSaleWorkbenchPermissionGroup()
                    .setAfterSaleWorkbenchPermissionType(PermissionTypeEnum.ALL.getCode());
        } else if (PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode()
                .equals(carEmployeeInfoSoOut.getPositionEnum().getCode())) {
            goIn.getAfterSaleWorkbenchPermissionGroup()
                    .setAfterSaleWorkbenchPermissionType(PermissionTypeEnum.BIG_ZONE.getCode());
        } else if (PositionEnum.CITY_SERVICE_MANAGER.getCode()
                .equals(carEmployeeInfoSoOut.getPositionEnum().getCode())) {
            goIn.getAfterSaleWorkbenchPermissionGroup()
                    .setAfterSaleWorkbenchPermissionType(PermissionTypeEnum.LITTLE_ZONE.getCode());
        }
    }

    /**
     * 转换入参
     *
     * @param goIn 入参
     */
    public void transformSearchKey(UserComplaintListSearchGoIn goIn) {
        if (StringUtils.isNotBlank(goIn.getContactPhone())) {
            goIn.setContactPhone(KeyCenterUtil.md5(goIn.getContactPhone()));
        }
        if (StringUtils.isNotBlank(goIn.getVin())) {
            String vid = carRemoteGateway.getVidByVin(goIn.getVin());
            if (StringUtils.isNotEmpty(vid)) {
                goIn.setVin(vid);
            } else {
                goIn.setVin(CommonConst.INVALID_DATA);
            }
        }
    }

    /**
     * 根据mid获取员工信息
     *
     * @param mid 员工id
     * @return 员工职位
     */
    public CarEmployeeInfoSoOut getEmployeeInfoByMid(Long mid) {
        if (ObjectUtil.isNull(mid)) {
            throw new BusinessException(ErrorCodeEnums.INTERNAL_ERROR, "mid为空");
        }
        //获取汽车员工信息和岗位信�?
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = carEmployeeRemoteGataway.getEmployeeInfoV2(mid);
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
        // 是否有运营检核岗
        boolean hasOperationalVerification = headPositionInfoList.stream()
                .anyMatch(headPosition -> PositionEnum.OPERATIONAL_VERIFICATION.getCode()
                        .equals(headPosition.getPositionId()));
        // 是否有区域运营管理岗
        boolean hasRegionalOperationsManagement = bigZonePositionsInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 是否有城市服务经理岗
        boolean hasCityServiceManager = littleZonePositionsInfoList.stream()
                .anyMatch(channelPositionInfo -> PositionEnum.CITY_SERVICE_MANAGER.getCode()
                        .equals(channelPositionInfo.getPositionId()));
        // 当前用户列表最大数据查看权�?若有多个岗位，岗位取优先级为：运营检�?> 区域运营管理 > 城市服务经理
        PositionEnum positionEnum = null;
        if (hasOperationalVerification) {
            positionEnum = PositionEnum.OPERATIONAL_VERIFICATION;
        } else if (hasRegionalOperationsManagement) {
            positionEnum = PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT;
        } else if (hasCityServiceManager) {
            positionEnum = PositionEnum.CITY_SERVICE_MANAGER;
        }
        return CarEmployeeInfoSoOut.builder().bigZonePositionsInfoList(bigZonePositionsInfoList)
                .littleZonePositionsInfoList(littleZonePositionsInfoList)
                .headPositionInfoList(headPositionInfoList)
                .channelPositionInfoList(channelPositionInfoList)
                .hasOperationalVerification(hasOperationalVerification)
                .hasRegionalOperationsManagement(hasRegionalOperationsManagement)
                .hasCityServiceManager(hasCityServiceManager)
                .positionEnum(positionEnum).build();
    }

    /**
     * 查询�?
     *
     * @param vidList vid列表
     * @return 返回车信�?
     */
    public CompletableFuture<List<CarInfoGoOut>> getCarFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getCarSimpleInfo(vidList, null),
                commonThreadPoolExecutor);
    }

    /**
     * 查询车主
     *
     * @param vid 车主vid
     * @return 返回车主信息
     */
    public CompletableFuture<CarUserAggGoOut> getCarUserFuture(String vid) {
        return CompletableFuture.supplyAsync(
                () -> carUserRemoteGateway.userAggQuery(CarUserAggGoIn.builder().vid(vid).build()),
                commonThreadPoolExecutor);
    }

    /**
     * 查询车辆动态信�?
     *
     * @param vidList vid列表
     * @return 车辆动态信�?
     */
    public CompletableFuture<GetDynamicInfoResponseGoOut> getDynamicInfoFuture(List<String> vidList) {
        return CompletableFuture.supplyAsync(() -> carRemoteGateway.getDynamicInfo(vidList), commonThreadPoolExecutor);
    }

    /**
     * 查询车辆保险信息
     *
     * @param vin 车辆vin
     * @return 车辆保险信息
     */
    public CompletableFuture<WarrantyPeriodGoOut> getWarrantyPeriodFuture(String vin) {
        return CompletableFuture.supplyAsync(() -> warrantyInfoGateway.getCarWarrantyPeriodInfo(vin),
                commonThreadPoolExecutor);
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

    /**
     * 批量查门店信�?
     * @param orgIdList 门店id列表
     * @return 门店信息
     */
    public CompletableFuture<List<StoreInfoGoOut>> batchGetStoreInfoFuture(List<String> orgIdList) {
        return CompletableFuture.supplyAsync(() -> storeRemoteGateway.getStoreListInfo(orgIdList),
                commonThreadPoolExecutor);
    }

    public CompletableFuture<List<ComplaintFollowProcessGoOut>> getProcessLogFuture(
            UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        return CompletableFuture.supplyAsync(
                () -> processRepositoryGateway.getProcessListByNo(userComplaintOrderDetailSoOut.getUcNo()),
                commonThreadPoolExecutor);
    }

    /**
     * 查询tab信息
     *
     * @param userComplaintOrderDetailSoOut 入参
     * @return tab信息
     */
    public List<ReportDetailTabEnum> getDetailTabByStatus(
            UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        return ReportDetailTabEnum.listTab(userComplaintOrderDetailSoOut.getOrderStatus());
    }

    /**
     * 查询vip信息
     *
     * @param vidList vid列表
     * @return vip信息
     */
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
     * 查询文件信息
     *
     * @param fileIds 文件id列表
     * @return 文件信息
     */
    public CompletableFuture<List<FileInfoGoOut>> getFileFuture(List<Long> fileIds) {
        return CompletableFuture.supplyAsync(() -> fileRemoteGateway.getFileList(fileIds, null),
                commonThreadPoolExecutor);
    }

    /**
     * 获取举报信息中的文件id
     *
     * @param complaintStructList 文件信息
     * @return 文件id列表
     */
    public List<Long> getFileIdFromStruct(List<TemplateStructSoIn> complaintStructList) {
        List<Long> fileIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(complaintStructList)) {
            for (TemplateStructSoIn templateStructSoIn : complaintStructList) {
                List<Long> tempFileIdList =
                        templateStructSoIn.getFields().stream().filter(e -> CollUtil.isNotEmpty(e.getAttachmentList()))
                                .flatMap(e -> e.getAttachmentList().stream()).map(AttachmentSoIn::getId)
                                .collect(Collectors.toList());
                fileIdList.addAll(tempFileIdList);
            }
        }
        return fileIdList;
    }

}
