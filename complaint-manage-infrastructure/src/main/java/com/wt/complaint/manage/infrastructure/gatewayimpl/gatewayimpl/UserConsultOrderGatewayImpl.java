package com.wt.complaint.manage.infrastructure.gatewayimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.wt.complaint.manage.domain.api.gateway.interfaces.UserConsultOrderGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ConsultListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.UcConsultOrderUpdateGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.UserConsultOrderMainGoOut;
import com.wt.complaint.manage.domain.model.ConsultStatusCountInfo;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import com.wt.complaint.manage.infrastructure.mapper.UserConsultOrderMapper;
import com.wt.complaint.manage.infrastructure.model.UserConsultOrderDO;
import com.wt.complaint.manage.infrastructure.model.param.UserConsultOrderSearchParam;
import com.wt.complaint.manage.infrastructure.model.param.UserConsultOrderUpdateParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 咨询类单据网关实�?
 */
@Slf4j
@Component
public class UserConsultOrderGatewayImpl implements UserConsultOrderGateway {

    private static final Long DEFAULT_TIME_IN_DATABASE = 18374400000L;

    @Resource
    private UserConsultOrderMapper userConsultOrderMapper;

    @Override
    public int createUserConsultOrder(UcConsultOrderGoIn param) {
        UserConsultOrderDO userConsultOrderDO = Convert.convert(UserConsultOrderDO.class, param);
        return userConsultOrderMapper.insertSelective(userConsultOrderDO);
    }

    @Override
    public int updateOrderSelective(UcConsultOrderUpdateGoIn updateGoIn) {
        if (updateGoIn == null || updateGoIn.getConsultNo() == null) {
            log.info("updateOrder param is null, updateGoIn: {} ", updateGoIn);
            return 0;
        }
        UserConsultOrderUpdateParam param = Convert.convert(UserConsultOrderUpdateParam.class, updateGoIn);
        return userConsultOrderMapper.updateByParam(updateGoIn.getConsultNo(), param);
    }

    @Override
    public UserConsultOrderMainGoOut searchUserConsultMainData(UcConsultOrderGoIn goIn) {
        // 参数转换
        UserConsultOrderSearchParam param = Convert.convert(UserConsultOrderSearchParam.class, goIn);

        // 查询数据
        List<UserConsultOrderDO> orderList = Optional.ofNullable(userConsultOrderMapper.selectByParam(param))
                .orElse(Collections.emptyList());

        // 数据转换并构建返回对�?
        UserConsultOrderMainGoOut result = new UserConsultOrderMainGoOut();
        result.setUserConsultOrderInfoList(orderList.stream()
                .map(order -> Convert.convert(UserConsultOrderInfo.class, order))
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public UserConsultOrderInfo searchUserConsultOrderInfo(UcConsultOrderGoIn goIn) {
        // 参数转换
        UserConsultOrderSearchParam param = Convert.convert(UserConsultOrderSearchParam.class, goIn);

        // 查询数据
        List<UserConsultOrderDO> orderList = Optional.ofNullable(userConsultOrderMapper.selectByParam(param))
                .orElse(Collections.emptyList());
        if (CollUtil.isNotEmpty(orderList)) {
            return Convert.convert(UserConsultOrderInfo.class, orderList.get(0));
        }
        return null;
    }

    @Override
    public  List<ConsultStatusCountInfo> countConsultStatistics(String orgId,Long  mid) {
        return Optional
                .ofNullable(userConsultOrderMapper.countStatisticsByOrgId(orgId,mid))
                .orElse(Collections.emptyList());

    }

    @Override
    public long countPadConsultPage(ConsultListGoIn goIn) {
        UserConsultOrderSearchParam param = buildSearchParam(goIn);
        return userConsultOrderMapper.countPadPageByParam(param);
    }

    @Override
    public List<UserConsultOrderInfo> pagePadConsultOrders(ConsultListGoIn goIn) {
        UserConsultOrderSearchParam param = buildSearchParam(goIn);
        List<UserConsultOrderDO> orderList = Optional
                .ofNullable(userConsultOrderMapper.padPageByParam(param))
                .orElse(Collections.emptyList());
        orderList.forEach(o->{
            if(o.getCreateTime().getTime() == DEFAULT_TIME_IN_DATABASE){
                o.setCreateTime(null);
            }
            if(o.getExpectingBackTime().getTime() == DEFAULT_TIME_IN_DATABASE){
                o.setExpectingBackTime(null);
            }
        });
        return orderList.stream()
                .map(order -> Convert.convert(UserConsultOrderInfo.class, order))
                .collect(Collectors.toList());
    }

    @Override
    public long countWebConsultPage(ConsultListGoIn goIn) {
        UserConsultOrderSearchParam param = buildSearchParam(goIn);
        return userConsultOrderMapper.countWebPageByParam(param);
    }

    @Override
    public List<UserConsultOrderInfo> pageWebConsultOrders(ConsultListGoIn goIn) {
        UserConsultOrderSearchParam param = buildSearchParam(goIn);
        List<UserConsultOrderDO> orderList = Optional
                .ofNullable(userConsultOrderMapper.webPageByParam(param))
                .orElse(Collections.emptyList());
        orderList.forEach(o->{
            if(o.getCreateTime().getTime() == DEFAULT_TIME_IN_DATABASE){
                o.setCreateTime(null);
            }
            if(o.getExpectingBackTime().getTime() == DEFAULT_TIME_IN_DATABASE){
                o.setExpectingBackTime(null);
            }
        });
        return orderList.stream()
                .map(order -> Convert.convert(UserConsultOrderInfo.class, order))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserConsultOrderInfo> findList(ConsultListGoIn param) {
        return userConsultOrderMapper.findList(param);
    }

    private UserConsultOrderSearchParam buildSearchParam(ConsultListGoIn goIn) {
        UserConsultOrderSearchParam param = new UserConsultOrderSearchParam();
        param.setOrgIdList(goIn.getOrgIdList());
        param.setConsultNo(goIn.getConsultNo());
        if (goIn.getConsultStatus() != null) {
            param.setOrderStatus(goIn.getConsultStatus().byteValue());
        }
        param.setOperatorMid(goIn.getOperatorMid());
        param.setKey(goIn.getKey());
        param.setPageOffset(goIn.getPageOffset());
        param.setPageSize(goIn.getPageSize());
        param.setConsultType(goIn.getConsultType());
        param.setVin(goIn.getVin());
        param.setHandleResult(goIn.getHandleResult());
        param.setUrgencyLevel(goIn.getUrgencyLevel());
        param.setCreateTimeStart(goIn.getCreateTimeStart());
        param.setCreateTimeEnd(goIn.getCreateTimeEnd());
        param.setFinishTimeStart(goIn.getFinishTimeStart());
        param.setFinishTimeEnd(goIn.getFinishTimeEnd());
        return param;
    }
}
