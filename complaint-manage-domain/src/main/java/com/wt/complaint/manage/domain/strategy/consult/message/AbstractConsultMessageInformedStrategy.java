package com.wt.complaint.manage.domain.strategy.consult.message;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintAuditGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.StoreEmployeeListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserBaseInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.wt.complaint.manage.domain.enumInfo.CarEmployeeEnum;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.domain.strategy.message.MessageInformedStrategy;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.account.ProretailRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wt.complaint.manage.domain.constant.ComplaintInfoConstant.ORG_NAME;

/**
 * 参考消息中心PRD <a href="https://xiaomi.f.mioffice.cn/docx/doxk4xaN3XLGR2p0hov4sbPVm9c"/>
 * 模板文档:<a href="https://xiaomi.f.mioffice.cn/wiki/SdNIwAFhoiNuexkRGmyk10il4Nb"/>
 */
@Slf4j
@SuppressWarnings("all")
public abstract class AbstractConsultMessageInformedStrategy implements ConsultMessageInformedStrategy {
    /**
     * 站内信标�? status字段枚举 1 表示 进度更新
     */
    public static final int NR_BOX_STATUS_PROGRESS_UPDATE = 1;

    /**
     * 站内信标�?status字段枚举 2 表示 待跟�?
     */
    public static final int NR_BOX_STATUS_TO_BE_FOLLOWED = 2;

    public static final String AUDIT_COMPLAINT_REQUEST_ID_FORMAT =
            "complaint_msg_send:type:%s:complaint_id:%s:audit_id:%s";

    public static final String COMPLAINT_REQUEST_ID_FORMAT = "complaint_send:type:%s:complaint_id:%s";

    @Resource
    CarRemoteGateway carRemoteGateway;
    @Resource
    StoreRemoteGateway storeRemoteGateway;
    @Resource
    EiamRemoteGateway eiamRemoteGateway;
    @Resource
    ComplaintAuditGateway complaintAuditGateway;
    @Value("${pc.main.car.maintenance.url}")
    String pcMainCarMaintenanceUrl;
    @Value("${pc.main.customer.service.url}")
    String pcMainCustomerServiceUrl;

    public String getVinByVid(String vid) {
        return carRemoteGateway.getVinByVid(vid);
    }

    public CarInfoGoOut getCarInfoByVid(String vid) {
        return carRemoteGateway.getCarInfoByVid(vid);
    }

    public String getOrgNameByOrgId(String orgId) {
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(orgId);
        if (storeInfo == null) {
            log.info("orgId:{} not found", orgId);
            return "";
        }
        return storeInfo.getOrgName();
    }

    public Set<Long> getMidSetByRoleAndOrg(List<String> roleList, String orgId) {
        // 指定角色对应的id
        List<Integer> positionIdList = CarEmployeeEnum.getIdByRoleList(roleList);
        // 查询指定岗位对应的人员信�?
        StoreEmployeeListGoIn employeeListGoIn = StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build();
        List<EmployeeInfoGoOut> goOutList = eiamRemoteGateway.queryEmployeeByStore(employeeListGoIn);
        return goOutList.stream().map(EmployeeInfoGoOut::getMiId).collect(Collectors.toSet());
    }

    @NotNull
    public Map<String, String> constructNrBoxPayload(ComplaintOrderGoOut complaintOrder, String message,
                                                     Integer status) {
        Map<String, Object> nrBoxExt = new HashMap<>();
        nrBoxExt.put("carNo", complaintOrder.getCarNo());
        nrBoxExt.put("carType", this.getCarInfoByVid(complaintOrder.getVid()) == null ? "" :
                this.getCarInfoByVid(complaintOrder.getVid()).getCarType());
        // 站内信扩展消息内�?
        String vin = this.getVinByVid(complaintOrder.getVid());
        nrBoxExt.put("vin", vin);
        nrBoxExt.put("status", status);
        nrBoxExt.put("message", message);
        nrBoxExt.put("orgId", complaintOrder.getOrgId());
        nrBoxExt.put(ORG_NAME, this.getOrgNameByOrgId(complaintOrder.getOrgId()));
        nrBoxExt.put("pageUrl",
                "complaint/OrderDetail?isFullScreen=true&complaintNo=" + complaintOrder.getComplaintNo());
        // 触发提醒时间
        nrBoxExt.put("pushTime", DateUtil.getTimeStrByDate(new Date()));
        nrBoxExt.put("complaintNo", complaintOrder.getComplaintNo());
        Map<String, String> payLoad = new HashMap<>();
        payLoad.put("content", JacksonUtil.toStr(nrBoxExt));
        return payLoad;
    }

    public List<String> getEmailList(ZonePositionUserGoIn zonePositionUserGoIn) {
        List<ZonePositionUserGoOut> userList = eiamRemoteGateway.getZonePositionUser(zonePositionUserGoIn);
        if (CollectionUtils.isEmpty(userList)) {
            log.warn("当前岗位和区域下,未查询到人员, zonePositionUserGoIn:{}", JacksonUtils.toJson(zonePositionUserGoIn));
            return Collections.emptyList();
        }
        return userList.stream().map(ZonePositionUserGoOut::getEmail).collect(Collectors.toList());
    }

    public Set<String> getEmailListByRoleAndOrg(List<String> roleList, String orgId) {
        // 指定角色对应的id
        List<Integer> positionIdList = CarEmployeeEnum.getIdByRoleList(roleList);
        // 查询指定岗位对应的人员信�?
        StoreEmployeeListGoIn employeeListGoIn = StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build();
        List<EmployeeInfoGoOut> goOutList = eiamRemoteGateway.queryEmployeeByStore(employeeListGoIn);
        return goOutList.stream().map(EmployeeInfoGoOut::getEmail).collect(Collectors.toSet());
    }

    public List<String> getEmailListByPositionId(Integer positionId) {
        int pageSize = 100;
        int maxPage = 500;
        List<UserBaseInfoGoOut> userList = new ArrayList<>();
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            List<UserBaseInfoGoOut> userListTmp =
                    eiamRemoteGateway.listByPositionIdAndState(Collections.singletonList(positionId), pageNum, pageSize);
            if (CollectionUtils.isEmpty(userListTmp) || userListTmp.size() < pageSize) {
                if (!CollectionUtils.isEmpty(userListTmp)) {
                    userList.addAll(userListTmp);
                }
                log.info("分页结束,pageNum:{}", pageNum);
                break;
            } else {
                userList.addAll(userListTmp);
            }
            if (pageNum == maxPage) {
                // 达到最大循环次数，不要阻塞发消息流�?仅发出告�?
                log.error("getEmailListByPositionId 达到最大循环次�?说明该岗位人数超�?万人,需要考虑扩容 positionId:{}",
                        positionId);
            }
        }
        if (CollectionUtils.isEmpty(userList)) {
            log.error("当前岗位没有查询用户, positionId:{}", positionId);
            return Collections.emptyList();
        }
        return userList.stream().map(UserBaseInfoGoOut::getEmail).collect(Collectors.toList());
    }

    @NotNull
    public Map<String, String> getNrMiPushPayload(ComplaintOrderGoOut complaintOrder,
                                                  String orgName,
                                                  List<String> receiverRoleList,
                                                  String title,
                                                  String description) {
        Map<String, Object> appPushExtra = new HashMap<>();
        appPushExtra.put("type", "complaint");
        appPushExtra.put("source", "push");
        Map<String, Object> appPushExtraMessage = new HashMap<>();
        appPushExtra.put("message", appPushExtraMessage);
        appPushExtraMessage.put("pageUrl", "complaint/OrderDetail?isFullScreen=true&complaintNo="
                + complaintOrder.getComplaintNo());
        appPushExtraMessage.put("orgId", complaintOrder.getOrgId());
        appPushExtraMessage.put(ORG_NAME, orgName);
        if (CollUtil.isNotEmpty(receiverRoleList)) {
            appPushExtraMessage.put("userRolekey", receiverRoleList);
        }
        appPushExtraMessage.put("auth", true);

        Map<String, String> nrMiPushPayload = new HashMap<>();
        nrMiPushPayload.put("title", title);
        nrMiPushPayload.put("description", description);
        nrMiPushPayload.put("extra", JacksonUtil.toStr(appPushExtra));
        return nrMiPushPayload;
    }

    /**
     * 通过大区和岗位查询邮�?
     */
    public Set<String> getFinalEmailSetByZoneAndPosition(ComplaintOrderGoOut complaintOrder, List<PositionEnum> positionEnumList) {
        ArrayList<String> finalEmailList = new ArrayList<>();
        boolean numeric = StringUtils.isNumeric(complaintOrder.getZoneId());
        for (PositionEnum positionEnum : positionEnumList) {

            if (positionEnum == PositionEnum.SATISFACTION_MANAGEMENT || positionEnum == PositionEnum.COMPLAINT_HANDLING) {
                finalEmailList.addAll(this.getEmailListByPositionId(positionEnum.getCode()));
            }

            if (positionEnum == PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT
                    && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode())
                        .bigZoneIdList(Collections.singletonList(Integer.valueOf(complaintOrder.getZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }

            if (positionEnum == PositionEnum.CITY_SERVICE_MANAGER && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.CITY_SERVICE_MANAGER.getCode())
                        .littleZoneIdList(Collections.singletonList(Integer.valueOf(complaintOrder.getLittleZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }

            if (positionEnum == PositionEnum.URBAN_EXPERIENCE_EXPERT && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.URBAN_EXPERIENCE_EXPERT.getCode())
                        .littleZoneIdList(Collections.singletonList(Integer.valueOf(complaintOrder.getLittleZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }

            if (positionEnum == PositionEnum.REGIONAL_EXPERIENCE_EXPERT && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.REGIONAL_EXPERIENCE_EXPERT.getCode())
                        .bigZoneIdList(Collections.singletonList(Integer.valueOf(complaintOrder.getZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }
        }

        return new HashSet<>(finalEmailList);
    }

    /**
     * 通过门店和角色获取对应人员邮�?
     * @param complaintOrder
     * @param roleList
     * @return
     */
    public Set<String> getFinalEmailSetByRoleAndOrg(ComplaintOrderGoOut complaintOrder, List<String> roleList) {
        Set<String> emailListByRoleAndOrg = this.getEmailListByRoleAndOrg(roleList, complaintOrder.getOrgId());
        return new HashSet<>(emailListByRoleAndOrg);
    }

    /**
     * 组装飞书消息扩展字段,�?@王祥�?约定的结�?
     *
     * @param complaintOrder
     * @param orgName
     * @return
     */
    @NotNull
    public Map<String, String> getMiOfficePayload(ComplaintOrderGoOut complaintOrder, String orgName) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("complaintOrderId", complaintOrder.getComplaintNo());
        miOfficePayload.put(ORG_NAME, orgName);
        // 跳转投诉单详情页
        miOfficePayload.put("href", pcMainCarMaintenanceUrl +
                "storeOperation/complaint/complaintListDetails?complaintNo=" + complaintOrder.getComplaintNo());
        return miOfficePayload;
    }



    /**
     * 根据岗位ID和门店ID查询相关角色人员邮箱
     * @param positionIdList 岗位ID列表
     * @param orgId 门店ID
     * @return 邮箱列表
     */
    public Set<Long> getMidSetByPositionIdListAndOrg(List<Integer> positionIdList, String orgId) {
        // 查询指定岗位对应的人员信�?
        StoreEmployeeListGoIn employeeListGoIn = StoreEmployeeListGoIn.builder().orgId(orgId).positionIdList(positionIdList).build();
        List<EmployeeInfoGoOut> goOutList = eiamRemoteGateway.queryEmployeeByStore(employeeListGoIn);
        return goOutList.stream().map(EmployeeInfoGoOut::getMiId).filter(mid -> mid != null && mid > 0).collect(Collectors.toSet());
    }

}
