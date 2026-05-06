package com.wt.complaint.manage.domain.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wt.commons.utils.JacksonUtil;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.CarEmployeeInfoSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.UserComplaintOrderDetailSoOut;
import com.wt.complaint.manage.domain.constant.ReportActionConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Component
public class ReportAuthManager {
    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    private static final Map<String, Map<Integer, List<UserAction>>> DETAIL_BASE_AUTH;

    static {
        // 初始�?DETAIL_BASE_AUTH
        Map<String, Map<Integer, List<UserAction>>> detailBaseAuth = new HashMap<>();

        // 区域运营管理权限
        Map<Integer, List<UserAction>> regionalOperationsManagementActions = new HashMap<>();
        // 待接�?
        regionalOperationsManagementActions.put(ReportOrderStatusEnum.PENDING_ORDER.getCode(),
                Collections.singletonList(
                        // 接单
                        UserAction.create(ReportActionConst.PICK_UP)
                ));
        regionalOperationsManagementActions.put(ReportOrderStatusEnum.PENDING_JUDGE.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 添加跟进记录
                        UserAction.create(ReportActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 举报判定
                        UserAction.create(ReportActionConst.REPORT_JUDGMENT)
                )));
        detailBaseAuth.put(String.valueOf(PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode()),
                Collections.unmodifiableMap(regionalOperationsManagementActions));

        // 城市服务经理权限
        Map<Integer, List<UserAction>> cityServiceManagerActions = new HashMap<>();
        cityServiceManagerActions.put(ReportOrderStatusEnum.PENDING_ORDER.getCode(), Collections.singletonList(
                // 接单
                UserAction.create(ReportActionConst.PICK_UP)
        ));
        cityServiceManagerActions.put(ReportOrderStatusEnum.PENDING_JUDGE.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 添加跟进记录
                        UserAction.create(ReportActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 举报判定
                        UserAction.create(ReportActionConst.REPORT_JUDGMENT)
                )));
        detailBaseAuth.put(String.valueOf(PositionEnum.CITY_SERVICE_MANAGER.getCode()),
                Collections.unmodifiableMap(cityServiceManagerActions));
        DETAIL_BASE_AUTH = Collections.unmodifiableMap(detailBaseAuth);
    }

    /**
     * 对外接口
     */
    public List<String> getDetailActionAuth(PositionEnum positionEnum, UserComplaintOrderDetailSoOut soOut,
                                            Long mid) {
        // 列表�?获取列表页权限树
        Map<Integer, List<UserAction>> statusActionList =
                DETAIL_BASE_AUTH.getOrDefault(String.valueOf(positionEnum.getCode()),
                        new HashMap<>());
        List<UserAction> actions = statusActionList.getOrDefault(soOut.getOrderStatus(), new ArrayList<>());
        return actions.stream().map(UserAction::getActionKey)
                .collect(Collectors.toList());
    }

    /**
     * 是否有权�?支持多角色判�?
     */
    public boolean hasDetailActionAuth(Long mid, String actionKey, UserComplaintOrderDetailSoOut soOut) {
        // 获取用户角色
        CarEmployeeInfoSoOut employeeInfo = getEmployeeInfoByMid(mid);
        if (employeeInfo == null) {
            return false;
        }

        for (RoleContext roleContext : employeeInfo.getRoleList()) {
            // 列表�?获取列表页权限树
            Map<Integer, List<UserAction>> statusActionList =
                    DETAIL_BASE_AUTH.getOrDefault(String.valueOf(roleContext.getPositionEnum().getCode()),
                            new HashMap<>());
            List<UserAction> actions = statusActionList.getOrDefault(soOut.getOrderStatus(), new ArrayList<>());

            // 获取权限判断所需数据
            UserActionAuthContext context = new UserActionAuthContext();
            context.setRoleAreaId(roleContext.getRoleAreaId());
            context.setBigZoneId(soOut.getZoneId() == null ? null : Integer.valueOf(soOut.getZoneId()));
            context.setLittleZoneId(soOut.getLittleZoneId() == null ? null : Integer.valueOf(soOut.getLittleZoneId()));

            // 特殊判断
            List<String> userActions = actions.stream()
                    .filter(x -> x.getActionKey().equals(actionKey))
                    .filter(x -> businessCheck(x, context))
                    .map(UserAction::getActionKey)
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(userActions)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 特殊业务逻辑检�?businessCheck
     */
    private boolean businessCheck(UserAction action, UserActionAuthContext context) {
        log.info("UserAuthManager|businessCheck context:{},action:{}", JacksonUtil.toStr(context), JacksonUtil.toStr(action));
        try {
            if (StringUtils.isNotEmpty(action.getFunc())) {
                log.info("invoke func:{}", action.getFunc());
                Method func = UserAuthManager.class.getMethod(action.getFunc(), context.getClass());
                return (Boolean) func.invoke(this, context);
            }
            return true;
        } catch (Exception ex) {
            log.error("UserAuthManager|err,action:{},error:", JacksonUtil.toStr(action), ex);
        }
        return false;
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

        CarEmployeeInfoGoOut employeeInfo = carEmployeeRemoteGateway.getEmployeeInfoV2(mid);
        
        // 获取各类型岗位信�?
        List<CarEmployeeInfoGoOut.ChannelPositionInfo> headPositions = employeeInfo.getHeadPositionsInfoList();
        List<CarEmployeeInfoGoOut.ChannelPositionInfo> channelPositions = employeeInfo.getChannelPositionInfoList();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositions = employeeInfo.getBigZonePositionsInfoList();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositions = employeeInfo.getLittleZonePositionsInfoList();

        CarEmployeeInfoSoOut result = CarEmployeeInfoSoOut.builder()
                .bigZonePositionsInfoList(bigZonePositions)
                .littleZonePositionsInfoList(littleZonePositions)
                .headPositionInfoList(headPositions)
                .channelPositionInfoList(channelPositions)
                .build();

        // 构建角色信息
        result.init();

        return result;
    }
}
