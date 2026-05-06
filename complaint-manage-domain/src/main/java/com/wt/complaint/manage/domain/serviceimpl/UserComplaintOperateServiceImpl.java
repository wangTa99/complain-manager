package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.UcOrderTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserComplaintOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcExpandOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserComplaintOrderMainGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.UserComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderPickUpSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderRemindSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceInfo;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderUpdateCustomerServiceSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.CreateOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.opetate.JudgeOrderSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderFollowUpRecordSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderPickUpSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderRemindSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.OrderUpdateCustomerServiceSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.CreateOrderSoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.operate.JudgeOrderSoOut;
import com.wt.complaint.manage.domain.model.UserComplaintOrderInfo;
import com.wt.complaint.manage.domain.strategy.operate.UserComplaintOperateFactory;
import com.wt.complaint.manage.domain.strategy.operate.UserComplaintOperateStrategy;
import com.wt.nr.common.utils.GsonUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * @author linjiehong
 * @date 2025/5/21 16:18
 */
@Slf4j
@Service
public class UserComplaintOperateServiceImpl implements UserComplaintOperateService {
    @Resource
    private UserComplaintOperateFactory userComplaintOperateFactory;

    @Resource
    private UserComplaintOrderGateway userComplaintOrderGateway;

    @Override
    public CreateOrderSoOut createOrder(CreateOrderSoIn soIn) {
        // 获取策略
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.mapToUcOrderTypeEnum(soIn.getWorkType(), soIn.getExpandSoIn().getServiceScene());
        UserComplaintOperateStrategy operateStrategy = userComplaintOperateFactory.getStrategy(ucOrderTypeEnum);

        // 创建订单
        String order = operateStrategy.createOrderWithLock(soIn);

        // 订单创建成功，返回订单号
        return CreateOrderSoOut.builder().ucNo(order).build();
    }

    @Override
    public OrderRemindSoOut remindOrder(OrderRemindSoIn soIn) {
        // 获取策略
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(soIn.getUcNo());
        UserComplaintOperateStrategy operateStrategy = userComplaintOperateFactory.getStrategy(ucOrderTypeEnum);

        // 催单
        operateStrategy.remindOrderWithLock(soIn);

        OrderRemindSoOut orderRemindSoOut = new OrderRemindSoOut();
        orderRemindSoOut.setRemindResult("success");
        return orderRemindSoOut;
    }

    @Override
    public OrderPickUpSoOut pickUpOrder(OrderPickUpSoIn soIn) {
        // 获取策略
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(soIn.getUcNo());
        UserComplaintOperateStrategy operateStrategy = userComplaintOperateFactory.getStrategy(ucOrderTypeEnum);

        // 接单
        operateStrategy.PickUpOrder(soIn);

        OrderPickUpSoOut orderPickUpSoOut = new OrderPickUpSoOut();
        orderPickUpSoOut.setResult("success");
        return orderPickUpSoOut;
    }

    @Override
    public OrderFollowUpRecordSoOut addFollowUpRecords(OrderAddFollowUpRecordSoIn soIn) {
        // 获取策略
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(soIn.getUcNo());
        UserComplaintOperateStrategy operateStrategy = userComplaintOperateFactory.getStrategy(ucOrderTypeEnum);

        // 添加跟进记录
        operateStrategy.addFollowUpRecords(soIn);

        OrderFollowUpRecordSoOut followUpRecordSoOut = new OrderFollowUpRecordSoOut();
        followUpRecordSoOut.setRecordResult("success");
        return followUpRecordSoOut;
    }

    @Override
    public JudgeOrderSoOut judgeOrder(JudgeOrderSoIn soIn) {
        // 获取策略
        UcOrderTypeEnum ucOrderTypeEnum =
                UcOrderTypeEnum.getByUcNo(soIn.getUcNo());
        UserComplaintOperateStrategy operateStrategy = userComplaintOperateFactory.getStrategy(ucOrderTypeEnum);

        // 举报判定
        operateStrategy.judgeOrder(soIn);

        JudgeOrderSoOut judgeOrderSoOut = new JudgeOrderSoOut();
        judgeOrderSoOut.setResult("success");
        return judgeOrderSoOut;
    }

    @Override
    public OrderUpdateCustomerServiceSoOut updateCustomer(OrderUpdateCustomerServiceSoIn soIn) {
        OrderUpdateCustomerServiceSoOut soOut = new OrderUpdateCustomerServiceSoOut();

        // 查询需要更新的客诉类单�?
        List<String> stNoList = soIn.getStNo();
        UcOrderInfoGoIn ucOrderInfoGoIn = new UcOrderInfoGoIn();
        ucOrderInfoGoIn.setStNoList(stNoList);
        UserComplaintOrderMainGoOut userComplaintOrderMainGoOut =
                userComplaintOrderGateway.searchUserComplaintMainData(ucOrderInfoGoIn);
        log.info("查询到的客诉单信�?{}", GsonUtil.toJson(userComplaintOrderMainGoOut));
        if (userComplaintOrderMainGoOut == null ||
                CollUtil.isEmpty(userComplaintOrderMainGoOut.getUserComplaintOrderInfoList())) {
            log.info("客诉单没有查询到该工单的信息，stNoList:{}", GsonUtil.toJson(stNoList));
            soOut.setUpdateResult(Boolean.TRUE);
            return soOut;
        }

        // 映射成key:单号 value:客服id
        Map<String, Long> customerServiceMap = soIn.getOrderUpdateCustomerServiceInfos().stream()
                .collect(Collectors.toMap(OrderUpdateCustomerServiceInfo::getStNo,
                        OrderUpdateCustomerServiceInfo::getCustomerServiceMid, (v1, v2) -> v1));
        log.info("映射后的客服信息:{}", GsonUtil.toJson(customerServiceMap));

        // 更新客服mid
        List<UserComplaintOrderInfo> collect = userComplaintOrderMainGoOut.getUserComplaintOrderInfoList().stream()
            .filter(e -> !Objects.equals(e.getOrderStatus(), ReportOrderStatusEnum.FINISH.getCode())).collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            soOut.setUpdateResult(Boolean.TRUE);
            return soOut;
        }
        List<UcExpandOrderGoIn> ucExpandOrderGoInList = collect.stream()
            .filter(e -> !Objects.equals(e.getOrderStatus(), ReportOrderStatusEnum.FINISH.getCode()))
                .map(e -> UcExpandOrderGoIn.builder()
                            .ucNo(e.getUcNo())
                            .customerServiceMid(customerServiceMap.get(e.getSuperTicketNo())).build())
                .collect(Collectors.toList());

        // 批量更新
        userComplaintOrderGateway.batchUpdateByUcNo(ucExpandOrderGoInList);
        soOut.setUpdateResult(Boolean.TRUE);
        return soOut;
    }

}
