package com.wt.complaint.manage.infrastructure.gatewayimpl;

import com.wt.complaint.manage.api.model.enums.ComplaintStatusEnum;
import com.wt.complaint.manage.api.model.enums.CreateSourceEnum;
import com.wt.complaint.manage.api.model.enums.GenderEnum;
import com.wt.complaint.manage.api.model.enums.ResponsibilityEnum;
import com.wt.complaint.manage.api.model.enums.RiskLevelEnum;
import com.wt.complaint.manage.domain.api.enums.ComplaintTypeEnum;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.GetDynamicInfoResponseGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.StoreInfoGoOut;
import com.wt.complaint.manage.domain.api.service.parameter.in.ComplaintListSearchGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchInfo;
import com.wt.complaint.manage.domain.api.service.parameter.out.ComplaintListSearchSoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.ComplaintOrderGoOut;
import com.wt.complaint.manage.domain.utils.KeyCenterUtil;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintOrderMapper;
import com.wt.complaint.manage.infrastructure.mapper.ComplaintTagMapper;
import com.wt.complaint.manage.infrastructure.model.ComplaintOrderDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * ComplaintGatewayImpl#getComplaintOrderList 单测（覆盖耗时统计日志相关逻辑，不�?Mapper/XML�?
 */
@ExtendWith(MockitoExtension.class)
class ComplaintGatewayImplUnitTest {

    @InjectMocks
    private ComplaintGatewayImpl complaintGateway;

    @Mock
    private ComplaintOrderMapper complaintOrderMapper;

    @Mock
    private ComplaintTagMapper complaintTagMapper;

    @Mock
    private CarRemoteGateway carRemoteGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @BeforeEach
    void setUp() throws Exception {
        // 使用 Executor 接口 mock 代替 MoneThreadPoolExecutor（避�?Java 21 final class mock 限制�?
        java.util.concurrent.Executor mockExecutor = mock(java.util.concurrent.Executor.class);
        lenient().doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));

        // MoneThreadPoolExecutor无法直接mock，通过Unsafe绕过Field.set的类型检�?
        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
        Field executorField = ComplaintGatewayImpl.class.getDeclaredField("complaintOrderListExecutor");
        long offset = unsafe.objectFieldOffset(executorField);
        unsafe.putObject(complaintGateway, offset, mockExecutor);
    }

    @Test
    void getComplaintOrderList_emptyList_returnsEmptyResult() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .source("pad")
                .build();
        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class)))
                .thenReturn(Collections.emptyList());

        ComplaintListSearchSoOut result = complaintGateway.getComplaintOrderList(goIn);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertNotNull(result.getDataList());
        assertTrue(result.getDataList().isEmpty());
        verify(complaintOrderMapper).selectPageByParam(any(ComplaintListSearchGoIn.class));
        verify(complaintOrderMapper, never()).countByParam(any());
        verify(complaintTagMapper, never()).selectByComplaintNoList(anyList());
    }

    @Test
    void getComplaintOrderList_hasData_returnsResultWithTotalAndDataList() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .source("pad")
                .build();
        ComplaintOrderDO orderDo = new ComplaintOrderDO();
        orderDo.setComplaintNo("TS-001");
        orderDo.setVid("vid1");
        orderDo.setOrgId("org1");
        List<ComplaintOrderDO> orderList = Collections.singletonList(orderDo);

        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class))).thenReturn(orderList);
        when(complaintOrderMapper.countByParam(any(ComplaintListSearchGoIn.class))).thenReturn(1);
        GetDynamicInfoResponseGoOut dynamicInfo = new GetDynamicInfoResponseGoOut();
        dynamicInfo.setItems(Collections.emptyList());
        when(carRemoteGateway.getDynamicInfo(anyList())).thenReturn(dynamicInfo);
        when(storeRemoteGateway.getStoreListInfo(anyList())).thenReturn(Collections.emptyList());
        when(eiamRemoteGateway.getNameByMid(anyList())).thenReturn(Collections.emptyMap());
        when(complaintTagMapper.selectByComplaintNoList(anyList())).thenReturn(Collections.emptyList());

        ComplaintListSearchSoOut result = complaintGateway.getComplaintOrderList(goIn);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertNotNull(result.getDataList());
        assertEquals(1, result.getDataList().size());
        assertEquals("TS-001", result.getDataList().get(0).getComplaintNo());
        verify(complaintOrderMapper).selectPageByParam(any(ComplaintListSearchGoIn.class));
        verify(complaintOrderMapper).countByParam(any(ComplaintListSearchGoIn.class));
        verify(complaintTagMapper).selectByComplaintNoList(anyList());
    }

    // ==================== fillEnumDescriptions 测试 ====================

    @Test
    void fillEnumDescriptions_allFieldsNotNull_setsAllDescriptions() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setComplaintType(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        info.setStatus(ComplaintStatusEnum.PENDING_ORDER.getCode());
        info.setResponsibility(ResponsibilityEnum.YES.getCode());
        info.setRiskLevel(RiskLevelEnum.LEVEL_1.getCode());
        info.setCreateSource(CreateSourceEnum.STORE.getCode());

        invokePrivateStaticMethod("fillEnumDescriptions", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals(ComplaintTypeEnum.PRODUCT_COMPLAINT.getDesc(), info.getComplaintTypeName());
        assertEquals(ComplaintStatusEnum.PENDING_ORDER.getDesc(), info.getStatusName());
        assertEquals(ResponsibilityEnum.YES.getDesc(), info.getResponsibilityName());
        assertEquals(RiskLevelEnum.LEVEL_1.getDesc(), info.getRiskLevelName());
        assertEquals(CreateSourceEnum.STORE.getDesc(), info.getCreateSourceDesc());
    }

    @Test
    void fillEnumDescriptions_allFieldsNull_doesNotSetDescriptions() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();

        invokePrivateStaticMethod("fillEnumDescriptions", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getComplaintTypeName());
        assertNull(info.getStatusName());
        assertNull(info.getResponsibilityName());
        assertNull(info.getRiskLevelName());
        assertNull(info.getCreateSourceDesc());
    }

    @Test
    void fillEnumDescriptions_partialFields_setsOnlyNonNullDescriptions() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setComplaintType(ComplaintTypeEnum.SERVICE_COMPLAINT.getCode());
        info.setRiskLevel(RiskLevelEnum.LEVEL_2.getCode());

        invokePrivateStaticMethod("fillEnumDescriptions", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals(ComplaintTypeEnum.SERVICE_COMPLAINT.getDesc(), info.getComplaintTypeName());
        assertNull(info.getStatusName());
        assertNull(info.getResponsibilityName());
        assertEquals(RiskLevelEnum.LEVEL_2.getDesc(), info.getRiskLevelName());
        assertNull(info.getCreateSourceDesc());
    }

    // ==================== fillDecryptedFields 测试 ====================

    @Test
    void fillDecryptedFields_withEncryptedData_decryptsSuccessfully() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactPhoneC("encrypted_phone");
        info.setContactNameC("encrypted_name");

        try (MockedStatic<KeyCenterUtil> mocked = mockStatic(KeyCenterUtil.class)) {
            mocked.when(() -> KeyCenterUtil.decrypt("encrypted_phone")).thenReturn("13800138000");
            mocked.when(() -> KeyCenterUtil.decrypt("encrypted_name")).thenReturn("张三");

            invokePrivateStaticMethod("fillDecryptedFields", new Class[]{ComplaintListSearchInfo.class}, info);

            assertEquals("13800138000", info.getContactPhone());
            assertEquals("张三", info.getContactName());
        }
    }

    @Test
    void fillDecryptedFields_emptyEncryptedFields_doesNotDecrypt() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactPhoneC("");
        info.setContactNameC("");

        invokePrivateStaticMethod("fillDecryptedFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getContactPhone());
        assertNull(info.getContactName());
    }

    @Test
    void fillDecryptedFields_nullEncryptedFields_doesNotDecrypt() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();

        invokePrivateStaticMethod("fillDecryptedFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getContactPhone());
        assertNull(info.getContactName());
    }

    // ==================== fillMidFields 测试 ====================

    @Test
    void fillMidFields_zeroMids_setsToNull() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOperatorMid(0L);
        info.setCustomerServiceMid(0L);

        invokePrivateStaticMethod("fillMidFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getOperatorMid());
        assertNull(info.getCustomerServiceMid());
    }

    @Test
    void fillMidFields_nonZeroMids_keepsOriginalValues() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOperatorMid(1001L);
        info.setCustomerServiceMid(1002L);

        invokePrivateStaticMethod("fillMidFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals(1001L, info.getOperatorMid());
        assertEquals(1002L, info.getCustomerServiceMid());
    }

    @Test
    void fillMidFields_nullMids_remainsNull() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();

        invokePrivateStaticMethod("fillMidFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getOperatorMid());
        assertNull(info.getCustomerServiceMid());
    }

    // ==================== fillTimeFields 测试 ====================

    @Test
    void fillTimeFields_defaultTime_clearsToEmpty() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setFirstResponseTime("1970-08-02 00:00:00");
        info.setFinishTime("1970-08-02 00:00:00");

        invokePrivateStaticMethod("fillTimeFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("", info.getFirstResponseTime());
        assertEquals("", info.getFinishTime());
    }

    @Test
    void fillTimeFields_normalTime_keepsOriginalValues() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setFirstResponseTime("2026-01-15 10:30:00");
        info.setFinishTime("2026-01-20 14:00:00");

        invokePrivateStaticMethod("fillTimeFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("2026-01-15 10:30:00", info.getFirstResponseTime());
        assertEquals("2026-01-20 14:00:00", info.getFinishTime());
    }

    @Test
    void fillTimeFields_emptyOrNullTime_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setFirstResponseTime("");
        info.setFinishTime(null);

        invokePrivateStaticMethod("fillTimeFields", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("", info.getFirstResponseTime());
        assertNull(info.getFinishTime());
    }

    @Test
    void fillTimeFields_invalidTimeFormat_doesNotThrow() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setFirstResponseTime("invalid-date");
        info.setFinishTime("not-a-date");

        invokePrivateStaticMethod("fillTimeFields", new Class[]{ComplaintListSearchInfo.class}, info);

        // 解析失败不抛异常，保持原�?
        assertEquals("invalid-date", info.getFirstResponseTime());
        assertEquals("not-a-date", info.getFinishTime());
    }

    // ==================== fillContactName 测试 ====================

    @Test
    void fillContactName_maleGender_appendsMaleSuffix() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName("张三");
        info.setContactGender(GenderEnum.MALE.getCode());

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("�? + GenderEnum.MALE.getExtend(), info.getContactName());
    }

    @Test
    void fillContactName_femaleGender_appendsFemaleSuffix() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName("李四");
        info.setContactGender(GenderEnum.FEMALE.getCode());

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("�? + GenderEnum.FEMALE.getExtend(), info.getContactName());
    }

    @Test
    void fillContactName_unknownGender_appendsUnknownSuffix() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName("王五");
        info.setContactGender(GenderEnum.UNKNOWN.getCode());

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("�? + GenderEnum.UNKNOWN.getExtend(), info.getContactName());
    }

    @Test
    void fillContactName_nullGender_appendsUnknownSuffix() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName("赵六");
        info.setContactGender(null);

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("�? + GenderEnum.UNKNOWN.getExtend(), info.getContactName());
    }

    @Test
    void fillContactName_emptyName_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName("");
        info.setContactGender(GenderEnum.MALE.getCode());

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertEquals("", info.getContactName());
    }

    @Test
    void fillContactName_nullName_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setContactName(null);
        info.setContactGender(GenderEnum.MALE.getCode());

        invokePrivateStaticMethod("fillContactName", new Class[]{ComplaintListSearchInfo.class}, info);

        assertNull(info.getContactName());
    }

    // ==================== fillUserMidName 测试 ====================

    @Test
    void fillUserMidName_withNameMap_fillsBothNames() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOperatorMid(1001L);
        info.setCustomerServiceMid(1002L);
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);
        Map<Long, String> nameMap = new HashMap<>();
        nameMap.put(1001L, "操作�?);
        nameMap.put(1002L, "客服人员");

        invokePrivateStaticMethod("fillUserMidName",
                new Class[]{List.class, Map.class}, list, nameMap);

        assertEquals("操作�?, info.getOperatorName());
        assertEquals("客服人员", info.getCustomerServiceName());
    }

    @Test
    void fillUserMidName_nullNameMap_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOperatorMid(1001L);
        info.setCustomerServiceMid(1002L);
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        invokePrivateStaticMethod("fillUserMidName",
                new Class[]{List.class, Map.class}, list, (Object) null);

        assertNull(info.getOperatorName());
        assertNull(info.getCustomerServiceName());
    }

    @Test
    void fillUserMidName_nullMids_doesNotSetNames() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);
        Map<Long, String> nameMap = new HashMap<>();
        nameMap.put(1001L, "操作�?);

        invokePrivateStaticMethod("fillUserMidName",
                new Class[]{List.class, Map.class}, list, nameMap);

        assertNull(info.getOperatorName());
        assertNull(info.getCustomerServiceName());
    }

    // ==================== getUserMids 测试 ====================

    @Test
    void getUserMids_withDuplicateMids_returnsDeduplicated() throws Exception {
        ComplaintListSearchInfo info1 = new ComplaintListSearchInfo();
        info1.setOperatorMid(1001L);
        info1.setCustomerServiceMid(1002L);
        ComplaintListSearchInfo info2 = new ComplaintListSearchInfo();
        info2.setOperatorMid(1001L);
        info2.setCustomerServiceMid(1003L);

        @SuppressWarnings("unchecked")
        List<Long> result = (List<Long>) invokePrivateStaticMethodWithReturn("getUserMids",
                new Class[]{List.class}, Arrays.asList(info1, info2));

        assertEquals(3, result.size());
        assertTrue(result.contains(1001L));
        assertTrue(result.contains(1002L));
        assertTrue(result.contains(1003L));
    }

    @Test
    void getUserMids_emptyList_returnsEmptyList() throws Exception {
        @SuppressWarnings("unchecked")
        List<Long> result = (List<Long>) invokePrivateStaticMethodWithReturn("getUserMids",
                new Class[]{List.class}, new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== fillVinData 测试 ====================

    @Test
    void fillVinData_withDynamicInfo_fillsVin() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setVid("V001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        GetDynamicInfoResponseGoOut dynamicInfo = new GetDynamicInfoResponseGoOut();
        GetDynamicInfoResponseGoOut.DynamicInfoItemDto item = new GetDynamicInfoResponseGoOut.DynamicInfoItemDto();
        item.setVid("V001");
        item.setVin("LMWSA12345");
        dynamicInfo.setItems(Collections.singletonList(item));

        invokePrivateStaticMethod("fillVinData",
                new Class[]{List.class, GetDynamicInfoResponseGoOut.class}, list, dynamicInfo);

        assertEquals("LMWSA12345", info.getVin());
    }

    @Test
    void fillVinData_nullDynamicInfo_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setVid("V001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        invokePrivateStaticMethod("fillVinData",
                new Class[]{List.class, GetDynamicInfoResponseGoOut.class}, list, (Object) null);

        assertNull(info.getVin());
    }

    @Test
    void fillVinData_noMatchingVid_doesNotFillVin() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setVid("V001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        GetDynamicInfoResponseGoOut dynamicInfo = new GetDynamicInfoResponseGoOut();
        GetDynamicInfoResponseGoOut.DynamicInfoItemDto item = new GetDynamicInfoResponseGoOut.DynamicInfoItemDto();
        item.setVid("V999");
        item.setVin("LMWSA99999");
        dynamicInfo.setItems(Collections.singletonList(item));

        invokePrivateStaticMethod("fillVinData",
                new Class[]{List.class, GetDynamicInfoResponseGoOut.class}, list, dynamicInfo);

        assertNull(info.getVin());
    }

    // ==================== fillStoreInfoData 测试 ====================

    @Test
    void fillStoreInfoData_withStoreInfo_fillsOrgName() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOrgId("F001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgId("F001")
                .orgName("北京朝阳门店")
                .build();
        List<StoreInfoGoOut> storeList = Collections.singletonList(storeInfo);

        invokePrivateStaticMethod("fillStoreInfoData",
                new Class[]{List.class, List.class}, list, storeList);

        assertEquals("北京朝阳门店", info.getOrgName());
    }

    @Test
    void fillStoreInfoData_nullStoreList_doesNothing() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOrgId("F001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        invokePrivateStaticMethod("fillStoreInfoData",
                new Class[]{List.class, List.class}, list, (Object) null);

        assertNull(info.getOrgName());
    }

    @Test
    void fillStoreInfoData_noMatchingOrgId_doesNotFillOrgName() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setOrgId("F001");
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        StoreInfoGoOut storeInfo = StoreInfoGoOut.builder()
                .orgId("F999")
                .orgName("其他门店")
                .build();
        List<StoreInfoGoOut> storeList = Collections.singletonList(storeInfo);

        invokePrivateStaticMethod("fillStoreInfoData",
                new Class[]{List.class, List.class}, list, storeList);

        assertNull(info.getOrgName());
    }

    // ==================== fillBasicInfo 集成测试 ====================

    @Test
    void fillBasicInfo_callsAllSubMethods() throws Exception {
        ComplaintListSearchInfo info = new ComplaintListSearchInfo();
        info.setComplaintType(ComplaintTypeEnum.PRODUCT_COMPLAINT.getCode());
        info.setStatus(ComplaintStatusEnum.PENDING_ORDER.getCode());
        info.setResponsibility(ResponsibilityEnum.YES.getCode());
        info.setRiskLevel(RiskLevelEnum.LEVEL_1.getCode());
        info.setCreateSource(CreateSourceEnum.STORE.getCode());
        info.setOperatorMid(0L);
        info.setCustomerServiceMid(1002L);
        info.setFirstResponseTime("1970-08-02 00:00:00");
        info.setFinishTime("2026-01-20 14:00:00");
        info.setContactPhoneC("encrypted_phone");
        info.setContactNameC("encrypted_name");
        info.setContactGender(GenderEnum.MALE.getCode());
        List<ComplaintListSearchInfo> list = Collections.singletonList(info);

        try (MockedStatic<KeyCenterUtil> mocked = mockStatic(KeyCenterUtil.class)) {
            mocked.when(() -> KeyCenterUtil.decrypt("encrypted_phone")).thenReturn("13800138000");
            mocked.when(() -> KeyCenterUtil.decrypt("encrypted_name")).thenReturn("张三");

            invokePrivateStaticMethod("fillBasicInfo", new Class[]{List.class}, list);

            // 验证枚举描述填充
            assertEquals(ComplaintTypeEnum.PRODUCT_COMPLAINT.getDesc(), info.getComplaintTypeName());
            assertEquals(ComplaintStatusEnum.PENDING_ORDER.getDesc(), info.getStatusName());
            assertEquals(CreateSourceEnum.STORE.getDesc(), info.getCreateSourceDesc());

            // 验证解密字段
            assertEquals("13800138000", info.getContactPhone());

            // 验证Mid字段�?L转null�?
            assertNull(info.getOperatorMid());
            assertEquals(1002L, info.getCustomerServiceMid());

            // 验证时间字段（默认时间被清除�?
            assertEquals("", info.getFirstResponseTime());
            assertEquals("2026-01-20 14:00:00", info.getFinishTime());

            // 验证联系人脱敏（decrypt返回"张三"，脱敏后 = "�? + 先生�?
            assertEquals("�? + GenderEnum.MALE.getExtend(), info.getContactName());
        }
    }

    // ==================== selectPageByParam 测试 ====================

    @Test
    void selectPageByParam_hasData_returnsConvertedList() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .build();
        ComplaintOrderDO orderDo = new ComplaintOrderDO();
        orderDo.setComplaintNo("TS-001");
        orderDo.setOrgId("F001");
        orderDo.setStatus(1);
        orderDo.setVinSufix("123456");
        List<ComplaintOrderDO> doList = Collections.singletonList(orderDo);

        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class))).thenReturn(doList);

        List<ComplaintOrderGoOut> result = complaintGateway.selectPageByParam(goIn);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TS-001", result.get(0).getComplaintNo());
        assertEquals("F001", result.get(0).getOrgId());
        assertEquals(1, result.get(0).getStatus());
        assertEquals(123456, result.get(0).getVinSufix());
        verify(complaintOrderMapper).selectPageByParam(goIn);
    }

    @Test
    void selectPageByParam_emptyList_returnsEmptyList() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .build();

        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class)))
                .thenReturn(Collections.emptyList());

        List<ComplaintOrderGoOut> result = complaintGateway.selectPageByParam(goIn);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(complaintOrderMapper).selectPageByParam(goIn);
    }

    @Test
    void selectPageByParam_multipleRecords_returnsAllConverted() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .build();
        ComplaintOrderDO order1 = new ComplaintOrderDO();
        order1.setComplaintNo("TS-001");
        order1.setOrgId("F001");
        ComplaintOrderDO order2 = new ComplaintOrderDO();
        order2.setComplaintNo("TS-002");
        order2.setOrgId("F002");

        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class)))
                .thenReturn(Arrays.asList(order1, order2));

        List<ComplaintOrderGoOut> result = complaintGateway.selectPageByParam(goIn);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TS-001", result.get(0).getComplaintNo());
        assertEquals("TS-002", result.get(1).getComplaintNo());
    }

    @Test
    void selectPageByParam_vinSufixBlank_convertsToZero() {
        ComplaintListSearchGoIn goIn = ComplaintListSearchGoIn.builder()
                .pageNum(1)
                .pageSize(10)
                .build();
        ComplaintOrderDO orderDo = new ComplaintOrderDO();
        orderDo.setComplaintNo("TS-003");
        orderDo.setVinSufix("");

        when(complaintOrderMapper.selectPageByParam(any(ComplaintListSearchGoIn.class)))
                .thenReturn(Collections.singletonList(orderDo));

        List<ComplaintOrderGoOut> result = complaintGateway.selectPageByParam(goIn);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getVinSufix());
    }

    // ==================== 反射工具方法 ====================

    private void invokePrivateStaticMethod(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = ComplaintGatewayImpl.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        method.invoke(null, args);
    }

    private Object invokePrivateStaticMethodWithReturn(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = ComplaintGatewayImpl.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }
}
