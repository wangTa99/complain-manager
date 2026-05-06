package com.wt.complaint.manage.domain.aggregation;

import com.wt.complaint.manage.api.model.enums.AuditStatusEnum;
import com.wt.complaint.manage.api.model.enums.AuditTypeEnum;
import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.MediaInfoEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.enums.UserAgreementEnum;
import com.wt.complaint.manage.api.model.enums.VehicleRepairedEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintAuditGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintFollowProcessGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.ComplaintOrderInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.in.RecordInfoGoIn;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintApplySoIn;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.testutil.TestDataBuilder;
import com.wt.nr.common.utils.GsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComplaintAuditAggregation 单元测试
 * 针对当前分支相对 master 新增�?userAgreement、vehicleRepaired、mediaInfo 相关逻辑进行覆盖
 */
public class ComplaintAuditAggregationUnitTest {

    /**
     * 申请结案时，auditGoIn �?userAgreement、vehicleRepaired、mediaInfo 被正确设置（新增字段�?
     */
    @Test
    void createApply_applicationForClosure_userAgreementVehicleRepairedMediaInfo_setOnAuditGoIn() {
        String complaintNo = "C001";
        String orgId = "F001";
        Long createMid = 1001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setVid("V001");
        orderInfo.setCarNo("京A12345");
        orderInfo.setContactNameC("contactNameC");
        orderInfo.setContactPhoneC("contactPhoneC");
        orderInfo.setContactPhoneMd5("contactPhoneMd5");
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId)
                .orgName("测试门店")
                .zoneId(1)
                .littleZoneId(10)
                .cityId("100")
                .build();
        List<StoreInfoGoOut> carStoreList = Collections.singletonList(store);

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(carStoreList)
                .employeeMap(employeeMap)
                .build();

        Integer userAgreement = UserAgreementEnum.YES.getCode();
        Integer vehicleRepaired = VehicleRepairedEnum.YES.getCode();
        Integer mediaInfo = MediaInfoEnum.NOT_INVOLVED.getCode();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .applyContent("申请结案")
                .solutionDesc("已解�?)
                .userAgreement(userAgreement)
                .vehicleRepaired(vehicleRepaired)
                .mediaInfo(mediaInfo)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        ComplaintAuditGoIn auditGoIn = aggregation.getAuditGoIn();
        assertNotNull(auditGoIn);
        assertEquals(userAgreement, auditGoIn.getUserAgreement());
        assertEquals(vehicleRepaired, auditGoIn.getVehicleRepaired());
        assertEquals(mediaInfo, auditGoIn.getMediaInfo());
    }

    /**
     * 申请结案时，createFinishProcess 写入�?processContent �?RecordInfoGoIn 包含 userAgreementDesc、vehicleRepairedDesc、mediaInfoDesc（新增字段）
     */
    @Test
    void createApply_applicationForClosure_finishProcess_recordInfoContainsUserAgreementVehicleRepairedMediaInfoDesc() {
        String complaintNo = "C002";
        String orgId = "F001";
        Long createMid = 1002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setVid("V002");
        orderInfo.setCarNo("京B67890");
        orderInfo.setContactNameC("contactNameC2");
        orderInfo.setContactPhoneC("contactPhoneC2");
        orderInfo.setContactPhoneMd5("contactPhoneMd52");
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId)
                .orgName("测试门店2")
                .zoneId(2)
                .littleZoneId(20)
                .cityId("200")
                .build();
        List<StoreInfoGoOut> carStoreList = Collections.singletonList(store);

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(carStoreList)
                .employeeMap(employeeMap)
                .build();

        Integer userAgreement = UserAgreementEnum.NO.getCode();
        Integer vehicleRepaired = VehicleRepairedEnum.NOT_INVOLVED.getCode();
        Integer mediaInfo = MediaInfoEnum.USER_DELETED.getCode();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .applyContent("申请结案")
                .solutionDesc("已解�?)
                .userAgreement(userAgreement)
                .vehicleRepaired(vehicleRepaired)
                .mediaInfo(mediaInfo)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        ComplaintFollowProcessGoIn followProcessGoIn = aggregation.getComplaintFollowProcessGoIn();
        assertNotNull(followProcessGoIn);
        assertNotNull(followProcessGoIn.getProcessContent());

        RecordInfoGoIn recordInfoGoIn = GsonUtil.fromJson(followProcessGoIn.getProcessContent(), RecordInfoGoIn.class);
        assertNotNull(recordInfoGoIn);
        assertEquals(UserAgreementEnum.getDescByCode(userAgreement), recordInfoGoIn.getUserAgreementDesc());
        assertEquals(VehicleRepairedEnum.getDescByCode(vehicleRepaired), recordInfoGoIn.getVehicleRepairedDesc());
        assertEquals(MediaInfoEnum.getDescByCode(mediaInfo), recordInfoGoIn.getMediaInfoDesc());
    }

    @Test
    void createComplaintAdjudicationApply_shouldBuildJudgeRecordAndClearOrderInfo() {
        String complaintNo = "C004";
        Long createMid = 1004L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setCreateMid(createMid);

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .employeeMap(employeeMap)
                .build();

        ComplaintAuditGoIn auditGoIn = ComplaintAuditGoIn.builder()
                .complaintNo(complaintNo)
                .auditType(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode())
                .build();

        aggregation.createComplaintAdjudicationApply(auditGoIn);

        assertNotNull(aggregation.getAuditGoIn());
        assertEquals(AuditTypeEnum.JUDGE_RESPONSIBILITY.getCode(), aggregation.getAuditGoIn().getAuditType());

        // 判责审批单由事务落库，聚合内不再构建跟进记录
        assertNull(aggregation.getComplaintFollowProcessGoIn());

        assertNull(aggregation.getOrderInfo());
    }

    // ======================== buildAuditGoIn 单元测试 ========================

    /**
     * buildAuditGoIn: 验证auditGoIn全部字段从orderInfo和soIn正确映射
     */
    @Test
    void createApply_buildAuditGoIn_allFieldsMappedCorrectly() {
        String complaintNo = "C010";
        String orgId = "F010";
        Long createMid = 2001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setVid("V010");
        orderInfo.setCarNo("京C10000");
        orderInfo.setContactNameC("张三密文");
        orderInfo.setContactPhoneC("13800138000密文");
        orderInfo.setContactPhoneMd5("md5hash010");
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("北京门店").zoneId(5).littleZoneId(50).cityId("500").build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "张三"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .applyContent("申请结案内容")
                .solutionDesc("已解�?)
                .userAgreement(UserAgreementEnum.YES.getCode())
                .vehicleRepaired(VehicleRepairedEnum.YES.getCode())
                .mediaInfo(MediaInfoEnum.NOT_INVOLVED.getCode())
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        ComplaintAuditGoIn auditGoIn = aggregation.getAuditGoIn();
        assertNotNull(auditGoIn);
        assertEquals(complaintNo, auditGoIn.getComplaintNo());
        assertEquals("V010", auditGoIn.getVid());
        assertEquals("京C10000", auditGoIn.getCarNo());
        assertEquals("张三密文", auditGoIn.getContactNameC());
        assertEquals("13800138000密文", auditGoIn.getContactPhoneC());
        assertEquals("md5hash010", auditGoIn.getContactPhoneMd5());
        assertEquals(orgId, auditGoIn.getOrgId());
        assertEquals("北京门店", auditGoIn.getOrgName());
        assertEquals("5", auditGoIn.getZoneId());
        assertEquals("50", auditGoIn.getLittleZoneId());
        assertEquals(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode(), auditGoIn.getAuditType());
        assertEquals("申请结案内容", auditGoIn.getApplyContent());
        assertEquals(AuditStatusEnum.PENDING.getCode(), auditGoIn.getAuditStatus());
        assertEquals(createMid, auditGoIn.getCreateMid());
    }

    /**
     * buildAuditGoIn: 验证soIn的createName、applyOrgName、desOrdName被正确填�?
     */
    @Test
    void createApply_buildAuditGoIn_fillsSoInNameFields() {
        String complaintNo = "C011";
        String orgId = "F011";
        String desOrgId = "F012";
        Long createMid = 2002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut orderStore = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("原门�?).zoneId(1).littleZoneId(10).build();
        StoreInfoGoOut desStore = StoreInfoGoOut.builder()
                .orgId(desOrgId).orgName("目标门店").zoneId(2).littleZoneId(20).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "李四"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Arrays.asList(orderStore, desStore))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .applyContent("改派门店")
                .applyReason("距离太远")
                .build();

        aggregation.createApply(soIn);

        // buildAuditGoIn 应将 employee name 填到 soIn.createName
        assertEquals("李四", soIn.getCreateName());
        // buildAuditGoIn 应将 applyOrgId 对应的门店名填到 soIn.applyOrgName
        assertEquals("原门�?, soIn.getApplyOrgName());
        // buildAuditGoIn 应将 desOrgId 对应的门店名填到 soIn.desOrdName
        assertEquals("目标门店", soIn.getDesOrdName());
    }

    /**
     * buildAuditGoIn: 当门店信息不匹配时，orgName/zoneId/littleZoneId应为空字符串
     */
    @Test
    void createApply_buildAuditGoIn_storeNotFound_fieldsEmpty() {
        String complaintNo = "C012";
        String orgId = "F_NOT_EXIST";
        Long createMid = 2003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        // carStoreList中没有orgId为F_NOT_EXIST的门�?
        StoreInfoGoOut otherStore = StoreInfoGoOut.builder()
                .orgId("F_OTHER").orgName("其他门店").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "王五"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(otherStore))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId("F_OTHER")
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .applyContent("申请结案")
                .solutionDesc("已解�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        ComplaintAuditGoIn auditGoIn = aggregation.getAuditGoIn();
        assertEquals("", auditGoIn.getOrgName());
        assertEquals("", auditGoIn.getZoneId());
        assertEquals("", auditGoIn.getLittleZoneId());
    }

    /**
     * buildAuditGoIn: 当employeeMap中找不到createMid时，soIn.createName应为空字符串
     */
    @Test
    void createApply_buildAuditGoIn_employeeNotFound_createNameEmpty() {
        String complaintNo = "C013";
        String orgId = "F013";
        Long createMid = 2004L;
        Long otherMid = 9999L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店13").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        // 放入其他mid，不放createMid
        employeeMap.put(otherMid, TestDataBuilder.buildEmployeeInfoGoOut(otherMid, "其他�?));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .applyContent("申请结案")
                .solutionDesc("已解�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        assertEquals("", soIn.getCreateName());
    }

    // ======================== handleAuditTypeProcess 单元测试 ========================

    /**
     * handleAuditTypeProcess: 无效的auditType抛出BusinessException
     */
    @Test
    void createApply_handleAuditTypeProcess_invalidAuditType_throwsException() {
        String complaintNo = "C020";
        String orgId = "F020";
        Long createMid = 3001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店20").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(999) // 无效的审批类�?
                .applyContent("测试")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> aggregation.createApply(soIn));
        assertTrue(ex.getMessage().contains("客诉申请类型错误"));
    }

    /**
     * handleAuditTypeProcess: 申请72H无法结案 �?create72HNOFinishProcess 分支
     */
    @Test
    void createApply_handleAuditTypeProcess_72HCannotBeClosed() {
        String complaintNo = "C021";
        String orgId = "F021";
        Long createMid = 3002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店21").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?1"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_72H_CANNOT_BE_CLOSED.getCode())
                .applyContent("72H无法结案")
                .applyReason("零件缺货")
                .deliveryTime("2026-01-01")
                .mileage(5000d)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        // create72HNOFinishProcess �?orderInfo 设为 null
        assertNull(aggregation.getOrderInfo());
        // processType 应为 APPLY_72H_CANNOT_FINISH
        ComplaintFollowProcessGoIn followProcess = aggregation.getComplaintFollowProcessGoIn();
        assertNotNull(followProcess);
        assertEquals(complaintNo, followProcess.getComplaintNo());
        assertEquals(ProcessTypeEnum.APPLY_72H_CANNOT_FINISH.getProcessCode(), followProcess.getProcessType());
        // processContent 应包含申请人和原�?
        RecordInfoGoIn record = GsonUtil.fromJson(followProcess.getProcessContent(), RecordInfoGoIn.class);
        assertEquals(createMid, record.getApplyMid());
        assertEquals("申请�?1", record.getApplyName());
        assertEquals("零件缺货", record.getApplyReason());
        assertEquals("2026-01-01", record.getDeliveryTime());
        assertEquals(5000d, record.getMileage());
    }

    /**
     * handleAuditTypeProcess: 产品风险类结案申请走 createFinishProcess 分支
     */
    @Test
    void createApply_handleAuditTypeProcess_productRiskClosureApplication() {
        String complaintNo = "C022";
        String orgId = "F022";
        Long createMid = 3003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店22").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "申请�?2"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.PRODUCT_RISK_CLOSURE_APPLICATION.getCode())
                .applyContent("产品风险结案")
                .solutionDesc("已修�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        // createFinishProcess 设置 orderInfo 状态为 FINISH_EVALUATION_PENDING
        assertNotNull(aggregation.getOrderInfo());
        assertEquals(ComplaintStatusEnum.FINISH_EVALUATION_PENDING.getCode(), aggregation.getOrderInfo().getStatus());
        // processType 应为 APPLY_FINISH
        assertEquals(ProcessTypeEnum.APPLY_FINISH.getProcessCode(),
                aggregation.getComplaintFollowProcessGoIn().getProcessType());
    }

    // ======================== handleReassignmentStores 单元测试 ========================

    /**
     * handleReassignmentStores: 改派门店时，auditGoIn.littleZoneId被覆盖为目标门店的littleZoneId
     */
    @Test
    void createApply_handleReassignmentStores_littleZoneIdOverriddenByDesStore() {
        String complaintNo = "C030";
        String orgId = "F030";
        String desOrgId = "F031";
        Long createMid = 4001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut orderStore = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("原门�?).zoneId(1).littleZoneId(10).build();
        StoreInfoGoOut desStore = StoreInfoGoOut.builder()
                .orgId(desOrgId).orgName("目标门店").zoneId(2).littleZoneId(88).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "改派申请�?));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Arrays.asList(orderStore, desStore))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .applyContent("改派门店")
                .applyReason("距离太远")
                .build();

        aggregation.createApply(soIn);

        // buildAuditGoIn 先设�?littleZoneId 为原门店�?10
        // handleReassignmentStores 再用目标门店�?88 覆盖
        assertEquals("88", aggregation.getAuditGoIn().getLittleZoneId());
    }

    /**
     * handleReassignmentStores: 改派门店后，orderInfo状态变为ORG_REASSIGN_PENDING，processType为APPLY_CHANGE_STORE
     */
    @Test
    void createApply_handleReassignmentStores_orderStatusAndProcessType() {
        String complaintNo = "C031";
        String orgId = "F030";
        String desOrgId = "F031";
        Long createMid = 4002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut orderStore = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("原门�?).zoneId(1).littleZoneId(10).build();
        StoreInfoGoOut desStore = StoreInfoGoOut.builder()
                .orgId(desOrgId).orgName("目标门店").zoneId(2).littleZoneId(20).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "改派申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Arrays.asList(orderStore, desStore))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .applyContent("改派门店")
                .applyReason("用户要求")
                .build();

        aggregation.createApply(soIn);

        // createReAssignFollowUpProcess 重新创建 orderInfo，状态为 ORG_REASSIGN_PENDING
        assertNotNull(aggregation.getOrderInfo());
        assertEquals(ComplaintStatusEnum.ORG_REASSIGN_PENDING.getCode(), aggregation.getOrderInfo().getStatus());
        assertEquals(complaintNo, aggregation.getOrderInfo().getComplaintNo());

        // processType �?APPLY_CHANGE_STORE
        ComplaintFollowProcessGoIn followProcess = aggregation.getComplaintFollowProcessGoIn();
        assertNotNull(followProcess);
        assertEquals(ProcessTypeEnum.APPLY_CHANGE_STORE.getProcessCode(), followProcess.getProcessType());
        assertEquals(complaintNo, followProcess.getComplaintNo());

        // processContent 包含改派门店信息
        RecordInfoGoIn record = GsonUtil.fromJson(followProcess.getProcessContent(), RecordInfoGoIn.class);
        assertEquals(createMid, record.getApplyMid());
        assertEquals("改派申请�?", record.getApplyName());
        assertEquals(orgId, record.getApplyOrgId());
        assertEquals("原门�?, record.getApplyOrgName());
        assertEquals(desOrgId, record.getReassignOrgId());
        assertEquals("目标门店", record.getReassignOrgName());
        assertEquals("用户要求", record.getApplyReason());
    }

    /**
     * handleReassignmentStores: 目标门店不在storeMap中时，littleZoneId不被覆盖
     */
    @Test
    void createApply_handleReassignmentStores_desStoreNotInMap_littleZoneIdNotOverridden() {
        String complaintNo = "C032";
        String orgId = "F030";
        String desOrgId = "F_NOT_IN_MAP";
        Long createMid = 4003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);

        StoreInfoGoOut orderStore = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("原门�?).zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "改派申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(orderStore))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .desOrgId(desOrgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.REASSIGNMENT_STORES.getCode())
                .applyContent("改派门店")
                .applyReason("原因")
                .build();

        aggregation.createApply(soIn);

        // desStore 找不到，littleZoneId 保持原门店的�?"10"
        assertEquals("10", aggregation.getAuditGoIn().getLittleZoneId());
    }

    // ======================== handleApplicationForWaiver 单元测试 ========================

    /**
     * handleApplicationForWaiver: 免责申请时，exemptionApplyTimes从null递增�?
     */
    @Test
    void createApply_handleApplicationForWaiver_exemptionApplyTimesFromNullTo1() {
        String complaintNo = "C040";
        String orgId = "F040";
        Long createMid = 5001L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);
        orderInfo.setExemptionApplyTimes(null);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店40").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "免责申请�?));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .applyContent("申请免责")
                .applyReason("非门店责�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        // exemptionApplyTimes �?null 递增�?1
        assertEquals(1, aggregation.getOrderInfo().getExemptionApplyTimes());
    }

    /**
     * handleApplicationForWaiver: 免责申请时，exemptionApplyTimes从已有值递增
     */
    @Test
    void createApply_handleApplicationForWaiver_exemptionApplyTimesIncrement() {
        String complaintNo = "C041";
        String orgId = "F041";
        Long createMid = 5002L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);
        orderInfo.setExemptionApplyTimes(2);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店41").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "免责申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .applyContent("申请免责")
                .applyReason("非门店责�?)
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        // exemptionApplyTimes �?2 递增�?3
        assertEquals(3, aggregation.getOrderInfo().getExemptionApplyTimes());
    }

    /**
     * handleApplicationForWaiver: 免责申请时，processType为APPLY_EXEMPTION，且processContent包含正确内容
     */
    @Test
    void createApply_handleApplicationForWaiver_processTypeAndContent() {
        String complaintNo = "C042";
        String orgId = "F042";
        Long createMid = 5003L;

        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn(complaintNo, ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        orderInfo.setOrgId(orgId);
        orderInfo.setExemptionApplyTimes(0);

        StoreInfoGoOut store = StoreInfoGoOut.builder()
                .orgId(orgId).orgName("门店42").zoneId(1).littleZoneId(10).build();

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(createMid, TestDataBuilder.buildEmployeeInfoGoOut(createMid, "免责申请�?"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(store))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(complaintNo)
                .applyOrgId(orgId)
                .createMid(createMid)
                .auditType(AuditTypeEnum.APPLICATION_FOR_WAIVER.getCode())
                .applyContent("申请免责")
                .applyReason("用户自身原因")
                .processInstanceId("PI-WAIVER-001")
                .attachmentSoInList(Collections.emptyList())
                .build();

        aggregation.createApply(soIn);

        ComplaintFollowProcessGoIn followProcess = aggregation.getComplaintFollowProcessGoIn();
        assertNotNull(followProcess);
        assertEquals(complaintNo, followProcess.getComplaintNo());
        assertEquals(ProcessTypeEnum.APPLY_EXEMPTION.getProcessCode(), followProcess.getProcessType());
        assertEquals("PI-WAIVER-001", followProcess.getProcessInstanceId());

        RecordInfoGoIn record = GsonUtil.fromJson(followProcess.getProcessContent(), RecordInfoGoIn.class);
        assertEquals(createMid, record.getApplyMid());
        assertEquals("免责申请�?", record.getApplyName());
        assertEquals("用户自身原因", record.getApplyReason());
    }

    // ======================== validateCreateApplyContext 单元测试 ========================

    /**
     * validateCreateApplyContext: orderInfo为null时抛出异�?
     */
    @Test
    void createApply_validateContext_orderInfoNull_throwsException() {
        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(1L, TestDataBuilder.buildEmployeeInfoGoOut(1L, "test"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(null)
                .carStoreList(Collections.singletonList(
                        StoreInfoGoOut.builder().orgId("F001").orgName("门店").zoneId(1).littleZoneId(10).build()))
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo("C050")
                .applyOrgId("F001")
                .createMid(1L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> aggregation.createApply(soIn));
        assertTrue(ex.getMessage().contains("客诉单为�?));
    }

    /**
     * validateCreateApplyContext: employeeMap为空时抛出异�?
     */
    @Test
    void createApply_validateContext_employeeMapEmpty_throwsException() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C051", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(
                        StoreInfoGoOut.builder().orgId("F001").orgName("门店").zoneId(1).littleZoneId(10).build()))
                .employeeMap(new HashMap<>())
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo("C051")
                .applyOrgId("F001")
                .createMid(1L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> aggregation.createApply(soIn));
        assertTrue(ex.getMessage().contains("员工信息不存�?));
    }

    /**
     * validateCreateApplyContext: carStoreList为空时抛出异�?
     */
    @Test
    void createApply_validateContext_carStoreListEmpty_throwsException() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C052", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(1L, TestDataBuilder.buildEmployeeInfoGoOut(1L, "test"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.emptyList())
                .employeeMap(employeeMap)
                .build();

        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo("C052")
                .applyOrgId("F001")
                .createMid(1L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> aggregation.createApply(soIn));
        assertTrue(ex.getMessage().contains("门店信息不存�?));
    }

    /**
     * validateCreateApplyContext: soIn校验失败（complaintNo为空）时抛出异常
     */
    @Test
    void createApply_validateContext_soInCheckFails_throwsException() {
        ComplaintOrderInfoGoIn orderInfo = TestDataBuilder.buildComplaintOrderInfoGoIn("C053", ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());

        Map<Long, EmployeeInfoGoOut> employeeMap = new HashMap<>();
        employeeMap.put(1L, TestDataBuilder.buildEmployeeInfoGoOut(1L, "test"));

        ComplaintAuditAggregation aggregation = ComplaintAuditAggregation.builder()
                .orderInfo(orderInfo)
                .carStoreList(Collections.singletonList(
                        StoreInfoGoOut.builder().orgId("F001").orgName("门店").zoneId(1).littleZoneId(10).build()))
                .employeeMap(employeeMap)
                .build();

        // complaintNo为空，checkApplySoIn会抛出异�?
        ComplaintApplySoIn soIn = ComplaintApplySoIn.builder()
                .complaintNo(null)
                .applyOrgId("F001")
                .createMid(1L)
                .auditType(AuditTypeEnum.APPLICATION_FOR_CLOSURE.getCode())
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> aggregation.createApply(soIn));
        assertTrue(ex.getMessage().contains("客诉单号不可为空"));
    }
}
