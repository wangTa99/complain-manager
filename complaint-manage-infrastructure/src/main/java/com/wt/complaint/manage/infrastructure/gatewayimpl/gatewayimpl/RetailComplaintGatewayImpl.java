package com.wt.complaint.manage.infrastructure.gatewayimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.enums.RetailTabEnum;
import com.wt.complaint.manage.api.model.resp.retail.RetailComplaintListSearchInfo;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.*;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.BubbleCountGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintListSearchGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailHasFirstResposeRecordFlagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StaticTabCountGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.*;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.exception.ErrorCodeEnums;
import com.wt.complaint.manage.infrastructure.converter.OrderConverter;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintDO;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintListSearchDetailDO;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.infrastructure.mapper.RetailComplaintMapper;
import com.wt.complaint.manage.infrastructure.model.RetailComplaintDetailDO;
import com.wt.complaint.manage.infrastructure.model.RetailHasFirstResposeRecordFlagDO;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 零售投诉视图服务相关接口
 *
 * @author p-wangkai95
 * @version 1.0
 */
@Slf4j
@Service
public class RetailComplaintGatewayImpl implements RetailComplaintGateway {

    @Autowired
    private RetailComplaintMapper retailComplaintMapper;

    @Resource
    private MoneThreadPoolExecutor retailComplaintOrderListExecutor;

    /**
     * 保存投诉信息
     *
     * @param retailComplaintOrderInfoGoIn 投诉信息请求参数
     * @return true:保存成功 false:保存失败
     */
    @Override
    public boolean saveComplaintInfo(RetailComplaintOrderInfoGoIn retailComplaintOrderInfoGoIn) {
        RetailComplaintDO retailComplaintDO = Convert.convert(RetailComplaintDO.class, retailComplaintOrderInfoGoIn);
        int result = retailComplaintMapper.insertSelective(retailComplaintDO);
        return result > 0;
    }

    /**
     * 获取气泡数量
     *
     * @param staticRetailCountGoIn 气泡请求参数
     * @return 气泡数量响应参数
     */
    @Override
    public BubbleCountGoOut getBubbleCount(StaticRetailCountGoIn staticRetailCountGoIn) {
        BubbleCountGoOut bubbleCountGoOut = new BubbleCountGoOut();
        List<CompletableFuture<Void>> completableFutureList = Lists.newArrayList();
        // 催办气泡数量
        completableFutureList.add(CompletableFuture.runAsync(
                () -> bubbleCountGoOut.setRemindCount(retailComplaintMapper.staticRetailCount(
                        StaticRetailCountGoIn.builder().orgId(staticRetailCountGoIn.getOrgId())
                                .tab(RetailTabEnum.REMIND.getCode()).afterSaleWorkbenchPermissionGroup(
                                        staticRetailCountGoIn.getAfterSaleWorkbenchPermissionGroup()).build())),
                retailComplaintOrderListExecutor));
        // 待首响气泡数�?
        completableFutureList.add(CompletableFuture.runAsync(() -> bubbleCountGoOut.setFirstResponsePendingCount(
                        retailComplaintMapper.staticRetailCount(
                                StaticRetailCountGoIn.builder().orgId(staticRetailCountGoIn.getOrgId())
                                        .tab(RetailTabEnum.FIRST_RESPONSE_PENDING.getCode()).afterSaleWorkbenchPermissionGroup(
                                                staticRetailCountGoIn.getAfterSaleWorkbenchPermissionGroup()).build())),
                retailComplaintOrderListExecutor));
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
        return bubbleCountGoOut;
    }

    @Override
    public StaticTabCountGoOut staticTabCount(StaticRetailCountGoIn retailCountGoIn) {
        log.info("RetailComplaintGatewayImpl.staticTabCount start, retailCountGoIn:{}",
                RetailJsonUtil.toJson(retailCountGoIn));
        CompletableFuture<StaticTabCountGoOut.TabData> firstResponseFuture =
                CompletableFuture.supplyAsync(
                        () -> StaticTabCountGoOut.TabData.builder()
                                .tab(RetailTabEnum.FIRST_RESPONSE_PENDING.getCode())
                                .count(retailComplaintMapper.staticRetailCount(
                                        StaticRetailCountGoIn.builder().zoneId(retailCountGoIn.getZoneId())
                                                .littleZoneId(retailCountGoIn.getLittleZoneId())
                                                .orgId(retailCountGoIn.getOrgId())
                                                .tab(RetailTabEnum.FIRST_RESPONSE_PENDING.getCode())
                                                .contactPhoneMd5(retailCountGoIn.getContactPhoneMd5())
                                                .drNo(retailCountGoIn.getDrNo())
                                                .afterSaleWorkbenchPermissionGroup(
                                                        retailCountGoIn.getAfterSaleWorkbenchPermissionGroup())
                                                .build()))
                                .build(),
                        retailComplaintOrderListExecutor);
        CompletableFuture<StaticTabCountGoOut.TabData> inProgressFuture =
                CompletableFuture.supplyAsync(
                        () -> StaticTabCountGoOut.TabData.builder()
                                .tab(RetailTabEnum.IN_PROGRESS.getCode())
                                .count(retailComplaintMapper.staticRetailCount(
                                        StaticRetailCountGoIn.builder().zoneId(retailCountGoIn.getZoneId())
                                                .littleZoneId(retailCountGoIn.getLittleZoneId())
                                                .orgId(retailCountGoIn.getOrgId())
                                                .tab(RetailTabEnum.IN_PROGRESS.getCode())
                                                .contactPhoneMd5(retailCountGoIn.getContactPhoneMd5())
                                                .drNo(retailCountGoIn.getDrNo())
                                                .afterSaleWorkbenchPermissionGroup(
                                                        retailCountGoIn.getAfterSaleWorkbenchPermissionGroup())
                                                .build()))
                                .build(),
                        retailComplaintOrderListExecutor);
        CompletableFuture<StaticTabCountGoOut.TabData> approachingTimeoutFuture =
                CompletableFuture.supplyAsync(
                        () -> StaticTabCountGoOut.TabData.builder()
                                .tab(RetailTabEnum.APPROACHING_TIMEOUT.getCode())
                                .count(retailComplaintMapper.staticRetailCount(
                                        StaticRetailCountGoIn.builder().zoneId(retailCountGoIn.getZoneId())
                                                .littleZoneId(retailCountGoIn.getLittleZoneId())
                                                .orgId(retailCountGoIn.getOrgId())
                                                .tab(RetailTabEnum.APPROACHING_TIMEOUT.getCode())
                                                .contactPhoneMd5(retailCountGoIn.getContactPhoneMd5())
                                                .drNo(retailCountGoIn.getDrNo())
                                                .afterSaleWorkbenchPermissionGroup(
                                                        retailCountGoIn.getAfterSaleWorkbenchPermissionGroup())
                                                .build()))
                                .build(),
                        retailComplaintOrderListExecutor);
        CompletableFuture<StaticTabCountGoOut.TabData> finishCompleteFuture =
                CompletableFuture.supplyAsync(
                        () -> StaticTabCountGoOut.TabData.builder()
                                .tab(RetailTabEnum.FINISH_COMPLETE.getCode())
                                .count(retailComplaintMapper.staticRetailCount(
                                        StaticRetailCountGoIn.builder().zoneId(retailCountGoIn.getZoneId())
                                                .littleZoneId(retailCountGoIn.getLittleZoneId())
                                                .orgId(retailCountGoIn.getOrgId())
                                                .tab(RetailTabEnum.FINISH_COMPLETE.getCode())
                                                .contactPhoneMd5(retailCountGoIn.getContactPhoneMd5())
                                                .drNo(retailCountGoIn.getDrNo())
                                                .afterSaleWorkbenchPermissionGroup(
                                                        retailCountGoIn.getAfterSaleWorkbenchPermissionGroup())
                                                .build()))
                                .build(),
                        retailComplaintOrderListExecutor);

        List<StaticTabCountGoOut.TabData> tabDataList = new ArrayList<>();
        try {
            StaticTabCountGoOut.TabData firstResponse = firstResponseFuture.join();
            tabDataList.add(orDefault(firstResponse, RetailTabEnum.FIRST_RESPONSE_PENDING.getCode(),
                    "firstResponse", retailCountGoIn));

            StaticTabCountGoOut.TabData inProgress = inProgressFuture.join();
            tabDataList.add(orDefault(inProgress, RetailTabEnum.IN_PROGRESS.getCode(),
                    "inProgress", retailCountGoIn));

            StaticTabCountGoOut.TabData approachingTimeout = approachingTimeoutFuture.join();
            tabDataList.add(orDefault(approachingTimeout, RetailTabEnum.APPROACHING_TIMEOUT.getCode(),
                    "approachingTimeout", retailCountGoIn));

            StaticTabCountGoOut.TabData finishComplete = finishCompleteFuture.join();
            tabDataList.add(orDefault(finishComplete, RetailTabEnum.FINISH_COMPLETE.getCode(),
                    "finishComplete", retailCountGoIn));
        } catch (Exception e) {
            log.error("RetailComplaintGatewayImpl.staticTabCount async query failed, retailCountGoIn:{}",
                    RetailJsonUtil.toJson(retailCountGoIn), e);
            throw new BusinessException(ErrorCodeEnums.BUS_ERROR, "tab统计数据查询异常");
        }

        if (CollUtil.isNotEmpty(tabDataList)) {
            tabDataList.sort(Comparator.comparing(StaticTabCountGoOut.TabData::getTab));
        }
        log.info("RetailComplaintGatewayImpl.staticTabCount success, tabDataList:{}",
                RetailJsonUtil.toJson(tabDataList));
        return StaticTabCountGoOut.builder().tabDataList(tabDataList).build();
    }

    /**
     * 如果tabData为null，则使用默认�?
     */
    private StaticTabCountGoOut.TabData orDefault(StaticTabCountGoOut.TabData tabData, Integer tabCode,
                                                  String tabName, StaticRetailCountGoIn retailCountGoIn) {
        if (tabData != null) {
            return tabData;
        }
        log.error("RetailComplaintGatewayImpl.staticTabCount {}Future returned null, use default tab={}, count=0, "
                + "retailCountGoIn:{}", tabName, tabCode, RetailJsonUtil.toJson(retailCountGoIn));
        return StaticTabCountGoOut.TabData.builder().tab(tabCode).count(0).build();
    }

    @Override
    public RetailComplaintListSearchGoOut searchRetailComplaintList(RetailComplaintListSearchGoIn goIn) {
        PageMethod.startPage(goIn.getPageNum(), goIn.getPageSize());
        // 搜索零售投诉列表
        List<RetailComplaintListSearchDetailDO> retailComplaintListSearchDetailDOList =
                retailComplaintMapper.searchRetailComplaintList(goIn);
        // 获取分页信息
        PageInfo<RetailComplaintListSearchDetailDO> pageInfo =
                new PageInfo<>(retailComplaintListSearchDetailDOList);
        // 转换为DTO对象，并收集结果
        List<RetailComplaintListSearchInfo> retailComplaintListSearchInfoList =
                retailComplaintListSearchDetailDOList.stream()
                        .map(detailDO -> Convert.convert(RetailComplaintListSearchInfo.class,
                                detailDO))  // 如果转换失败，返回null
                        .filter(Objects::nonNull)  // 过滤掉null�?
                        .collect(Collectors.toList());
        // 构建输出对象
        return RetailComplaintListSearchGoOut.builder()
                .total(pageInfo.getTotal())
                .dataList(retailComplaintListSearchInfoList)
                .build();
    }

    @Override
    public RetailComplaintDetaiGoOut getRetailComplaintDetail(RetailComplaintDetailGoIn retailComplaintDetailGoIn) {
        RetailComplaintDetailDO retailComplaintDetail =
                retailComplaintMapper.getRetailComplaintDetail(retailComplaintDetailGoIn);
        return Convert.convert(RetailComplaintDetaiGoOut.class, retailComplaintDetail);
    }

    @Override
    public RetailHasFirstResposeRecordFlagGoOut getRetailHasFirstResposeRecordFlag(
            RetailHasFirstResponseRecordFlagGoIn goIn) {
        if (StrUtil.isBlank(goIn.getDrNo())) {
            return RetailHasFirstResposeRecordFlagGoOut.builder().hasFirstResposeRecordFlag(false).build();
        }
        RetailHasFirstResposeRecordFlagDO retailHasFirstResposeRecordFlagDO =
                retailComplaintMapper.getRetailHasFirstResposeRecordFlag(goIn);
        boolean hasFirstResposeRecordFlag = ObjectUtil.isNotNull(retailHasFirstResposeRecordFlagDO) ? Boolean.TRUE :
                Boolean.FALSE;
        return RetailHasFirstResposeRecordFlagGoOut.builder().hasFirstResposeRecordFlag(hasFirstResposeRecordFlag)
                .build();
    }

    @Override
    public Boolean updateOrderByDrNo(UpdateRetailOrderGoIn updateOrderStatusGoIn) {
        log.info("updateOrderStatusGoIn:{}", updateOrderStatusGoIn);
        int result = retailComplaintMapper.updateOrderByDrNo(updateOrderStatusGoIn);
        return result > 0;
    }

    @Override
    public RetailComplaintDetaiGoOut findByIdempotentId(FindByIdempotentIdGoIn findByIdempotentIdGoIn) {
        RetailComplaintDetailDO retailComplaintDetailDO =
                retailComplaintMapper.findByIdempotentKey(findByIdempotentIdGoIn);
        return Convert.convert(RetailComplaintDetaiGoOut.class, retailComplaintDetailDO);
    }

    @Override
    public List<RetailComplaintListGoOut> selectFirstResponseToTimeoutList() {
        List<RetailComplaintDO> retailComplaintList = retailComplaintMapper.selectFirstResponseToTimeoutList();
        log.info("retailComplaintMapper.selectFirstResponseToTimeoutList success, retailComplaintList={}",
                RetailJsonUtil.toJson(retailComplaintList));
        return OrderConverter.INSTANCE.retailToComplaintGoOutList(retailComplaintList);
    }

    @Override
    public List<RetailComplaintListGoOut> selectFinishToTimeoutList() {
        List<RetailComplaintDO> retailComplaintList = retailComplaintMapper.selectFinishToTimeoutList();
        log.info("retailComplaintMapper.selectFinishToTimeoutList success, retailComplaintList={}",
                RetailJsonUtil.toJson(retailComplaintList));
        return OrderConverter.INSTANCE.retailToComplaintGoOutList(retailComplaintList);
    }

}
