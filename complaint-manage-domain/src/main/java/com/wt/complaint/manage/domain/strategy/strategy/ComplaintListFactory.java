package com.wt.complaint.manage.domain.strategy;

import com.wt.complaint.manage.api.model.enums.SourceEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.constant.MrRoleConstant;
import com.wt.complaint.manage.domain.constant.StrategyConstant;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.domain.strategy.complaintlist.ComplaintListStrategy;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ComplaintListFactory {
    @Resource
    private Map<String, ComplaintListStrategy> complaintListStrategyMap;

    @Autowired
    private CarEmployeeRemoteGateway carEmployeeRemoteGataway;

    public ComplaintListStrategy getStrategy(ComplaintListSearchGoIn goIn) {
        if (SourceEnum.PAD_LIST.getCode().equals(goIn.getSource())) {
//            checkPadAuth(goIn);
            return complaintListStrategyMap.get(StrategyConstant.PAD_COMPLAINT_LIST_SEARCH);
        } else if (SourceEnum.AFTER_SALE_WORKBENCH.getCode().equals(goIn.getSource())) {
            //调用用户中台查询用户职位,以及用户负责的区域id和城市id
            CarEmployeeInfoGoOut carEmployeeInfoGoOut = carEmployeeRemoteGataway.getEmployeeInfoV2(goIn.getMid());
            List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositionInfoList = carEmployeeInfoGoOut.getChannelPositionInfoList();
            List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositionsInfoList = carEmployeeInfoGoOut.getBigZonePositionsInfoList();
            List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositionsInfoList = carEmployeeInfoGoOut.getLittleZonePositionsInfoList();
            ComplaintListSearchGoIn.AfterSaleWorkbenchPermissionGroup afterSaleWorkbenchPermissionGroup = new ComplaintListSearchGoIn.AfterSaleWorkbenchPermissionGroup();
            afterSaleWorkbenchPermissionGroup.setBigZonePositionsInfoList(bigZonePositionsInfoList);
            afterSaleWorkbenchPermissionGroup.setLittleZonePositionsInfoList(littleZonePositionsInfoList);
            goIn.setAfterSaleWorkbenchPermissionGroup(afterSaleWorkbenchPermissionGroup);
            boolean isSatisfactionManagement  = channelPositionInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.SATISFACTION_MANAGEMENT.getCode().equals(channelPositionInfo.getPositionId()));
            boolean isRegionalOperationsManagement = bigZonePositionsInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode().equals(channelPositionInfo.getPositionId()));
            boolean isCityServiceManager = littleZonePositionsInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.CITY_SERVICE_MANAGER.getCode().equals(channelPositionInfo.getPositionId()));
            boolean isUrbanExperienceExpert = littleZonePositionsInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.URBAN_EXPERIENCE_EXPERT.getCode().equals(channelPositionInfo.getPositionId()));
            boolean isComplaintHandling = channelPositionInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.COMPLAINT_HANDLING.getCode().equals(channelPositionInfo.getPositionId()));
            boolean isRegionalExperienceExpert = bigZonePositionsInfoList.stream().anyMatch(channelPositionInfo -> PositionEnum.REGIONAL_EXPERIENCE_EXPERT.getCode().equals(channelPositionInfo.getPositionId()));

            // 是否满意度管理或客诉处理-->>可以查看全国数据
            if (isSatisfactionManagement || isComplaintHandling) {
                return complaintListStrategyMap.get(StrategyConstant.CENTER_STAGE_EXPERIENCE_SPECIALIST_SEARCH);
            }
            // 是否区域运营管理-->>可以查看所管理大区数据
            if (isRegionalOperationsManagement || isRegionalExperienceExpert) {
                return complaintListStrategyMap.get(StrategyConstant.REGIONAL_OPERATIONS_MANAGER_SEARCH);
            }
            // 是否城市服务经理或城市体验专�?->>可以产看所管理城市数据
            if (isCityServiceManager || isUrbanExperienceExpert) {
                return complaintListStrategyMap.get(StrategyConstant.CITY_MANAGER_SEARCH);
            }
            return null;
        } else if (SourceEnum.PAD_RELATE_LIST.getCode().equals(goIn.getSource())) {
            // 建单的时�?查询投诉单列�?不要权限校验
            return complaintListStrategyMap.get(StrategyConstant.PAD_RELATE_COMPLAINT_LIST_SEARCH);
        } else {
            log.error("source is not support, source:{}", goIn.getSource());
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "source is not support");
        }
    }

//    private void checkPadAuth(ComplaintListSearchGoIn goIn) {
//        if (!MrRoleConstant.CAR_ORG_MANAGER.equals(goIn.getCurrRole())
//                && !MrRoleConstant.RECEIVER.equals(goIn.getCurrRole())
//                && !MrRoleConstant.CAR_BRAND_REPRESENTATIVE.equals(goIn.getCurrRole())) {
//            log.error("ComplaintListFactory#getStrategy wrong role, goIn:{}",
//                    RetailJsonUtil.toJson(goIn));
//            throw new BusinessException(ErrorCodeEnums.NO_PERMISSION);
//        }
//        if (StringUtils.isBlank(goIn.getOrgId())) {
//            log.error("ComplaintListFactory#getStrategy orgId is null, goIn:{}", RetailJsonUtil.toJson(goIn));
//            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "orgId is null");
//        }
//    }
}
