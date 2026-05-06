package com.wt.complaint.manage.domain.api.service.parameter.out;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wt.complaint.manage.api.model.enums.ReportOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.ServiceSceneEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.FileInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.converter.OrderViewConverter;
import com.wt.complaint.manage.domain.api.service.parameter.in.TemplateStructSoIn;
import com.wt.complaint.manage.domain.utils.DateUtil;
import com.wt.proretail.newcommon.util.RetailJsonUtil;
import com.xiaomi.mone.docs.annotations.dubbo.ApiDocClassDefine;
import com.xiaomi.newretail.common.tools.utils.CollUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class UserComplaintDetailSoOut implements Serializable {

    @ApiDocClassDefine(value = "举报场景：用,分隔", description = "举报场景：用,分隔")
    private String serviceScene;

    @ApiDocClassDefine(value = "举报单号", description = "举报单号")
    private String ucNo;

    @ApiDocClassDefine(value = "服务单号", description = "服务单号")
    private String soNo;

    @ApiDocClassDefine(value = "工单�?, description = "工单�?)
    private String superTicketNo;

    @ApiDocClassDefine(value = "举报单状�?, description = "举报单状�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private Integer orderStatus;

    @ApiDocClassDefine(value = "举报单状态名�?, description = "举报单状态名�?0-待接�?1-待举报判�?2-已完�?3-已撤销")
    private String orderStatusName;

    @ApiDocClassDefine(value = "创建人姓�?, description = "创建人姓�?)
    private String createName;

    @ApiDocClassDefine(value = "门店id", description = "门店id")
    private String orgId;

    @ApiDocClassDefine(value = "门店名称", description = "门店名称")
    private String orgName;

    @ApiDocClassDefine(value = "处理人mid", description = "处理人mid")
    private Long handleMid;

    @ApiDocClassDefine(value = "处理�?, description = "处理�?)
    private String handleName;

    @ApiDocClassDefine(value = "创建人mid", description = "创建人mid")
    private Long createMid;

    @ApiDocClassDefine(value = "创建时间", description = "创建时间")
    private String createTime;

    @ApiDocClassDefine(value = "完成时间", description = "完成时间")
    private String finishTime;

    @ApiDocClassDefine(value = "举报单判定结�?, description = "举报单判定结�?)
    private Integer judgeType;

    @ApiDocClassDefine(value = "举报单详情信息列�?, description = "举报单详情信息列�?)
    private List<TemplateStructSoOut> userComplaintDetailInfos;

    public void fillBaseInfo(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut) {
        this.ucNo = userComplaintOrderDetailSoOut.getUcNo();
        this.soNo = userComplaintOrderDetailSoOut.getSoNo();
        this.superTicketNo = userComplaintOrderDetailSoOut.getSuperTicketNo();
        this.orgId = userComplaintOrderDetailSoOut.getOrgId();
        this.orderStatus = userComplaintOrderDetailSoOut.getOrderStatus();
        this.createTime = DateUtil.getTimeStrByDate(userComplaintOrderDetailSoOut.getCreateTime());
        this.finishTime = DateUtil.isDefaultTime(userComplaintOrderDetailSoOut.getFinishTime()) ? null :
                DateUtil.getTimeStrByDate(userComplaintOrderDetailSoOut.getFinishTime());
        this.orderStatusName = ReportOrderStatusEnum.getDescByCode(userComplaintOrderDetailSoOut.getOrderStatus());
        if (StrUtil.isNotBlank(userComplaintOrderDetailSoOut.getServiceScene())) {
            String[] serviceSceneCodes = userComplaintOrderDetailSoOut.getServiceScene().split(",");
            List<String> serviceSceneDescs = new ArrayList<>();
            for (String codeStr : serviceSceneCodes) {
                try {
                    int code = Integer.parseInt(codeStr.trim());
                    ServiceSceneEnum sceneEnum = ServiceSceneEnum.getByCode(code);
                    if (sceneEnum != null) {
                        serviceSceneDescs.add(sceneEnum.getDesc());
                    }
                } catch (NumberFormatException e) {
                    log.error("UserComplaintOrderGatewayImpl#fillBasicInfo business error,req:{},e:{}",
                            RetailJsonUtil.toJson(userComplaintOrderDetailSoOut), e.getMessage());
                }
            }
            this.serviceScene = String.join(",", serviceSceneDescs);
        }
    }

    public void fillStoreUserInfo(UserComplaintOrderDetailSoOut userComplaintOrderDetailSoOut,
                                  List<EmployeeInfoGoOut> employeeInfoList,
                                  StoreInfoGoOut storeInfo) {
        this.orgName = Objects.nonNull(storeInfo) ? storeInfo.getOrgName() : "";
        if (CollUtils.isEmpty(employeeInfoList)) {
            log.warn("工单处理人信息为�?);
            return;
        }
        Map<Long, EmployeeInfoGoOut> employeeMap =
                employeeInfoList.stream().collect(Collectors.toMap(EmployeeInfoGoOut::getMiId, e -> e, (k1, k2) -> k1));
        this.handleName = employeeMap.containsKey(userComplaintOrderDetailSoOut.getOperatorMid()) ?
                employeeMap.get(userComplaintOrderDetailSoOut.getOperatorMid()).getName() : "";
        this.createName = employeeMap.containsKey(userComplaintOrderDetailSoOut.getCreateMid()) ?
                employeeMap.get(userComplaintOrderDetailSoOut.getCreateMid()).getName() : "";
    }

    public void fillDetailInfo(List<TemplateStructSoIn> complaintStructList, List<FileInfoGoOut> fileInfoList) {
        if (CollUtils.isEmpty(complaintStructList)) {
            return;
        }
        List<TemplateStructSoOut> templateStructSoOut =
                OrderViewConverter.INSTANCE.toTemplateStructSoOut(complaintStructList);

        Map<Long, FileInfoGoOut> fileMap = Optional.ofNullable(fileInfoList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(FileInfoGoOut::getFileId, e -> e, (k1, k2) -> k1));
        for (TemplateStructSoOut structSoOut : templateStructSoOut) {
            // 补充附件url信息
            for (DetailFieldSoOut field : structSoOut.getFields()) {
                if (CollUtils.isNotEmpty(field.getAttachments())) {
                    field.getAttachments().forEach(
                            e -> {
                                e.setUrl(fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileUrl() : "");
                                e.setFileName(
                                        fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileName() : "");
                            }
                    );
                }
            }
        }
        this.userComplaintDetailInfos = templateStructSoOut;
    }

    /**
     * 补充附件url信息
     * @param fileInfoList
     */
    public void fillDetailInfo(List<FileInfoGoOut> fileInfoList) {
        if (this.getUserComplaintDetailInfos() == null) {
            return;
        }
        Map<Long, FileInfoGoOut> fileMap = Optional.ofNullable(fileInfoList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(FileInfoGoOut::getFileId, e -> e, (k1, k2) -> k1));
        for (TemplateStructSoOut structSoOut : this.getUserComplaintDetailInfos()) {
            // 补充附件url信息
            for (DetailFieldSoOut field : structSoOut.getFields()) {
                if (CollUtil.isNotEmpty(field.getAttachments())) {
                    field.getAttachments().forEach(
                            e -> {
                                e.setUrl(fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileUrl() : "");
                                e.setFileName(
                                        fileMap.containsKey(e.getId()) ? fileMap.get(e.getId()).getFileName() : "");
                            }
                    );
                }
            }
        }
    }
}
