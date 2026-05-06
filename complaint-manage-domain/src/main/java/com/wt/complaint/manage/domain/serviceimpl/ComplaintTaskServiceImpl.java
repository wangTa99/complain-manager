package com.wt.complaint.manage.domain.serviceimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.wt.complaint.manage.api.model.enums.*;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintOrderRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintTagGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintTagListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.OrderListGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.ComplaintTaskService;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintTagSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.task.TimeOutTagTaskSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.task.TimeOutTagTaskSoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.nr.common.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComplaintTaskServiceImpl implements ComplaintTaskService {
    @Resource
    private ComplaintOrderRepositoryGateway complaintOrderRepositoryGateway;

    @Resource
    private ComplaintTagGateway complaintTagGateway;

    @Resource
    private DeliverComplaintGateway deliverComplaintGateway;

    @Value("${spring.profiles.active}")
    private String profile;

    private static final String SUCCESS_TIP = "success";

    @Override
    public TimeOutTagTaskSoOut syncTimeOutTag(TimeOutTagTaskSoIn soIn) {
        deliverRetailSyncTimeOutTag(soIn);
        return serviceSyncTimeOutTag(soIn);
    }

    /**
     * 服务客诉首响、结案超时更新标�?
     * @param soIn
     * @return
     */
    public TimeOutTagTaskSoOut serviceSyncTimeOutTag(TimeOutTagTaskSoIn soIn) {
        log.info("serviceSyncTimeOutTag:{}", GsonUtil.toJson(soIn));
        OrderListGoIn listGoIn = new OrderListGoIn();
        List<ComplaintTagSoIn> insertList = new ArrayList<>();
        listGoIn.setComplaintStatusList(ComplaintStatusEnum.getTagNeedStatus());
        if (CollUtil.isNotEmpty(soIn.getComplaintNoList())) {
            listGoIn.setComplaintNoList(soIn.getComplaintNoList());
        }
        if (StringUtils.isNotEmpty(soIn.getOrgId())) {
            listGoIn.setOrgId(soIn.getOrgId());
        }
        List<ComplaintOrderInfoGoIn> orderList = complaintOrderRepositoryGateway.findList(listGoIn);
        Map<String, List<ComplaintOrderInfoGoIn>> stringListMap = groupOrderList(orderList);
        List<ComplaintTagSoIn> complaintTagSoIns = dealWithWaitFirstResp(stringListMap.get(TagTypeEnum.FIRST_RESPONSE_TIMEOUT.getCode()));
        List<ComplaintTagSoIn> complaintTagSoIns1 = dealWithFinishResp(stringListMap.get(TagTypeEnum.FINISH_TIMEOUT.getCode()));
        insertList.addAll(complaintTagSoIns);
        insertList.addAll(complaintTagSoIns1);
        // 将数�?00个一组插入到数据�?
        List<List<ComplaintTagSoIn>> split = ListUtil.split(insertList, 100);
        log.info("开始更新标签，总共有：{}�?, insertList.size());
        log.info("首响超时：{}�?, GsonUtil.toJson(complaintTagSoIns.stream().map(e->e.getComplaintNo()).collect(Collectors.toList())));
        log.info("结案超时：{}�?, GsonUtil.toJson(complaintTagSoIns1.stream().map(e->e.getComplaintNo()).collect(Collectors.toList())));
        for (List<ComplaintTagSoIn> tagSoIns : split) {
            Boolean b = complaintTagGateway.batchInsertTag(tagSoIns);
            if (!b) {
                log.warn("本次批量插入标签失败:{}", tagSoIns);
            }
        }
        return TimeOutTagTaskSoOut.builder().result(SUCCESS_TIP).build();
    }

    /**
     * 交付、零售客诉首响、结案超时更新标�?
     * @param soIn 入参
     */
    public TimeOutTagTaskSoOut deliverRetailSyncTimeOutTag(TimeOutTagTaskSoIn soIn) {
        OrderListGoIn listGoIn = new OrderListGoIn();
        List<Integer> retailTagNeedStatus = RetailComplaintOrderStatusEnum.getTagNeedStatus();
        List<Integer> deliverTagNeedStatus = DeliverComplaintOrderStatusEnum.getTagNeedStatus();
        Set<Integer> tagNeedStatus = new HashSet<>();
        tagNeedStatus.addAll(retailTagNeedStatus);
        tagNeedStatus.addAll(deliverTagNeedStatus);
        log.info("deliverSyncTimeOutTag:{}, tagNeedStatus:{}", GsonUtil.toJson(soIn), tagNeedStatus);
        listGoIn.setComplaintStatusList(new ArrayList<>(tagNeedStatus));
        if (CollUtil.isNotEmpty(soIn.getComplaintNoList())) {
            listGoIn.setComplaintNoList(soIn.getComplaintNoList());
        }
        if (StringUtils.isNotEmpty(soIn.getOrgId())) {
            listGoIn.setOrgId(soIn.getOrgId());
        }

        List<String> firstResponseOrderList = deliverComplaintGateway.selectFirstResponseTimeOutTagList(listGoIn);
        log.info("开始更新交付、零售首响超时标签，总共有：{}�?, firstResponseOrderList.size());
        firstResponseOrderList.forEach(drNo -> {
            DeliverComplaintBO deliverComplaintBO = new DeliverComplaintBO();
            deliverComplaintBO.setDrNo(drNo);
            deliverComplaintBO.setFirstResponseTag(1);
            deliverComplaintGateway.updateByDrNo(deliverComplaintBO);
        });

        List<String> finishOrderList = deliverComplaintGateway.selectFinishTimeOutTagList(listGoIn);
        log.info("开始更新交付、零售结案超时标签，总共有：{}�?, finishOrderList.size());
        finishOrderList.forEach(drNo -> {
            DeliverComplaintBO deliverComplaintBO = new DeliverComplaintBO();
            deliverComplaintBO.setDrNo(drNo);
            deliverComplaintBO.setFinishTag(1);
            deliverComplaintGateway.updateByDrNo(deliverComplaintBO);
        });
        return TimeOutTagTaskSoOut.builder().result(SUCCESS_TIP).build();
    }

    // 区分需要判断首响超时与结案超时的工�?
    private Map<String, List<ComplaintOrderInfoGoIn>> groupOrderList(List<ComplaintOrderInfoGoIn> orderList) {
        Map<String, List<ComplaintOrderInfoGoIn>> tempMap = new HashMap<>();
        List<ComplaintOrderInfoGoIn> waitFirstResp = orderList.stream().filter(e -> ComplaintStatusEnum.getBeforeFirstRespStatus().contains(e.getStatus())).collect(Collectors.toList());
        List<ComplaintOrderInfoGoIn> waitFinishResp = orderList.stream().filter(e -> ComplaintStatusEnum.getWaitJudgeTimeOutFinishStatus().contains(e.getStatus())).collect(Collectors.toList());
        List<ComplaintOrderInfoGoIn> temp1 = selectNeedResp(waitFirstResp, TagTypeEnum.FIRST_RESPONSE_TIMEOUT.getCode());
        List<ComplaintOrderInfoGoIn> temp2 = selectNeedResp(waitFinishResp, TagTypeEnum.FINISH_TIMEOUT.getCode());
        tempMap.put(TagTypeEnum.FIRST_RESPONSE_TIMEOUT.getCode(), temp1);
        tempMap.put(TagTypeEnum.FINISH_TIMEOUT.getCode(), temp2);
        return tempMap;
    }

    private List<ComplaintOrderInfoGoIn> selectNeedResp(List<ComplaintOrderInfoGoIn> orderListGoIn, String tagTypeCode) {
        List<ComplaintOrderInfoGoIn> needFirstRespList = new ArrayList<>();
        List<String> collect = orderListGoIn.stream().map(e -> e.getComplaintNo()).collect(Collectors.toList());
        ComplaintTagListGoIn tagListGoIn = ComplaintTagListGoIn.builder().complaintNoList(collect).build();
        List<ComplaintTagGoOut> tagList = complaintTagGateway.getComplaintTagByComplaintNo(tagListGoIn);
        Map<String, List<ComplaintTagGoOut>> tagMap = tagList.stream().collect(Collectors.groupingBy(ComplaintTagGoOut::getComplaintNo));
        for (ComplaintOrderInfoGoIn s : orderListGoIn) {
            if (tagMap.containsKey(s.getComplaintNo())) {
                List<ComplaintTagGoOut> complaintTagGoOuts = tagMap.get(s.getComplaintNo());
                boolean contains = complaintTagGoOuts.stream().map(e -> e.getTagType()).collect(Collectors.toList()).contains(tagTypeCode);
                boolean free72H = false;
                if (tagTypeCode.equals(TagTypeEnum.FINISH_TIMEOUT.getCode())) {
                    free72H = complaintTagGoOuts.stream().map(e -> e.getTagType()).collect(Collectors.toList()).contains(TagTypeEnum.FINISH_72H_ASSESSMENT_FREE.getCode());
                }
                if (contains || free72H) {
                    continue;
                }
            }
            needFirstRespList.add(s);
        }
        return needFirstRespList;
    }

    private List<ComplaintTagSoIn> dealWithWaitFirstResp(List<ComplaintOrderInfoGoIn> orderList) {
        List<ComplaintTagSoIn> tagList = new ArrayList<>();
        List<ComplaintOrderInfoGoIn> allList = new ArrayList<>();
        List<ComplaintOrderInfoGoIn> lowLevelList = orderList.stream().filter(e -> RiskLevelEnum.getLowLevel().contains(e.getRiskLevel())).filter(e-> DateUtil.timeDiff(e.getCreateTime(), new Date(), RiskLevelEnum.LEVEL_1.getDelayHours())).collect(Collectors.toList());
        List<ComplaintOrderInfoGoIn> highLevelList = orderList.stream().filter(e -> RiskLevelEnum.getHighLevel().contains(e.getRiskLevel())).filter(e-> DateUtil.timeDiff(e.getCreateTime(), new Date(), RiskLevelEnum.LEVEL_3.getDelayHours())).collect(Collectors.toList());
        allList.addAll(lowLevelList);
        allList.addAll(highLevelList);
        for (ComplaintOrderInfoGoIn s : allList) {
            ComplaintTagSoIn complaintTagSoIn = ComplaintTagSoIn.builder().complaintNo(s.getComplaintNo()).tagType(TagTypeEnum.FIRST_RESPONSE_TIMEOUT.getCode()).build();
            tagList.add(complaintTagSoIn);
        }
        return tagList;
    }

    private List<ComplaintTagSoIn> dealWithFinishResp(List<ComplaintOrderInfoGoIn> orderList) {
        List<ComplaintTagSoIn> tagList = new ArrayList<>();
        List<ComplaintOrderInfoGoIn> allList = new ArrayList<>();
        List<ComplaintOrderInfoGoIn> finishList = orderList.stream().filter(e-> DateUtil.timeDiff(e.getCreateTime(), new Date(), 72)).collect(Collectors.toList());
        for (ComplaintOrderInfoGoIn s : finishList) {
            ComplaintTagSoIn complaintTagSoIn = ComplaintTagSoIn.builder().complaintNo(s.getComplaintNo()).tagType(TagTypeEnum.FINISH_TIMEOUT.getCode()).build();
            tagList.add(complaintTagSoIn);
        }
        return tagList;
    }

}
