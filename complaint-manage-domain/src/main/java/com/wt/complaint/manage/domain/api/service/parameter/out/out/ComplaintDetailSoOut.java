package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintTagGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ComplaintDetailSoOut {
    /**
     * 客诉单号
     */
    private String complaintNo;

    /**
     * 客诉标签列表
     * 投诉率免考核（COMPLAINT_RATE_ASSESSMENT_FREE�?
     * 72H无法结案(FINISH_72H_ASSESSMENT_FREE)
     * 首响超时(FIRST_RESPONSE_TIMEOUT)
     * 结案超时(FINISH_TIMEOUT)
     */
    private List<String> complaintTagList;

    /**
     * 客诉单状�?
     * 1-待接�?PENDING_ORDER
     * 2-申请改派门店待审�?ORG_REASSIGN_PENDING
     * 3-待首�?FIRST_RESPONSE_PENDING
     * 4-待申请结�?APPLY_FINISH_PENDING
     * 5-待结案评�?FINISH_EVALUATION_PENDING
     * 6-结案完成-FINISH_COMPLETE
     */
    private Integer complaintStatus;

    /**
     * 投诉类型: 1-产品投诉, 2-服务投诉, 3-产品风险
     */
    private Integer complaintType;

    /**
     * 交付零售客诉单状�?0-初始�?10-待首�?15-待改派完�?20-跟进�?30-待结案完�?50-已结�?
     */
    private Integer deliverRetailComplaintStatus;

    /**
     * 跟进客服名称
     */
    private String customerServiceName;

    /**
     * 跟进客服邮箱前缀
     */
    private String customerServiceEmail;

    /**
     * 门店id
     */
    private String orgId;

    /**
     * 门店名称
     */
    private String orgName;

    /**
     * 处理�?
     */
    private String handleName;
    /**
     * 创建�?
     */
    private String createName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 结案时间
     */
    private String finishTime;
    /**
     * 客诉详情信息列表
     */
    private List<TemplateStructSoOut> complaintDetailInfos;

    public void fillBaseInfo(ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        this.complaintNo = complaintOrderInfoGoIn.getComplaintNo();
        this.orgId = complaintOrderInfoGoIn.getOrgId();
        this.complaintStatus = complaintOrderInfoGoIn.getStatus();
        this.complaintType = complaintOrderInfoGoIn.getComplaintType();
        this.deliverRetailComplaintStatus = complaintOrderInfoGoIn.getDeliverRetailComplaintStatus();
        this.createTime = DateUtil.getTimeStrByDate(complaintOrderInfoGoIn.getCreateTime());
        this.finishTime = DateUtil.isDefaultTime(complaintOrderInfoGoIn.getFinishTime()) ? null : DateUtil.getTimeStrByDate(complaintOrderInfoGoIn.getFinishTime());
    }

    public void fillComplaintTag(List<ComplaintTagGoOut> complaintTagGoOutList, ComplaintOrderInfoGoIn complaintOrderInfoGoIn) {
        if (CollUtils.isEmpty(complaintTagGoOutList)) {
            log.info("fillComplaintTag complaintTagGoOutList is empty");
            return;
        }
        Map<String, List<ComplaintTagGoOut>> complaintTagMap = complaintTagGoOutList.stream().collect(Collectors.groupingBy(e -> e.getComplaintNo()));
        List<ComplaintTagGoOut> complaintTagGoOuts = complaintTagMap.get(complaintOrderInfoGoIn.getComplaintNo());
        List<String> tagList = Optional.ofNullable(complaintTagGoOuts).orElse(new ArrayList<>()).stream().map(
                ComplaintTagGoOut::getTagType).collect(Collectors.toList());
        this.complaintTagList = tagList;
    }

    public void fillStoreUserInfo(ComplaintOrderInfoGoIn orderInfo, List<EmployeeInfoGoOut> employeeInfoList, StoreInfoGoOut storeInfo) {
        this.orgName = Objects.nonNull(storeInfo) ? storeInfo.getOrgName() : "";
        if (CollUtils.isEmpty(employeeInfoList)) {
            log.warn("工单处理人信息为�?);
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap = employeeInfoList.stream().collect(Collectors.toMap(
                EmployeeInfoGoOut::getMiId, e -> e, (k1, k2) -> k1));
        this.customerServiceName = employeeMap.containsKey(orderInfo.getCustomerServiceMid()) ? employeeMap.get(orderInfo.getCustomerServiceMid()).getName() : "";
        this.customerServiceEmail = employeeMap.containsKey(orderInfo.getCustomerServiceMid()) ? employeeMap.get(orderInfo.getCustomerServiceMid()).getEmailPrefix() : "";
        this.handleName = employeeMap.containsKey(orderInfo.getOperatorMid()) ? employeeMap.get(orderInfo.getOperatorMid()).getName() : "";
        this.createName = employeeMap.containsKey(orderInfo.getCreateMid()) ? employeeMap.get(orderInfo.getCreateMid()).getName() : "";
    }

    public void fillDetailInfo(List<TemplateStructSoIn> complaintStructList, List<FileInfoGoOut> fileInfoList) {
        if (CollUtils.isEmpty(complaintStructList)) {
            return;
        }
        List<TemplateStructSoOut> templateStructSoOut = OrderViewConverter.INSTANCE.toTemplateStructSoOut(complaintStructList);

        Map<Long, FileInfoGoOut> fileMap = Optional.ofNullable(fileInfoList).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(e -> e.getFileId(), e -> e, (k1, k2) -> k1));
        for (TemplateStructSoOut structSoOut : templateStructSoOut) {
            // 补充附件url信息
            for (DetailFieldSoOut field : structSoOut.getFields()) {
                if (CollUtils.isNotEmpty(field.getAttachments())) {
                    field.getAttachments().stream().forEach(
                        e -> {
                            e.setUrl(fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileUrl() : "");
                            e.setFileName(fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileName() : "");
                        }
                    );
                }
            }
        }
        this.complaintDetailInfos = templateStructSoOut;
    }
}