package com.wt.complaint.manage.domain.testutil;

import com.google.common.collect.Lists;
import com.wt.car.soc.api.dto.FieldValue;
import com.wt.car.soc.api.dto.FieldValueDto;
import com.wt.car.soc.api.dto.GroupValueDto;
import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.MediaInvolvedEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.enums.FieldTypeEnum;
import com.wt.complaint.manage.domain.api.enums.PositionEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintOrderUpgradeSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.FieldValueSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderEditComplaintSoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.approve.ComplaintAuditSoOut;
import com.wt.complaint.manage.domain.constant.ComplaintInfoConstant;
import com.wt.complaint.manage.domain.model.ComplaintAuditInfo;
import com.wt.complaint.manage.domain.utils.GsonUtil;
import com.wt.complaint.manage.domain.api.service.parameter.in.OrderAddFollowUpRecordSoInV2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 测试数据构建工具�?
 * 用于构建各种测试需要的数据对象
 *
 * @author zhangzheyang
 * @date 2026/01/28
 */
public class TestDataBuilder {

    /**
     * 构建ComplaintOrderInfoGoIn测试数据
     */
    public static ComplaintOrderInfoGoIn buildComplaintOrderInfoGoIn(String complaintNo, Integer complaintType) {
        ComplaintOrderInfoGoIn goIn = new ComplaintOrderInfoGoIn();
        goIn.setComplaintNo(complaintNo);
        goIn.setComplaintType(complaintType);
        goIn.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
        goIn.setRiskLevel(RiskLevelEnum.LEVEL_1.getCode());
        goIn.setResponsibility(ResponsibilityEnum.YES.getCode());
        goIn.setOrgId("F001");
        goIn.setZoneId("1");
        goIn.setLittleZoneId("10");
        goIn.setCityId("100");
        goIn.setMediaInvolved(MediaInvolvedEnum.NO.getCode());
        goIn.setMediaLink("");
        goIn.setVid("V001");
        goIn.setSuperTicketNo("ST001");
        goIn.setCustomerServiceMid(1001L);
        goIn.setOperatorMid(1001L);
        goIn.setSoNo("SO001");

        // 构建complaint_content JSON
        String complaintContent = buildComplaintContentJson(complaintType, RiskLevelEnum.LEVEL_1.getCode());
        goIn.setComplaintContent(complaintContent);
        
        return goIn;
    }

    /**
     * 构建ComplaintOrderGoOut测试数据
     */
    public static ComplaintOrderGoOut buildComplaintOrderGoOut(String complaintNo, Integer complaintType) {
        ComplaintOrderGoOut goOut = new ComplaintOrderGoOut();
        goOut.setComplaintNo(complaintNo);
        goOut.setComplaintType(complaintType);
        goOut.setStatus(ComplaintStatusEnum.APPLY_FINISH_PENDING.getCode());
        goOut.setRiskLevel(RiskLevelEnum.LEVEL_1.getCode());
        goOut.setResponsibility(ResponsibilityEnum.YES.getCode());
        goOut.setOrgId("F001");
        goOut.setZoneId("1");
        goOut.setLittleZoneId("10");
        goOut.setCityId("100");
        goOut.setMediaInvolved(MediaInvolvedEnum.NO.getCode());
        goOut.setMediaLink("");
        goOut.setVid("V001");
        goOut.setSuperTicketNo("ST001");
        goOut.setCustomerServiceMid(1001L);
        
        // 构建complaint_content JSON
        String complaintContent = buildComplaintContentJson(complaintType, RiskLevelEnum.LEVEL_1.getCode());
        goOut.setComplaintContent(complaintContent);
        
        return goOut;
    }

    /**
     * 构建complaint_content的JSON字符�?
     */
    public static String buildComplaintContentJson(Integer complaintType, Integer riskLevel) {
        GroupValueDto group = new GroupValueDto();
        List<FieldValueDto> fields = new ArrayList<>();
        
        // 投诉分类字段
        FieldValueDto complaintTypeField = new FieldValueDto();
        complaintTypeField.setFieldCode(ComplaintInfoConstant.COMPLAINT_TYPE);
        complaintTypeField.setFieldName("投诉分类");
        complaintTypeField.setFieldType(FieldTypeEnum.OPTION.getCode());
        FieldValue complaintTypeValue = new FieldValue();
        complaintTypeValue.setCode(String.valueOf(complaintType));
        complaintTypeValue.setDesc(ComplaintTypeEnum.getDescByCode(complaintType));
        complaintTypeField.setValue(Lists.newArrayList(complaintTypeValue));
        fields.add(complaintTypeField);
        
        // 投诉场景字段
        FieldValueDto complaintSceneField = new FieldValueDto();
        complaintSceneField.setFieldCode(ComplaintInfoConstant.COMPLAINT_SCENE);
        complaintSceneField.setFieldType(FieldTypeEnum.CASCADE_SELECTION.getCode());
        complaintSceneField.setFieldName("投诉场景");
        FieldValue complaintSceneValue = new FieldValue();
        complaintSceneValue.setCode("SC001");
        complaintSceneValue.setDesc("交车体验");
        complaintSceneValue.setPathId("1/2/3");
        complaintSceneValue.setPathName("交付/交车体验/交车体验");
        complaintSceneField.setValue(Lists.newArrayList(complaintSceneValue));
        fields.add(complaintSceneField);
        
        // 风险等级字段
        FieldValueDto riskLevelField = new FieldValueDto();
        riskLevelField.setFieldCode(ComplaintInfoConstant.RISK_LEVEL);
        riskLevelField.setFieldName("风险等级");
        riskLevelField.setFieldType(FieldTypeEnum.OPTION.getCode());
        FieldValue riskLevelValue = new FieldValue();
        riskLevelValue.setCode(String.valueOf(riskLevel));
        riskLevelValue.setDesc(RiskLevelEnum.getDescByCode(riskLevel));
        riskLevelField.setValue(Lists.newArrayList(riskLevelValue));
        fields.add(riskLevelField);
        
        // 涉媒字段
        FieldValueDto mediaInvolvedField = new FieldValueDto();
        mediaInvolvedField.setFieldCode(ComplaintInfoConstant.MEDIA_INVOLVED);
        mediaInvolvedField.setFieldName("是否涉媒");
        mediaInvolvedField.setFieldType(FieldTypeEnum.OPTION.getCode());
        FieldValue mediaInvolvedValue = new FieldValue();
        mediaInvolvedValue.setCode("0");
        mediaInvolvedValue.setDesc(MediaInvolvedEnum.NO.getDesc());
        mediaInvolvedField.setValue(Lists.newArrayList(mediaInvolvedValue));
        fields.add(mediaInvolvedField);
        
        // 涉媒链接字段
        FieldValueDto mediaLinkField = new FieldValueDto();
        mediaLinkField.setFieldCode(ComplaintInfoConstant.MEDIA_LINK);
        mediaLinkField.setFieldType(FieldTypeEnum.LINK.getCode());
        mediaLinkField.setFieldName("涉媒链接");
        mediaLinkField.setValue(Lists.newArrayList());
        fields.add(mediaLinkField);
        
        group.setFields(fields);
        return GsonUtil.toJson(Lists.newArrayList(group));
    }

    /**
     * 构建ComplaintAuditSoOut测试数据
     */
    public static ComplaintAuditSoOut buildComplaintAuditSoOut(String complaintNo, Integer auditType) {
        ComplaintAuditSoOut soOut = new ComplaintAuditSoOut();
        soOut.setId(1001L);
        soOut.setComplaintNo(complaintNo);
        soOut.setAuditType(auditType);
        soOut.setAuditStatus(AuditStatusEnum.PENDING.getCode());
        soOut.setZoneId("1");
        soOut.setLittleZoneId("10");
        soOut.setCreateMid(1001L);
        soOut.setApplyContent("{}");
        return soOut;
    }

    /**
     * 构建ComplaintAuditInfo测试数据（满意度管理�?
     */
    public static ComplaintAuditInfo buildComplaintAuditInfo_SatisfactionManagement(Long mid) {
        ComplaintAuditInfo info = new ComplaintAuditInfo();
        info.setMid(mid);
        info.setIsSatisfactionManagement(true);
        info.setIsUrbanExperienceExpert(false);
        info.setIsRegionalExperienceExpert(false);
        info.setIsComplaintHandling(false);
        info.setLittleZoneIdList(new ArrayList<>());
        info.setZoneIdList(new ArrayList<>());
        return info;
    }

    /**
     * 构建ComplaintAuditInfo测试数据（城市体验专家）
     */
    public static ComplaintAuditInfo buildComplaintAuditInfo_UrbanExpert(Long mid, List<Integer> littleZoneIds) {
        ComplaintAuditInfo info = new ComplaintAuditInfo();
        info.setMid(mid);
        info.setIsSatisfactionManagement(false);
        info.setIsUrbanExperienceExpert(true);
        info.setIsRegionalExperienceExpert(false);
        info.setIsComplaintHandling(false);
        info.setLittleZoneIdList(littleZoneIds);
        info.setZoneIdList(new ArrayList<>());
        return info;
    }

    /**
     * 构建ComplaintAuditInfo测试数据（区域体验专家）
     */
    public static ComplaintAuditInfo buildComplaintAuditInfo_RegionalExpert(Long mid, List<Integer> zoneIds) {
        ComplaintAuditInfo info = new ComplaintAuditInfo();
        info.setMid(mid);
        info.setIsSatisfactionManagement(false);
        info.setIsUrbanExperienceExpert(false);
        info.setIsRegionalExperienceExpert(true);
        info.setIsComplaintHandling(false);
        info.setLittleZoneIdList(new ArrayList<>());
        info.setZoneIdList(zoneIds);
        return info;
    }

    /**
     * 构建ComplaintAuditInfo测试数据（客诉处理岗位）
     */
    public static ComplaintAuditInfo buildComplaintAuditInfo_ComplaintHandling(Long mid) {
        ComplaintAuditInfo info = new ComplaintAuditInfo();
        info.setMid(mid);
        info.setIsSatisfactionManagement(false);
        info.setIsUrbanExperienceExpert(false);
        info.setIsRegionalExperienceExpert(false);
        info.setIsComplaintHandling(true);
        info.setLittleZoneIdList(new ArrayList<>());
        info.setZoneIdList(new ArrayList<>());
        return info;
    }

    /**
     * 构建ComplaintAuditInfo测试数据（无权限�?
     */
    public static ComplaintAuditInfo buildComplaintAuditInfo_NoPermission(Long mid) {
        ComplaintAuditInfo info = new ComplaintAuditInfo();
        info.setMid(mid);
        info.setIsSatisfactionManagement(false);
        info.setIsUrbanExperienceExpert(false);
        info.setIsRegionalExperienceExpert(false);
        info.setIsComplaintHandling(false);
        info.setLittleZoneIdList(new ArrayList<>());
        info.setZoneIdList(new ArrayList<>());
        return info;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（满意度管理�?
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_SatisfactionManagement() {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        
        // 渠道岗位 - 满意度管�?
        CarEmployeeInfoGoOut.ChannelPositionInfo channelPosition = new CarEmployeeInfoGoOut.ChannelPositionInfo(
                PositionEnum.SATISFACTION_MANAGEMENT.getCode(),
                PositionEnum.SATISFACTION_MANAGEMENT.getName()
        );
        goOut.setChannelPositionInfoList(Lists.newArrayList(channelPosition));
        
        goOut.setLittleZonePositionsInfoList(new ArrayList<>());
        goOut.setBigZonePositionsInfoList(new ArrayList<>());
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        
        return goOut;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（区域体验专家）
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_RegionalExpert(List<Integer> zoneIds) {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        
        // 大区岗位 - 区域体验专家
        List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositions = new ArrayList<>();
        for (Integer zoneId : zoneIds) {
            CarEmployeeInfoGoOut.ZonePositionInfo zonePosition = new CarEmployeeInfoGoOut.ZonePositionInfo(
                    PositionEnum.REGIONAL_EXPERIENCE_EXPERT.getCode(),
                    PositionEnum.REGIONAL_EXPERIENCE_EXPERT.getName(),
                    zoneId,
                    "大区" + zoneId
            );
            bigZonePositions.add(zonePosition);
        }
        goOut.setBigZonePositionsInfoList(bigZonePositions);
        
        goOut.setChannelPositionInfoList(new ArrayList<>());
        goOut.setLittleZonePositionsInfoList(new ArrayList<>());
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        
        return goOut;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（城市体验专家）
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_UrbanExpert(List<Integer> littleZoneIds) {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        
        // 小区岗位 - 城市体验专家
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositions = new ArrayList<>();
        for (Integer littleZoneId : littleZoneIds) {
            CarEmployeeInfoGoOut.ZonePositionInfo zonePosition = new CarEmployeeInfoGoOut.ZonePositionInfo(
                    PositionEnum.URBAN_EXPERIENCE_EXPERT.getCode(),
                    PositionEnum.URBAN_EXPERIENCE_EXPERT.getName(),
                    littleZoneId,
                    "小区" + littleZoneId
            );
            littleZonePositions.add(zonePosition);
        }
        goOut.setLittleZonePositionsInfoList(littleZonePositions);
        
        goOut.setChannelPositionInfoList(new ArrayList<>());
        goOut.setBigZonePositionsInfoList(new ArrayList<>());
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        
        return goOut;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（区域运营管理，门店免责二审�?
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_RegionalOpsManager(List<Integer> zoneIds) {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> bigZonePositions = new ArrayList<>();
        for (Integer zoneId : zoneIds) {
            CarEmployeeInfoGoOut.ZonePositionInfo zonePosition = new CarEmployeeInfoGoOut.ZonePositionInfo(
                    PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getCode(),
                    PositionEnum.REGIONAL_OPERATIONS_MANAGEMENT.getName(),
                    zoneId,
                    "大区" + zoneId
            );
            bigZonePositions.add(zonePosition);
        }
        goOut.setBigZonePositionsInfoList(bigZonePositions);
        goOut.setChannelPositionInfoList(new ArrayList<>());
        goOut.setLittleZonePositionsInfoList(new ArrayList<>());
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        return goOut;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（城市服务经理，门店免责一审）
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_CityServiceManager(List<Integer> littleZoneIds) {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        List<CarEmployeeInfoGoOut.ZonePositionInfo> littleZonePositions = new ArrayList<>();
        for (Integer littleZoneId : littleZoneIds) {
            CarEmployeeInfoGoOut.ZonePositionInfo zonePosition = new CarEmployeeInfoGoOut.ZonePositionInfo(
                    PositionEnum.CITY_SERVICE_MANAGER.getCode(),
                    PositionEnum.CITY_SERVICE_MANAGER.getName(),
                    littleZoneId,
                    "小区" + littleZoneId
            );
            littleZonePositions.add(zonePosition);
        }
        goOut.setLittleZonePositionsInfoList(littleZonePositions);
        goOut.setChannelPositionInfoList(new ArrayList<>());
        goOut.setBigZonePositionsInfoList(new ArrayList<>());
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        return goOut;
    }

    /**
     * 构建CarEmployeeInfoGoOut测试数据（客诉处理岗位）
     * 实现�?isComplaintHandling 根据 channelPositionInfoList 判断，故客诉处理岗位需放在渠道岗位列表�?
     */
    public static CarEmployeeInfoGoOut buildCarEmployeeInfo_ComplaintHandling() {
        CarEmployeeInfoGoOut goOut = new CarEmployeeInfoGoOut();
        
        // 渠道岗位 - 客诉处理岗位（与 ComplaintAuditServiceImpl.getComplaintAuditInfo 判断逻辑一致）
        CarEmployeeInfoGoOut.ChannelPositionInfo complaintHandlingPosition = new CarEmployeeInfoGoOut.ChannelPositionInfo(
                PositionEnum.COMPLAINT_HANDLING.getCode(),
                PositionEnum.COMPLAINT_HANDLING.getName()
        );
        goOut.setChannelPositionInfoList(Lists.newArrayList(complaintHandlingPosition));
        
        goOut.setHeadPositionsInfoList(new ArrayList<>());
        goOut.setLittleZonePositionsInfoList(new ArrayList<>());
        goOut.setBigZonePositionsInfoList(new ArrayList<>());
        
        return goOut;
    }

    /**
     * 构建EmployeeInfoGoOut测试数据
     */
    public static EmployeeInfoGoOut buildEmployeeInfoGoOut(Long mid, String name) {
        EmployeeInfoGoOut goOut = new EmployeeInfoGoOut();
        goOut.setMiId(mid);
        goOut.setName(name);
        goOut.setEmail(name + "@xiaomi.com");
        return goOut;
    }

    /**
     * 构建ComplaintOrderUpgradeSoIn测试数据
     */
    public static ComplaintOrderUpgradeSoIn buildComplaintOrderUpgradeSoIn(String complaintNo, Integer targetType) {
        ComplaintOrderUpgradeSoIn soIn = new ComplaintOrderUpgradeSoIn();
        soIn.setComplaintNo(complaintNo);
        soIn.setTargetType(targetType);
        soIn.setUpgradeReason("升级原因测试");
        soIn.setOperatorMid(1001L);
        soIn.setOperatorName("测试操作�?);
        return soIn;
    }

    /**
     * 构建OrderEditComplaintSoIn测试数据
     */
    public static OrderEditComplaintSoIn buildOrderEditComplaintSoIn(String complaintNo) {
        OrderEditComplaintSoIn soIn = new OrderEditComplaintSoIn();
        soIn.setComplaintNo(complaintNo);
        soIn.setOperateMid(1001L);
        soIn.setOperateName("测试操作�?);
        return soIn;
    }

    /**
     * 构建FieldValueSoIn测试数据（投诉场景）
     */
    public static FieldValueSoIn buildFieldValueSoIn(String code, String desc, String pathId, String pathName) {
        FieldValueSoIn soIn = new FieldValueSoIn();
        soIn.setCode(code);
        soIn.setDesc(desc);
        soIn.setPathId(pathId);
        soIn.setPathName(pathName);
        return soIn;
    }

    /**
     * 构建OrderAddFollowUpRecordSoInV2测试数据
     */
    public static OrderAddFollowUpRecordSoInV2 buildOrderAddFollowUpRecordSoInV2(String complaintNo) {
        OrderAddFollowUpRecordSoInV2 soIn = new OrderAddFollowUpRecordSoInV2();
        soIn.setComplaintNo(complaintNo);
        soIn.setFollowUpMid("1001");
        soIn.setFollowUpName("测试跟进�?);
        soIn.setFollowInfo("测试跟进内容");
        soIn.setAttachmentList(new ArrayList<>());
        soIn.setMileage("1000.50");
        soIn.setLoginRole("TEST_ROLE"); // 设置登录角色避免校验失败
        return soIn;
    }
}
