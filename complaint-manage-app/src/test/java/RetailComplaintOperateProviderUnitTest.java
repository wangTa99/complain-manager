import com.google.common.collect.Lists;
import com.wt.complaint.manage.api.model.Attachment;
import com.wt.complaint.manage.api.model.req.retail.RetailComplaintFinishApplyReq;
import com.wt.complaint.manage.api.model.req.retail.RetailOrgChangeApplyReq;
import com.wt.complaint.manage.api.model.resp.apply.OrgApplyResp;
import com.wt.complaint.manage.app.providerimpl.RetailComplaintOperateProviderImpl;
import com.wt.complaint.manage.domain.api.gateway.interfaces.RetailComplaintGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.EiamRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.FileRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.StoreRemoteGateway;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.CarEmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.EmployeeInfoGoOut;
import com.wt.complaint.manage.domain.api.gateway.parameter.out.RetailComplaintDetaiGoOut;
import com.wt.complaint.manage.domain.api.service.interfaces.RetailComplaintOperateService;
import com.wt.complaint.manage.domain.api.service.parameter.out.retail.apply.RetailComplaintApplySoOut;
import com.wt.complaint.manage.domain.utils.RedisUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RetailComplaintOperateProviderUnitTest {

    @InjectMocks
    private RetailComplaintOperateProviderImpl retailComplaintOperateProvider;

    @Mock
    private RetailComplaintOperateService retailComplaintOperateService;

    @Mock
    private RetailComplaintGateway retailComplaintGateway;

    @Mock
    private FileRemoteGateway fileRemoteGateway;

    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @Mock
    private EiamRemoteGateway eiamRemoteGateway;

    @Mock
    private StoreRemoteGateway storeRemoteGateway;

    @Mock
    RedissonClient redissonClient;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        RpcContext.getContext().setAttachment("$upc_miID", "3150447733");

        MockitoAnnotations.initMocks(this);
        // 修复：创�?RedisUtil 实例，调用非静�?setter 注入模拟�?RedissonClient
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedissonClient(redissonClient);

        // 创建自定义线程池（复用原�?executor 创建逻辑�?
        ThreadPoolTaskExecutor threadPoolTaskExecutor = submitChangeApplyExecutor();

        // 反射注入 applyChangeExecutor 到目标对象（替代�?setter 调用�?
        Field applyChangeExecutorField = RetailComplaintOperateProviderImpl.class.getDeclaredField("applyChangeExecutor");
        applyChangeExecutorField.setAccessible(true); // 突破私有字段访问限制
        applyChangeExecutorField.set(retailComplaintOperateProvider, threadPoolTaskExecutor); // 注入线程池实�?

        ThreadPoolTaskExecutor submitFinishApplyExecutor = submitFinishApplyExecutor();
        Field executorField = RetailComplaintOperateProviderImpl.class.getDeclaredField("executor");
        executorField.setAccessible(true); // 突破私有字段访问限制
        executorField.set(retailComplaintOperateProvider, submitFinishApplyExecutor); // 注入线程池实�?
    }


    public ThreadPoolTaskExecutor submitChangeApplyExecutor() {
        return createThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, 50, 5, 1024,
                "submitChangeApplyExecutor-", new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolTaskExecutor submitFinishApplyExecutor() {
        return createThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, 50, 5, 1024,
                "submitFinishApplyExecutor-", new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolTaskExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveSecods,
                                                           int queueCapacity, String threadNamePrefix, RejectedExecutionHandler handler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveSecods);
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(handler);
        // 异步MDC
//        executor.setTaskDecorator(new MdcDecorator());
        executor.initialize();

        return executor;
    }

    @Test
    void testSubmitChangeOrgApply() throws InterruptedException {
        // 构建请求参数
        RetailOrgChangeApplyReq req =
                RetailOrgChangeApplyReq.builder().drNo("RC256701001026680").applyOrgId("F1031").desOrgId("X5999")
                        .reassignRemark("申请改派测试").build();
        // mock获取�?
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(0, RedisUtil.TTL, TimeUnit.SECONDS)).thenReturn(true);
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        doNothing().when(mockLock).unlock();
        //mock获取投诉详情
        RetailComplaintDetaiGoOut complaintDetail = new RetailComplaintDetaiGoOut();
        complaintDetail.setOrgId("F1031");
        complaintDetail.setReassignmentTimes(0);
        complaintDetail.setOrderStatus(10);
        complaintDetail.setRiskLevel(1);
        when(retailComplaintGateway.getRetailComplaintDetail(any())).thenReturn(complaintDetail);
        // mock根据小米ID和岗位类型查询用户信�?
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = new CarEmployeeInfoGoOut();
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList = Lists.newArrayList();
        CarEmployeeInfoGoOut.StorePositionInfo storePositionInfo = new CarEmployeeInfoGoOut.StorePositionInfo(82, "F1031", "测试门店");
        storePositionInfoList.add(storePositionInfo);
        carEmployeeInfoGoOut.setStorePositionInfoList(storePositionInfoList);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(any(), any())).thenReturn(carEmployeeInfoGoOut);
        // mock根据小米id获取员工信息
        EmployeeInfoGoOut mockEmployee = new EmployeeInfoGoOut();
        mockEmployee.setName("测试员工");
        when(eiamRemoteGateway.getEmployee(anyLong())).thenReturn(mockEmployee);
        // mock获取门店信息
        when(storeRemoteGateway.getStoreListInfo(anyList())).thenReturn(Lists.newArrayList());
        // 模拟提交申请
        RetailComplaintApplySoOut soOut = new RetailComplaintApplySoOut();
        soOut.setProcessInstanceId("12345");
        when(retailComplaintOperateService.submitChangeOrgApply(any())).thenReturn(soOut);
        // 执行测试
        Result<OrgApplyResp> result = retailComplaintOperateProvider.submitChangeOrgApply(req);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
    }

    @Test
    void testSubmitFinishApply() throws InterruptedException {
        Attachment attachment = Attachment.builder()
                .id(1L)
                .fileName("测试文件")
                .type(1).build();
        List<Attachment> attachmentList = Lists.newArrayList(attachment);
        // 构建请求参数
        RetailComplaintFinishApplyReq req =
                RetailComplaintFinishApplyReq.builder().drNo("RC256701001026681").applyOrgId("F1031").isReconcile(1)
                        .canBeRevisited(1).solutionDesc("测试方案").attachmentList(attachmentList).build();
        // mock获取�?
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(0, RedisUtil.TTL, TimeUnit.SECONDS)).thenReturn(true);
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        doNothing().when(mockLock).unlock();
        //mock获取投诉详情
        RetailComplaintDetaiGoOut complaintDetail = new RetailComplaintDetaiGoOut();
        complaintDetail.setOrgId("F1031");
        complaintDetail.setReassignmentTimes(0);
        complaintDetail.setOrderStatus(10);
        complaintDetail.setRiskLevel(1);
        complaintDetail.setZoneId(1);
        complaintDetail.setLittleZoneId(1);
        when(retailComplaintGateway.getRetailComplaintDetail(any())).thenReturn(complaintDetail);
        // mock根据小米ID和岗位类型查询用户信�?
        CarEmployeeInfoGoOut carEmployeeInfoGoOut = new CarEmployeeInfoGoOut();
        List<CarEmployeeInfoGoOut.StorePositionInfo> storePositionInfoList = Lists.newArrayList();
        CarEmployeeInfoGoOut.StorePositionInfo storePositionInfo = new CarEmployeeInfoGoOut.StorePositionInfo(82, "F1031", "测试门店");
        storePositionInfoList.add(storePositionInfo);
        carEmployeeInfoGoOut.setStorePositionInfoList(storePositionInfoList);
        when(carEmployeeRemoteGateway.getEmployeeInfoV2WithChannelType(any(), any())).thenReturn(carEmployeeInfoGoOut);
        // mock根据小米id获取员工信息
        EmployeeInfoGoOut mockEmployee = new EmployeeInfoGoOut();
        mockEmployee.setName("测试员工");
        when(eiamRemoteGateway.getEmployee(anyLong())).thenReturn(mockEmployee);
        // mock获取门店信息
        when(storeRemoteGateway.getStoreNameMap(anyList())).thenReturn(new HashMap<>());
        // 模拟提交申请
        when(retailComplaintOperateService.submitFinishApply(any())).thenReturn(anyString());
        // 执行测试
        Result<OrgApplyResp> result = retailComplaintOperateProvider.submitFinishApply(req);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getCode());
    }
}
