package com.wt.complaint.manage.domain.strategy.deliver.message;

import com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum;
import static com.wt.complaint.manage.domain.api.enums.DeliverPositionEnum.*;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StoreEmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployee;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import static com.wt.complaint.manage.domain.constant.PushConstant.COMPLAINT_ORDER_ID;
import static com.wt.complaint.manage.domain.constant.PushConstant.OPERATOR_NAME;
import com.wt.complaint.manage.domain.model.ComplaintBasicInfo;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新客诉消息组装抽象类
 *
 * @author zhangzheyang
 * @date 2025/6/23
 */
@Slf4j
public abstract class AbstractNewComplaintMessageStrategy implements NewComplaintMessageStrategy {

    @Resource
    CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Resource
    EiamRemoteGateway eiamRemoteGateway;

    @Resource
    StoreRemoteGateway storeRemoteGateway;

    @Value("${pc.main.car.maintenance.url}")
    private String pcMainCarMaintenanceUrl;

    @Value("${pc.main.customer.service.url}")
    private String pcMainCustomerServiceUrl;

    public String getOrgNameByOrgId(String orgId) {
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(orgId);
        if (storeInfo == null) {
            log.info("orgId:{} not found", orgId);
            return "";
        }
        return storeInfo.getOrgName();
    }

    /**
     * 扩展字段只有客诉单号
     *
     * @param complaintBasicInfo 新交付零售客诉基础字段
     */
    @NotNull
    public Map<String, String> getMiOfficePayloadOnlyOrderId(ComplaintBasicInfo complaintBasicInfo) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put(COMPLAINT_ORDER_ID, complaintBasicInfo.getDrNo());
        if (StringUtils.isEmpty(complaintBasicInfo.getOperatorName())) {
            String operatorName = eiamRemoteGateway.getEmployee(complaintBasicInfo.getOperatorMid()).getName();
            miOfficePayload.put(OPERATOR_NAME, operatorName == null ? "" : operatorName);
        } else {
            miOfficePayload.put(OPERATOR_NAME, complaintBasicInfo.getOperatorName());
        }
        return miOfficePayload;
    }

    public void getEmailSet(ComplaintBasicInfo complaintBasicInfo, Set<String> allEmailSet) {

        // 1. 根据跟进人岗位判断拉取哪些人
        if (DeliverPositionEnum.POSITION_A.getPositionId().equals(complaintBasicInfo.getOperatorPositionId())) {
            // 查询A岗主�?
            getZonePositionUsers(null, complaintBasicInfo.getLittleZoneId(), POSITION_A_LEADER.getPositionId()
                    , allEmailSet);
            // 区域邀约经�?
            getZonePositionUsers(complaintBasicInfo.getZoneId(), null, REGIONAL_INVITE_MANAGER.getPositionId(),
                    allEmailSet);
        } else if (DeliverPositionEnum.POSITION_B.getPositionId().equals(complaintBasicInfo.getOperatorPositionId())) {
            // 查询B岗主管和店长
            getStoreEmployees(complaintBasicInfo.getOrgId(),
                    Arrays.asList(POSITION_B_LEADER.getPositionId(), DELIVERY_CENTER_MANAGER.getPositionId()),
                    allEmailSet);
        }
        List<Long> midList = Collections.singletonList(complaintBasicInfo.getOperatorMid());
        // 查询mid对应的邮箱前缀
        addEmailsFromMids(midList, allEmailSet);
    }

    /**
     * 获取区域职位用户的邮箱并添加到列�?
     */
    public void getZonePositionUsers(Integer zoneId, Integer littleZoneId, Integer positionId, Set<String> allEmailSet) {
        if (zoneId == null && littleZoneId == null) {
            log.error("AbstractNewComplaintMessageStrategy#getZonePositionUsers zoneId and littleZoneId is null");
            return;
        }
        ZonePositionUserGoIn goIn = new ZonePositionUserGoIn();
        if (zoneId != null) {
            goIn.setBigZoneIdList(Collections.singletonList(zoneId));
        }
        if (littleZoneId != null) {
            goIn.setLittleZoneIdList(Collections.singletonList(littleZoneId));
        }
        goIn.setPositionId(positionId);
        List<ZonePositionUserGoOut> users = eiamRemoteGateway.getZonePositionUser(goIn);
        if (!CollectionUtils.isEmpty(users)) {
            List<String> emailList = users.stream()
                    .map(ZonePositionUserGoOut::getEmail)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("AbstractNewComplaintMessageStrategy#getZonePositionUsers 获取区域职位用户的邮箱前缀," +
                            " zoneId: {}, positionId: {}, emailList: {}",
                    zoneId, positionId, GsonUtil.toJson(emailList));
            allEmailSet.addAll(emailList);
        }
    }

    /**
     * 获取门店员工的邮箱并添加到列�?
     */
    public void getStoreEmployees(String orgId, List<Integer> positionIdList, Set<String> allEmailSet) {
        if (orgId == null) {
            log.error("AbstractNewComplaintMessageStrategy#getStoreEmployees orgId is null");
            return;
        }
        StoreEmployeeListGoIn goIn = new StoreEmployeeListGoIn();
        goIn.setOrgId(orgId);
        goIn.setPositionIdList(positionIdList);
        List<EmployeeInfoGoOut> employees = eiamRemoteGateway.queryEmployeeByStore(goIn);
        if (!CollectionUtils.isEmpty(employees)) {
            List<String> emailList = employees.stream()
                    .map(EmployeeInfoGoOut::getEmail)
                    .collect(Collectors.toList());
            allEmailSet.addAll(emailList);
            log.info(
                    "AbstractNewComplaintMessageStrategy#getStoreEmployees 获取门店员工的邮�? orgId: {}, positionIdList: {}, emailList: {}",
                    orgId, GsonUtil.toJson(positionIdList), GsonUtil.toJson(emailList));
        }
    }

    /**
     * 根据mid列表获取员工邮箱并添加到列表
     */
    public void addEmailsFromMids(List<Long> midList, Set<String> allEmailSet) {
        if (CollectionUtils.isEmpty(midList)) {
            log.error("AbstractNewComplaintMessageStrategy#addEmailsFromMids midList is empty");
            return;
        }
        Map<Long, CarEmployee> midCarEmployeeMap = carEmployeeRemoteGateway.queryCarEmployee(midList);
        if (!CollectionUtils.isEmpty(midCarEmployeeMap)) {
            List<String> emailList = midCarEmployeeMap.values().stream()
                    .map(CarEmployee::getEmail)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            allEmailSet.addAll(emailList);
            log.info("AbstractNewComplaintMessageStrategy#addEmailsFromMids 获取员工邮箱, midList: {}, emailList: {}",
                    GsonUtil.toJson(midList), GsonUtil.toJson(emailList));
        }
    }

    @NotNull
    public Map<String, String> constructNrBoxPayload(ComplaintBasicInfo complaintBasicInfo) {
        // 站内信扩展消息内�?
        Map<String, String> payLoad = new HashMap<>();
        payLoad.put("stNo", complaintBasicInfo.getStNo());
        payLoad.put("href", "detail?id=" +
                complaintBasicInfo.getStNo());
        return payLoad;
    }

    @NotNull
    public Map<String, String> getMiOfficePayload(ComplaintBasicInfo complaintBasicInfo) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("stNo", complaintBasicInfo.getStNo());
        miOfficePayload.put("href",
                pcMainCustomerServiceUrl
                        + "task-center/handle-order/processing/detail?id="
                        + complaintBasicInfo.getStNo());
        return miOfficePayload;
    }

}
