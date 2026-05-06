package com.wt.complaint.manage.infrastructure.gatewayimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.wt.commons.utils.StringUtils;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintDetailGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintUpdateGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverStatisticsItemGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.PageGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.bo.DeliverStatisticsItemBO;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.converter.OrderConverter;
import com.wt.complaint.manage.infrastructure.mapper.DeliverComplaintMapper;
import com.wt.complaint.manage.infrastructure.model.DeliverComplaintDO;
import com.wt.nr.common.utils.GsonUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzheyang
 * @date 2024/12/24
 */
@Slf4j
@Service
public class DeliverComplaintGatewayImpl implements DeliverComplaintGateway {

    @Resource
    private DeliverComplaintMapper deliverComplaintMapper;

    @Override
    public DeliverStatisticsItemBO selectStatisticsItems(DeliverStatisticsItemGoIn goIn) {
        return deliverComplaintMapper.selectStatisticsItems(goIn);
    }

    @Override
    public List<DeliverComplaintBO> selectListByCondition(DeliverComplaintListGoIn goIn) {
        log.info("selectListByCondition goIn:{}", GsonUtil.toJson(goIn));
        List<DeliverComplaintDO> complaintDOList = deliverComplaintMapper.selectListByCondition(goIn);
        log.info("selectListByCondition data:{}", GsonUtil.toJson(complaintDOList));
        return Convert.convert(new TypeReference<List<DeliverComplaintBO>>() {}, complaintDOList);
    }

    @Override
    public Long selectCountByCondition(DeliverComplaintListGoIn goIn) {
        return deliverComplaintMapper.selectCountByCondition(goIn);
    }

    @Override
    public DeliverComplaintBO selectDetail(DeliverComplaintDetailGoIn goIn) {
        DeliverComplaintDO complaintDO = deliverComplaintMapper.selectDetail(goIn);
        return Convert.convert(DeliverComplaintBO.class, complaintDO);
    }

    /**
     * 根据客诉单号查询单个客诉�?
     *
     * @param drNo 客诉单号
     * @return 客诉单业务对�?
     */
    @Override
    public DeliverComplaintBO selectByDrNo(String drNo) {
        DeliverComplaintDO complaintDO = deliverComplaintMapper.selectByDrNo(drNo);
        if (complaintDO == null) {
            return null;
        }
        return Convert.convert(DeliverComplaintBO.class, complaintDO);
    }

    /**
     * 根据客诉单号列表查询多个客诉�?
     *
     * @param drNoList 客诉单号列表
     * @return 客诉单业务对象列�?
     */
    @Override
    public List<DeliverComplaintBO> selectByDrNoList(List<String> drNoList) {
        log.info("selectByDrNoList, drNoList: {}", GsonUtil.toJson(drNoList));
        if (drNoList == null || drNoList.isEmpty()) {
            log.warn("selectByDrNoList drNoList is empty");
            return new ArrayList<>();
        }
        List<DeliverComplaintDO> complaintDOList = deliverComplaintMapper.selectByDrNoList(drNoList);
        return Convert.convert(new TypeReference<List<DeliverComplaintBO>>() {}, complaintDOList);
    }

    @Override
    public List<DeliverComplaintBO> selectByStNoList(List<String> stNoList) {
       log.info("selectByStNoList, stNoList: {}", GsonUtil.toJson(stNoList));
       if (CollectionUtils.isEmpty(stNoList)) {
           log.warn("selectByStNoList stNoList is empty");
           return new ArrayList<>();
       }
       List<DeliverComplaintDO> complaintDOList = deliverComplaintMapper.selectByStNoList(stNoList);
        log.info("DeliverComplaintGatewayImpl#selectByStNoList complaintDOList:{}",
                GsonUtil.toJson(complaintDOList));
       return Convert.convert(new TypeReference<List<DeliverComplaintBO>>() {}, complaintDOList);
    }

    @Override
    public void updateOrderStatus(String drNo, Integer orderStatus) {
        log.info("updateOrderStatus, drNo: {}, orderStatus: {}", drNo, orderStatus);
        if (drNo == null || orderStatus == null) {
            return;
        }
        deliverComplaintMapper.updateOrderStatus(drNo, orderStatus);
    }

    @Override
    public void updateByDrNo(DeliverComplaintBO bo) {
        log.info("updateByDrNo, bo: {}", bo);
        if (bo == null) {
            return;
        }
        if (bo.getDrNo() == null) {
            log.error("updateByDrNo drNo is null, bo: {}", bo);
            throw new BusinessException(ErrorCodeEnums.VALIDATE_ERROR, "客诉单号不能为空");
        }
        DeliverComplaintDO complaintDO = Convert.convert(DeliverComplaintDO.class, bo);
        deliverComplaintMapper.updateByDrNo(complaintDO);
    }

    @Override
    public int batchUpdateByDrNo(List<DeliverComplaintUpdateGoIn> updateParamList) {
        return deliverComplaintMapper.batchUpdateByDrNo(updateParamList);
    }

    @Override
    public List<DeliverComplaintListGoOut> selectFirstResponseToTimeoutList() {
        List<DeliverComplaintDO> deliverComplaintList = deliverComplaintMapper.selectFirstResponseToTimeoutList();
        log.info("deliverComplaintMapper.selectFirstResponseToTimeoutList success, deliverComplaintList={}", RetailJsonUtil.toJson(deliverComplaintList));
        return OrderConverter.INSTANCE.deliverToComplaintGoOutList(deliverComplaintList);
    }

    @Override
    public List<DeliverComplaintListGoOut> selectFinishToTimeoutList() {
        List<DeliverComplaintDO> deliverComplaintList = deliverComplaintMapper.selectFinishToTimeoutList();
        log.info("deliverComplaintMapper.selectFinishToTimeoutList success, deliverComplaintList={}", RetailJsonUtil.toJson(deliverComplaintList));
        return OrderConverter.INSTANCE.deliverToComplaintGoOutList(deliverComplaintList);
    }

    @Override
    public List<String> selectFirstResponseTimeOutTagList(OrderListGoIn listGoIn) {
        List<String> hasList = deliverComplaintMapper.selectHasFirstResponseTimeOutTagList(OrderConverter.INSTANCE.toOrderListParam(listGoIn));
        List<String> notHasList = deliverComplaintMapper.selectNotHasFirstResponseTimeOutTagList(OrderConverter.INSTANCE.toOrderListParam(listGoIn));
        log.info("deliverComplaintMapper.selectFirstResponseTimeOutTagList success, hasList={}, notHasList={}", hasList, notHasList);
        hasList.addAll(notHasList);
        return hasList;
    }

    @Override
    public List<String> selectFinishTimeOutTagList(OrderListGoIn listGoIn) {
        List<String> hasList = deliverComplaintMapper.selectHasFinishTimeOutTagList(OrderConverter.INSTANCE.toOrderListParam(listGoIn));
        List<String> notHasList = deliverComplaintMapper.selectNotHasFinishTimeOutTagList(OrderConverter.INSTANCE.toOrderListParam(listGoIn));
        log.info("deliverComplaintMapper.selectFinishTimeOutTagList success, hasList={}, notHasList={}", hasList, notHasList);
        hasList.addAll(notHasList);
        return hasList;
    }

    @Override
    public List<DeliverComplaintBO> selectEmptyComplaintScene() {
        List<DeliverComplaintDO> complaintDO = deliverComplaintMapper.selectEmptyComplaintScene();
        return Convert.toList(DeliverComplaintBO.class, complaintDO);
    }

    @Override
    public void updateComplaintSceneByDrNo(List<DeliverComplaintBO> list) {
        log.info("updateComplaintSceneByDrNo, list: {}", list);
        if (CollUtil.isEmpty(list)) {
            log.warn("updateComplaintSceneByDrNo list is empty");
            return;
        }

        // 过滤掉drNo为空的记�?
        List<DeliverComplaintBO> validList = list.stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getDrNo())).collect(Collectors.toList());

        if (validList.isEmpty()) {
            log.warn("updateComplaintSceneByDrNo no valid records to update");
            return;
        }

        // 转换为DO对象
        List<DeliverComplaintDO> updateList = Convert.convert(new TypeReference<List<DeliverComplaintDO>>() {}, validList);

        try {
            int updateCount = 0;
            List<List<DeliverComplaintDO>> batches = CollUtil.split(updateList, 50);
            for (List<DeliverComplaintDO> batch : batches) {
                updateCount += deliverComplaintMapper.batchUpdateComplaintSceneByDrNo(batch);
            }
            log.info("updateComplaintSceneByDrNo success, updateCount: {}", updateCount);
        } catch (Exception e) {
            log.error("updateComplaintSceneByDrNo failed, list: {}", GsonUtil.toJson(validList), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "批量更新投诉场景失败");
        }
    }

    @Override
    public List<DeliverComplaintBO> selectByPageGoIn(PageGoIn pageGoIn) {
        List<DeliverComplaintDO> complaintDO = deliverComplaintMapper.selectByPageGoIn(pageGoIn);
        List<DeliverComplaintBO> list = Convert.toList(DeliverComplaintBO.class, complaintDO);
        log.info("gatewayimpl#DeliverComplaintGatewayImpl.selectByPageGoIn req:{} resp:{}", GsonUtil.toJson(pageGoIn)
                , GsonUtil.toJson(list));
        return list;
    }

    @Override
    public void updateCityZoneIdByDrNo(List<DeliverComplaintBO> list) {
        log.info("updateCityZoneIdByDrNo, list: {}", list);
        if (CollUtil.isEmpty(list)) {
            log.warn("updateCityZoneIdByDrNo list is empty");
            return;
        }

        // 仅保留drNo非空且cityZoneId非空的记�?
        List<DeliverComplaintBO> validList = list.stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getDrNo()))
                .collect(Collectors.toList());

        if (validList.isEmpty()) {
            log.warn("updateCityZoneIdByDrNo no valid records to update");
            return;
        }

        // 转换为DO对象
        List<DeliverComplaintDO> updateList = Convert.convert(new TypeReference<List<DeliverComplaintDO>>() {}, validList);

        try {
            int updateCount = 0;
            List<List<DeliverComplaintDO>> batches = CollUtil.split(updateList, 50);
            for (List<DeliverComplaintDO> batch : batches) {
                updateCount += deliverComplaintMapper.batchUpdateCityZoneIdByDrNo(batch);
            }
            log.info("updateCityZoneIdByDrNo success, updateCount: {}", updateCount);
        } catch (Exception e) {
            log.error("updateCityZoneIdByDrNo failed, list: {}", GsonUtil.toJson(validList), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "批量更新城市区域ID失败");
        }
    }
}
