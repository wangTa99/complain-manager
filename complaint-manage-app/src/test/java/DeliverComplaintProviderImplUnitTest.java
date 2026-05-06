import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.enums.DeliverComplaintOrderStatusEnum;
import com.wt.complaint.manage.api.model.enums.TestTagEnum;
import com.wt.complaint.manage.api.model.req.deliver.*;
import com.wt.complaint.manage.api.model.resp.deliver.DeliverComplaintListDTO;
import com.wt.complaint.manage.api.model.resp.deliver.DeliverComplaintListResp;
import com.wt.complaint.manage.app.convert.DeliverComplaintConvert;
import com.wt.complaint.manage.app.providerimpl.DeliverComplaintProviderImpl;
import com.wt.complaint.manage.domain.api.gateway.interfaces.ComplaintFollowProcessRepositoryGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.DeliverComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.DeliverComplaintService;
import com.wt.complaint.manage.domain.api.service.parameter.in.deliver.DeliverComplaintListGoIn;
import com.wt.complaint.manage.domain.api.service.parameter.out.deliver.DeliverComplaintListGoOut;
import com.wt.complaint.manage.domain.bo.DeliverComplaintBO;
import com.wt.complaint.manage.domain.model.UserInfo;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.xiaomi.youpin.infra.rpc.Result;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzheyang
 * @date 2025/7/31
 */
@ExtendWith(MockitoExtension.class)
public class DeliverComplaintProviderImplUnitTest {

    @InjectMocks
    private DeliverComplaintProviderImpl deliverComplaintProviderImpl;

    @Mock
    private DeliverComplaintService deliverComplaintService;

    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;
    // Mock依赖
    @Mock
    private DeliverComplaintConvert deliverComplaintConvert;

    @Mock
    private UserInfo userInfo;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private DeliverComplaintGateway complaintGateway;

    @Mock
    ComplaintFollowProcessRepositoryGateway followProcessGateway;

    @Mock
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // 修复：创�?RedisUtil 实例，调用非静�?setter 注入模拟�?RedissonClient
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedissonClient(redissonClient);
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setAttachment("$curr_role", "car_org_manager");
        rpcContext.setAttachment("$upc_miID", "3150409040");
    }

    @Test
    void testQueryStatisticsItems() {


//        RequestContextInfo.fillContextValue(rpcContext);

        StatisticsItemReq statisticsItemReq = new StatisticsItemReq();
        statisticsItemReq.setOrgIds("F1031");

//        when(deliverComplaintService.queryStatisticsItems(any())).thenThrow(new BusinessException(ErrorCodeEnums.BUS_ERROR));
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = new CarEmployeeInfoGoOut();
        carEmployeeInfoGoOut.setBigZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setChannelPositionInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setHeadPositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setLittleZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setStorePositionInfoList(Lists.newArrayList());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(any(), any())).thenReturn(carEmployeeInfoGoOut);
        Assertions.assertNotNull(deliverComplaintProviderImpl.queryStatisticsItems(statisticsItemReq));
    }

    @Test
    void testGetLoginPositionThrowsException() {
        Assertions.assertNotNull(deliverComplaintProviderImpl.getLoginPosition());
    }

    @Test
    void testQueryStatisticsItemsThrowsException() {
        StatisticsItemReq req = new StatisticsItemReq();
        Assertions.assertNotNull(deliverComplaintProviderImpl.queryStatisticsItems(req));
    }

    @Test
    void testListThrowsException() {
        DeliverComplaintListReq req = new DeliverComplaintListReq();
        Assertions.assertNotNull(deliverComplaintProviderImpl.list(req));
    }

    @Test
    void testApplyExemptionThrowsException() throws InterruptedException {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setAttachment("$curr_role", "car_org_manager");
        rpcContext.setAttachment("$upc_miID", "3150409040");

        DeliverComplaintApplyExemptionReq req = new DeliverComplaintApplyExemptionReq();
        req.setDrNo("DR123456");
        ArrayList<Attachment> attachments = Lists.newArrayList();
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachments.add(attachment);
        req.setApplyExemptionAttachmentList(attachments);

        // mock获取�?
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(0, RedisUtil.TTL, TimeUnit.SECONDS)).thenReturn(true);
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        doNothing().when(mockLock).unlock();

        // mock根据小米ID和岗位类型查询用户信�?
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = new CarEmployeeInfoGoOut();
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList = Lists.newArrayList();
        CarEmployeeInfoGoOut.StorePositionInfo storePositionInfo = new CarEmployeeInfoGoOut.StorePositionInfo(71, "F1031", "测试门店");
        storePositionInfoList.add(storePositionInfo);
        carEmployeeInfoGoOut.setHeadPositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setChannelPositionInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setBigZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setLittleZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setStorePositionInfoList(storePositionInfoList);
        carEmployeeInfoGoOut.setCityZonePositionInfoList(Lists.newArrayList());
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(any(), any())).thenReturn(carEmployeeInfoGoOut);

        List<DeliverComplaintBO> complaintOrderGoOutList = new ArrayList<>();
        DeliverComplaintBO complaintOrderGoOut = new DeliverComplaintBO();
        complaintOrderGoOut.setDrNo("DR123456");
        complaintOrderGoOut.setOrderStatus(DeliverComplaintOrderStatusEnum.FINISHED.getCode());
        complaintOrderGoOut.setRealFinishTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        complaintOrderGoOutList.add(complaintOrderGoOut);
        when(complaintGateway.selectListByCondition(any())).thenReturn(complaintOrderGoOutList);

        Assertions.assertNotNull(deliverComplaintProviderImpl.applyExemption(req));
    }

    @Test
    void testList() {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setAttachment("$curr_role", "car_org_manager");
        rpcContext.setAttachment("$upc_miID", "3150409040");
        // 准备测试数据
        DeliverComplaintListReq req = new DeliverComplaintListReq();
        req.setPageNum(1);
        req.setPageSize(10);
        DeliverComplaintListGoIn goIn = new DeliverComplaintListGoIn();
        goIn.setTestTag(TestTagEnum.NON_TEST.getCode());
        // Mock行为
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = new CarEmployeeInfoGoOut();
        carEmployeeInfoGoOut.setBigZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setChannelPositionInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setHeadPositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setLittleZonePositionsInfoList(Lists.newArrayList());
        carEmployeeInfoGoOut.setCityZonePositionInfoList(Lists.newArrayList());
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList = Lists.newArrayList();
        CarEmployeeInfoGoOut.StorePositionInfo storePositionInfo = new CarEmployeeInfoGoOut.StorePositionInfo(87,"","");
        storePositionInfoList.add(storePositionInfo);
        carEmployeeInfoGoOut.setStorePositionInfoList(storePositionInfoList);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(any(), any())).thenReturn(carEmployeeInfoGoOut);

        ArrayList<DeliverComplaintListGoOut> deliverComplaintListGoOutArrayList = new ArrayList<>();
        DeliverComplaintListGoOut deliverComplaintListGoOut = new DeliverComplaintListGoOut();
        deliverComplaintListGoOutArrayList.add(deliverComplaintListGoOut);
        when(deliverComplaintService.selectListByCondition(any())).thenReturn(deliverComplaintListGoOutArrayList);
        when(deliverComplaintService.selectCountByCondition(any())).thenReturn(1L);
        // 执行测试方法
        Result<DeliverComplaintListResp> result = deliverComplaintProviderImpl.list(req);
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
        assertEquals(1, result.getData().getPageNum());
        assertEquals(10, result.getData().getPageSize());
    }

    @Test
    void testExportThrowsException() {
        DeliverComplaintListReq req = new DeliverComplaintListReq();
        Assertions.assertNotNull(deliverComplaintProviderImpl.export(req));
    }

    @Test
    void testDetailThrowsException() {
        DeliverComplaintDetailReq req = new DeliverComplaintDetailReq();
        Assertions.assertNotNull(deliverComplaintProviderImpl.detail(req));
    }

    @Test
    void testFollowUpThrowsException() {
        DeliverComplaintFollowUpReq req = new DeliverComplaintFollowUpReq();
        req.setDrNo("123456");
        // mock RedisUtil的静态方�?
        try (MockedStatic<RedisUtil> mocked = Mockito.mockStatic(RedisUtil.class)) {
            mocked.when(() -> RedisUtil.tryLock(any())).thenReturn(false);
            Assertions.assertNotNull(deliverComplaintProviderImpl.followUp(req));
        }
    }

    @Test
    void testStartProcessThrowsException() {
        DeliverComplaintStartProcessReq req = new DeliverComplaintStartProcessReq();
        req.setDrNo("123456");
        // mock RedisUtil的静态方�?
        try (MockedStatic<RedisUtil> mocked = Mockito.mockStatic(RedisUtil.class)) {
            mocked.when(() -> RedisUtil.tryLock(any())).thenReturn(false);
            Assertions.assertNotNull(deliverComplaintProviderImpl.startProcess(req));
        }

    }

    @Test
    void testQueryReassignEmployeeThrowsException() {
        QueryReassignEmployeeReq req = new QueryReassignEmployeeReq();
        Assertions.assertNotNull(deliverComplaintProviderImpl.queryReassignEmployee(req));
    }

    @Test
    void testReassignThrowsException() {
        DeliverComplaintReassignReq req = new DeliverComplaintReassignReq();
        req.setDrNo("123456");
        // mock RedisUtil的静态方�?
        try (MockedStatic<RedisUtil> mocked = Mockito.mockStatic(RedisUtil.class)) {
            mocked.when(() -> RedisUtil.tryLock(any())).thenReturn(false);
            Assertions.assertNotNull(deliverComplaintProviderImpl.reassign(req));
        }
    }

    @Test
    void testFinishThrowsException() {
        DeliverComplaintFinishReq req = new DeliverComplaintFinishReq();
        req.setDrNo("123456");
        // mock RedisUtil的静态方�?
        try (MockedStatic<RedisUtil> mocked = Mockito.mockStatic(RedisUtil.class)) {
            mocked.when(() -> RedisUtil.tryLock(any())).thenReturn(false);
            Assertions.assertNotNull(deliverComplaintProviderImpl.finish(req));
        }
    }
}
