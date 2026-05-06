package com.wt.complaint.manage.domain.strategy.pushmessage;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ZonePositionUserGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserBaseInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ZonePositionUserGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import static com.wt.complaint.manage.domain.constant.ComplaintInfoConstant.ORG_NAME;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 参考消息中心PRD <a href="https://xiaomi.f.mioffice.cn/docx/doxk4xaN3XLGR2p0hov4sbPVm9c"/>
 * 模板文档:<a href="https://xiaomi.f.mioffice.cn/wiki/SdNIwAFhoiNuexkRGmyk10il4Nb"/>
 */
@Slf4j
public abstract class AbstractComplaintMessageInformedStrategy implements ComplaintMessageInformedStrategy {

    public static final String REPORT_REQUEST_ID_FORMAT = "report_send:type:%s:uc_no:%s";

    @Resource
    CarRemoteGateway carRemoteGateway;
    @Resource
    StoreRemoteGateway storeRemoteGateway;
    @Resource
    EiamRemoteGateway eiamRemoteGateway;
    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;
    @Value("${pc.main.car.maintenance.url}")
    String pcMainCarMaintenanceUrl;

    public String getVinByVid(String vid) {
        return carRemoteGateway.getVinByVid(vid);
    }

    public CarInfoGoOut getCarInfoByVid(String vid) {
        return carRemoteGateway.getCarInfoByVid(vid);
    }

    public String getEmailByMid(Long mid) {
        return carEmployeeRemoteGateway.queryEmailByMid(mid);
    }

    public String getOrgNameByOrgId(String orgId) {
        StoreInfoGoOut storeInfo = storeRemoteGateway.getStoreInfo(orgId);
        if (storeInfo == null) {
            log.info("orgId:{} not found", orgId);
            return "";
        }
        return storeInfo.getOrgName();
    }

    public List<String> getEmailList(ZonePositionUserGoIn zonePositionUserGoIn) {
        List<ZonePositionUserGoOut> userList = eiamRemoteGateway.getZonePositionUser(zonePositionUserGoIn);
        if (CollectionUtils.isEmpty(userList)) {
            log.warn("当前岗位和区域下,未查询到人员, zonePositionUserGoIn:{}",
                    JacksonUtils.toJson(zonePositionUserGoIn));
            return Collections.emptyList();
        }
        return userList.stream().map(ZonePositionUserGoOut::getEmail).collect(Collectors.toList());
    }

    public List<String> getEmailListByPositionId(Integer positionId) {
        int pageSize = 100;
        int maxPage = 500;
        List<UserBaseInfoGoOut> userList = new ArrayList<>();
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            List<UserBaseInfoGoOut> userListTmp =
                    eiamRemoteGateway.listByPositionIdAndState(Collections.singletonList(positionId), pageNum,
                            pageSize);
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
                log.error(
                        "getEmailListByPositionId 达到最大循环次�?说明该岗位人数超�?万人,需要考虑扩容 positionId:{}",
                        positionId);
            }
        }
        if (CollectionUtils.isEmpty(userList)) {
            log.error("当前岗位没有查询用户, positionId:{}", positionId);
            return Collections.emptyList();
        }
        return userList.stream().map(UserBaseInfoGoOut::getEmail).collect(Collectors.toList());
    }

    /**
     * 通过投诉单获取全部相关角色人员邮�?
     */
    public Set<String> getAllEmailSet(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        return new HashSet<>(getFinalEmailSetByZoneAndPosition(userComplaintOrderDetailSoOut,
                Arrays.asList(PositionEnum.CITY_SERVICE_MANAGER, PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT)));
    }

    /**
     * 通过大区和岗位查询邮�?
     */
    public Set<String> getFinalEmailSetByZoneAndPosition(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut,
                                                         List<PositionEnum> positionEnumList) {
        ArrayList<String> finalEmailList = new ArrayList<>();
        boolean numeric = StringUtils.isNumeric(userComplaintOrderDetailSoOut.getZoneId());
        for (PositionEnum positionEnum : positionEnumList) {

            // 城市服务经理
            if (positionEnum == PositionEnum.CITY_SERVICE_MANAGER && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.CITY_SERVICE_MANAGER.getCode())
                        .littleZoneIdList(Collections.singletonList(
                                Integer.valueOf(userComplaintOrderDetailSoOut.getLittleZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }

            // 区域运营管理
            if (positionEnum == PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT && numeric) {
                List<String> emailList = this.getEmailList(ZonePositionUserGoIn.builder()
                        .positionId(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode())
                        .bigZoneIdList(Collections.singletonList(
                                Integer.valueOf(userComplaintOrderDetailSoOut.getZoneId())))
                        .build());
                finalEmailList.addAll(emailList);
            }
        }

        return new HashSet<>(finalEmailList);
    }

    /**
     * 组装飞书消息扩展字段,�?@王祥�?约定的结�?
     *
     * @param reportOrder 举报单信�?
     * @param orgName 门店名称
     * @return 扩展信息
     */
    @NotNull
    public Map<String, String> getMiOfficePayload(UserComplaintOrderDetailSoOut reportOrder, String orgName) {
        Map<String, String> miOfficePayload = new HashMap<>();
        miOfficePayload.put("ucNo", reportOrder.getUcNo());
        miOfficePayload.put(ORG_NAME, orgName);
        // 跳转举报单详情页
        miOfficePayload.put("href", pcMainCarMaintenanceUrl +
                "storeOperation/reportForm/detail?ucNo=" + reportOrder.getUcNo());
        return miOfficePayload;
    }

}
