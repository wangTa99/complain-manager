package com.wt.complaint.manage.domain.api.service.parameter.out;

import com.wt.complaint.manage.api.model.enums.ConsultDetailTabEnum;
import com.wt.complaint.manage.api.model.enums.ConsultStatusEnum;
import com.wt.complaint.manage.api.model.enums.DoneYNEnum;
import com.wt.complaint.manage.api.model.enums.ProcessTypeEnum;
import com.wt.complaint.manage.api.model.resp.UserActionAuth;
import com.wt.complaint.manage.domain.api.enums.ConsultOrderStatusEnum;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.*;
import com.wt.complaint.manage.domain.manager.UserAuthManager;
import com.wt.complaint.manage.domain.model.UserConsultOrderInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConsultDetailSoOut 单元测试
 * 测试其中包含的业务方法（constructActionList, fillCarInfo, constructStatusBar, fillDetailTab, addMemberLabel�?
 */
@ExtendWith(MockitoExtension.class)
class ConsultDetailSoOutUnitTest {

    @Mock
    private UserAuthManager userAuthManager;

    private ConsultDetailSoOut consultDetailSoOut;

    private static final String TEST_CONSULT_NO = "ZX20260301001";
    private static final String TEST_VID = "V001";
    private static final Long TEST_MID = 1001L;

    @BeforeEach
    void setUp() {
        consultDetailSoOut = new ConsultDetailSoOut();
        consultDetailSoOut.setVid(TEST_VID);
        ConsultDetailSoOut.ConsultOrderInfo orderInfo = new ConsultDetailSoOut.ConsultOrderInfo();
        orderInfo.setConsultNo(TEST_CONSULT_NO);
        consultDetailSoOut.setConsultOrderInfo(orderInfo);
    }

    // ==================== constructActionList ====================

    @Test
    void constructActionList_setsUserActionAuth() {
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        List<String> actions = Arrays.asList("PICK_UP", "EDIT");
        when(userAuthManager.getDetailActionAuth(anyString(), any(UserConsultOrderInfo.class), anyLong())).thenReturn(actions);

        consultDetailSoOut.constructActionList("ADMIN", TEST_MID, userAuthManager, orderInfo);

        assertNotNull(consultDetailSoOut.getUserActionAuth());
        assertEquals(2, consultDetailSoOut.getUserActionAuth().getActionsList().size());
        assertTrue(consultDetailSoOut.getUserActionAuth().getButtons().contains("PICK_UP"));
        assertTrue(consultDetailSoOut.getUserActionAuth().getButtons().contains("EDIT"));
    }

    @Test
    void constructActionList_emptyActions() {
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        when(userAuthManager.getDetailActionAuth(anyString(), any(UserConsultOrderInfo.class), anyLong())).thenReturn(Collections.emptyList());

        consultDetailSoOut.constructActionList("ADMIN", TEST_MID, userAuthManager, orderInfo);

        assertNotNull(consultDetailSoOut.getUserActionAuth());
        assertTrue(consultDetailSoOut.getUserActionAuth().getActionsList().isEmpty());
    }

    // ==================== fillCarInfo ====================

    @Test
    void fillCarInfo_success() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vid(TEST_VID)
                .vin("LMWTEST1234567890")
                .carType("SU7")
                .carImg("http://img.test/car.jpg")
                .itemMap(new HashMap<>())
                .build();

        CarUserAggGoOut carUserAgg = new CarUserAggGoOut();
        carUserAgg.setSysUserName("车主张三");
        carUserAgg.setSysUserPhone("13800000000");

        consultDetailSoOut.fillCarInfo(
                Collections.singletonList(carInfo),
                carUserAgg,
                null,
                null);

        assertNotNull(consultDetailSoOut.getCarInfo());
        assertEquals("LMWTEST1234567890", consultDetailSoOut.getVin());
        assertEquals("SU7", consultDetailSoOut.getCarInfo().getCarType());
        assertEquals("车主张三", consultDetailSoOut.getCarInfo().getCarOwner());
        assertEquals("13800000000", consultDetailSoOut.getCarInfo().getCarOwnerTel());
    }

    @Test
    void fillCarInfo_noMatchingVid_carInfoEmpty() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vid("OTHER_VID")
                .vin("OTHER_VIN")
                .build();

        consultDetailSoOut.fillCarInfo(
                Collections.singletonList(carInfo),
                null,
                null,
                null);

        assertNotNull(consultDetailSoOut.getCarInfo());
        // VIN不应被设置因为vid不匹�?
        assertNull(consultDetailSoOut.getVin());
    }

    @Test
    void fillCarInfo_withDynamicInfo() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vid(TEST_VID)
                .vin("LMWTEST1234567890")
                .carType("SU7")
                .itemMap(new HashMap<>())
                .build();

        GetDynamicInfoResponseGoOut dynamicInfo = new GetDynamicInfoResponseGoOut();
        GetDynamicInfoResponseGoOut.DynamicInfoItemDto itemDto = new GetDynamicInfoResponseGoOut.DynamicInfoItemDto();
        itemDto.setVid(TEST_VID);
        itemDto.setSysVersion("1.5.0");
        dynamicInfo.setItems(Collections.singletonList(itemDto));

        consultDetailSoOut.fillCarInfo(
                Collections.singletonList(carInfo),
                null,
                dynamicInfo,
                null);

        assertNotNull(consultDetailSoOut.getCarInfo());
        assertEquals("1.5.0", consultDetailSoOut.getCarInfo().getCurrentVersion());
    }

/*    @Test
    void fillCarInfo_withMemberInfo() {
        CarInfoGoOut carInfo = CarInfoGoOut.builder()
                .vid(TEST_VID)
                .vin("LMWTEST1234567890")
                .carType("SU7")
                .itemMap(new HashMap<>())
                .build();

        BatchMemberInfoBO memberInfoBO = BatchMemberInfoBO.builder().build();
        BatchMemberInfoBO.MemberInfoBo memberInfo = new BatchMemberInfoBO.MemberInfoBo();
        memberInfo.setVid(TEST_VID);
        memberInfo.setLevel(3);
        memberInfo.setLevelName("SVIP");
        memberInfoBO.setList(Collections.singletonList(memberInfo));

        consultDetailSoOut.fillCarInfo(
                Collections.singletonList(carInfo),
                null,
                null,
                memberInfoBO);

        assertNotNull(consultDetailSoOut.getCarInfo());
    }*/

    // ==================== addMemberLabel ====================

    @Test
    void addMemberLabel_nullMemberInfo_returnsOriginalList() {
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> labelList = new ArrayList<>();
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> result =
                ConsultDetailSoOut.addMemberLabel(null, TEST_VID, labelList);
        assertEquals(labelList, result);
    }

    @Test
    void addMemberLabel_emptyList_returnsOriginalList() {
        BatchMemberInfoBO memberInfoBO = BatchMemberInfoBO.builder().list(Collections.emptyList()).build();
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> labelList = new ArrayList<>();
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> result =
                ConsultDetailSoOut.addMemberLabel(memberInfoBO, TEST_VID, labelList);
        assertEquals(labelList, result);
    }

    @Test
    void addMemberLabel_blankVid_returnsOriginalList() {
        BatchMemberInfoBO memberInfoBO = BatchMemberInfoBO.builder().build();
        BatchMemberInfoBO.MemberInfoBo memberInfo = new BatchMemberInfoBO.MemberInfoBo();
        memberInfo.setVid(TEST_VID);
        memberInfoBO.setList(Collections.singletonList(memberInfo));

        List<com.wt.complaint.manage.api.model.resp.LabelDTO> labelList = new ArrayList<>();
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> result =
                ConsultDetailSoOut.addMemberLabel(memberInfoBO, "", labelList);
        assertEquals(labelList, result);
    }

    @Test
    void addMemberLabel_withMatchingVid_addsTag() {
        BatchMemberInfoBO memberInfoBO = BatchMemberInfoBO.builder().build();
        BatchMemberInfoBO.MemberInfoBo memberInfo = new BatchMemberInfoBO.MemberInfoBo();
        memberInfo.setVid(TEST_VID);
        memberInfo.setLevel(3);
        memberInfo.setLevelName("SVIP");
        memberInfoBO.setList(Collections.singletonList(memberInfo));

        List<com.wt.complaint.manage.api.model.resp.LabelDTO> labelList = new ArrayList<>();
        List<com.wt.complaint.manage.api.model.resp.LabelDTO> result =
                ConsultDetailSoOut.addMemberLabel(memberInfoBO, TEST_VID, labelList);

        assertFalse(result.isEmpty());
    }

    // ==================== constructStatusBar ====================

    @Test
    void constructStatusBar_waitReceiveStatus() {
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());

        consultDetailSoOut.constructStatusBar(Collections.emptyList(), orderInfo);

        assertNotNull(consultDetailSoOut.getStatusBar());
    }

    @Test
    void constructStatusBar_completedStatus() {
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.COMPLETED.getCode());

        ComplaintFollowProcessGoOut pickupProcess = ComplaintFollowProcessGoOut.builder()
                .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode())
                .createTime(new Date())
                .build();

        ComplaintFollowProcessGoOut finishProcess = ComplaintFollowProcessGoOut.builder()
                .processType(ProcessTypeEnum.AUDIT_FINISH_PASS.getProcessCode())
                .createTime(new Date())
                .build();

        consultDetailSoOut.constructStatusBar(Arrays.asList(pickupProcess, finishProcess), orderInfo);

        assertNotNull(consultDetailSoOut.getStatusBar());
    }

    @Test
    void constructStatusBar_withPickupAndDispatchOrder() {
        UserConsultOrderInfo orderInfo = buildUserConsultOrderInfo();
        orderInfo.setOrderStatus(ConsultOrderStatusEnum.WAIT_CLOSE.getCode());

        ComplaintFollowProcessGoOut pickupProcess = ComplaintFollowProcessGoOut.builder()
                .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode())
                .createTime(new Date(System.currentTimeMillis() - 10000))
                .build();

        ComplaintFollowProcessGoOut dispatchProcess = ComplaintFollowProcessGoOut.builder()
                .processType(ProcessTypeEnum.DISPATCH_ORDER.getProcessCode())
                .createTime(new Date())
                .build();

        consultDetailSoOut.constructStatusBar(Arrays.asList(pickupProcess, dispatchProcess), orderInfo);

        assertNotNull(consultDetailSoOut.getStatusBar());
    }

    // ==================== fillDetailTab ====================

    @Test
    void fillDetailTab_emptyTabs() {
        consultDetailSoOut.fillDetailTab(Collections.emptyList(), Collections.emptyList());

        assertNull(consultDetailSoOut.getTabDataList());
    }

    @Test
    void fillDetailTab_withTabs_noFollowRecords_removesFollowTab() {
        List<ConsultDetailTabEnum> tabEnums = ConsultDetailTabEnum.listTab(0, ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());

        consultDetailSoOut.fillDetailTab(tabEnums, Collections.emptyList());

        if (consultDetailSoOut.getTabDataList() != null) {
            boolean hasFollowUpTab = consultDetailSoOut.getTabDataList().stream()
                    .anyMatch(t -> ConsultDetailTabEnum.FOLLOW_UP_RECORDS.getType().equals(t.getTabCode()));
            assertFalse(hasFollowUpTab);
        }
    }

    @Test
    void fillDetailTab_withTabs_withFollowRecords_keepsFollowTab() {
        List<ConsultDetailTabEnum> tabEnums = ConsultDetailTabEnum.listTab(0, ConsultOrderStatusEnum.WAIT_CLOSE.getCode());

        ComplaintFollowProcessGoOut process = ComplaintFollowProcessGoOut.builder()
                .processType(ProcessTypeEnum.PICKUP_ORDER.getProcessCode())
                .createTime(new Date())
                .build();

        consultDetailSoOut.fillDetailTab(tabEnums, Collections.singletonList(process));

        assertNotNull(consultDetailSoOut.getTabDataList());
    }

    // ==================== CompleteInfo ====================

    @Test
    void completeInfo_builder() {
        ConsultDetailSoOut.CompleteInfo completeInfo = ConsultDetailSoOut.CompleteInfo.builder()
                .completeTime("2026-03-01 10:00:00")
                .completeUser("1001")
                .completeUserName("测试�?)
                .completeResult("已处�?)
                .solution("解决方案描述")
                .build();

        assertEquals("2026-03-01 10:00:00", completeInfo.getCompleteTime());
        assertEquals("1001", completeInfo.getCompleteUser());
        assertEquals("测试�?, completeInfo.getCompleteUserName());
        assertEquals("已处�?, completeInfo.getCompleteResult());
        assertEquals("解决方案描述", completeInfo.getSolution());
    }

    // ==================== StatusData ====================

    @Test
    void statusData_builder() {
        ConsultDetailSoOut.StatusData statusData = ConsultDetailSoOut.StatusData.builder()
                .status(1)
                .stateName("待接�?)
                .doneYn(DoneYNEnum.YES.getCode())
                .updateTime("2026-03-01 10:00:00")
                .build();

        assertEquals(1, statusData.getStatus());
        assertEquals("待接�?, statusData.getStateName());
        assertEquals(DoneYNEnum.YES.getCode(), statusData.getDoneYn());
    }

    // ==================== TabData ====================

    @Test
    void tabData_builder() {
        ConsultDetailSoOut.TabData tabData = ConsultDetailSoOut.TabData.builder()
                .tabName("跟进记录")
                .tabCode("FOLLOW_UP")
                .build();

        assertEquals("跟进记录", tabData.getTabName());
        assertEquals("FOLLOW_UP", tabData.getTabCode());
    }

    // ==================== 辅助构建方法 ====================

    private UserConsultOrderInfo buildUserConsultOrderInfo() {
        UserConsultOrderInfo info = new UserConsultOrderInfo();
        info.setConsultNo(TEST_CONSULT_NO);
        info.setConsultType(1);
        info.setVid(TEST_VID);
        info.setOrgId("F001");
        info.setOrderStatus(ConsultOrderStatusEnum.WAIT_RECEIVE.getCode());
        info.setCreateMid(TEST_MID);
        info.setOperatorMid(TEST_MID);
        info.setPriority(4);
        info.setReminderTimes(0);
        info.setCreateTime(new Date());
        return info;
    }
}
