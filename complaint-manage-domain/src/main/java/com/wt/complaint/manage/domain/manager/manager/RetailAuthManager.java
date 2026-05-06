package com.wt.complaint.manage.domain.manager;

import com.wt.complaint.manage.api.model.enums.RetailComplaintOrderStatusEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.constant.RetailActionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class RetailAuthManager {
    @Resource
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    private static final Map<String, Map<Integer, List<UserAction>>> DETAIL_BASE_AUTH;

    static {
        // 初始�?DETAIL_BASE_AUTH
        Map<String, Map<Integer, List<UserAction>>> detailBaseAuth = new HashMap<>();

        // 门店店长权限
        Map<Integer, List<UserAction>> carStoreManagerActions = new HashMap<>();
        // 待首�?
        carStoreManagerActions.put(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 改派门店
                        UserAction.create(RetailActionConst.REASSIGNMENT_STORES),
                        // 添加跟进记录
                        UserAction.create(RetailActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 申请结案
                        UserAction.create(RetailActionConst.APPLICATION_FOR_CLOSURE)
                )));
        carStoreManagerActions.put(RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 添加跟进记录
                        UserAction.create(RetailActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 申请结案
                        UserAction.create(RetailActionConst.APPLICATION_FOR_CLOSURE)
                )));
        // 门店店长权限
        detailBaseAuth.put(String.valueOf(PositionEnum.CAR_STORE_MANAGER.getCode()),
                Collections.unmodifiableMap(carStoreManagerActions));

        // 门店主管权限
        Map<Integer, List<UserAction>> carStoreOAActions = new HashMap<>();
        // 待首�?
        carStoreOAActions.put(RetailComplaintOrderStatusEnum.FIRST_RESPONSE_PENDING.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 改派门店
                        UserAction.create(RetailActionConst.REASSIGNMENT_STORES),
                        // 添加跟进记录
                        UserAction.create(RetailActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 申请结案
                        UserAction.create(RetailActionConst.APPLICATION_FOR_CLOSURE)
                )));
        carStoreOAActions.put(RetailComplaintOrderStatusEnum.IN_PROGRESS.getCode(),
                Collections.unmodifiableList(Arrays.asList(
                        // 添加跟进记录
                        UserAction.create(RetailActionConst.ADD_FOLLOW_UP_RECORDS),
                        // 申请结案
                        UserAction.create(RetailActionConst.APPLICATION_FOR_CLOSURE)
                )));
        // 门店主管权限
        detailBaseAuth.put(String.valueOf(PositionEnum.CAR_STORE_OA.getCode()),
                Collections.unmodifiableMap(carStoreOAActions));
        DETAIL_BASE_AUTH = Collections.unmodifiableMap(detailBaseAuth);
    }

    /**
     * 对外接口
     */
    public List<String> getDetailActionAuth(PositionEnum positionEnum,
                                            RetailComplaintDetaiGoOut retailComplaintDetaiGoOut
    ) {
        // 列表�?获取列表页权限树
        Map<Integer, List<UserAction>> statusActionList =
                DETAIL_BASE_AUTH.getOrDefault(String.valueOf(positionEnum.getCode()),
                        new HashMap<>());
        List<UserAction> actions =
                statusActionList.getOrDefault(retailComplaintDetaiGoOut.getOrderStatus(), new ArrayList<>());
        return actions.stream().map(UserAction::getActionKey)
                .collect(Collectors.toList());
    }
}
