package com.wt.complaint.manage.domain.serviceimpl;

import com.wt.complaint.manage.domain.api.gateway.interfaces.rpc.CarEmployeeRemoteGateway;
import com.wt.complaint.manage.domain.constant.CommonConst;
import com.wt.complaint.manage.domain.exception.BusinessException;
import com.wt.complaint.manage.domain.model.UserInfo;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * CustomeUserContextImpl 单元测试
 * 不启�?Spring，使�?Mock 覆盖�?RpcContext 获取用户信息逻辑
 */
@ExtendWith(MockitoExtension.class)
class CustomeUserContextImplUnitTest {

    @InjectMocks
    private CustomeUserContextImpl customeUserContext;

    @Mock
    private CarEmployeeRemoteGateway carEmployeeRemoteGateway;

    @BeforeEach
    void setUp() {
        RpcContext.getContext().clearAttachments();
    }

    @AfterEach
    void tearDown() {
        RpcContext.removeContext();
    }

    @Test
    void fromRpcContext_midBlank_throwsBusinessException() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL, "test@xiaomi.com");

        BusinessException ex = assertThrows(BusinessException.class, () -> customeUserContext.fromRpcContext());
        assertTrue(ex.getMessage().contains("未登�?));
    }

    @Test
    void fromRpcContext_midPresent_returnsUserInfo() {
        String mid = "1001";
        String email = "user@xiaomi.com";
        String role = "OPERATOR";
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, mid);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL, email);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, role);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ROLES_LIST, "[]");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_USERNAME, "测试用户");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ACCOUNT, "account");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_TRACE_ID, "trace-1");

        UserInfo userInfo = customeUserContext.fromRpcContext();

        assertNotNull(userInfo);
        assertEquals(1001L, userInfo.getMiID());
        assertEquals(email, userInfo.getEmail());
        assertEquals(role, userInfo.getCurrRole());
    }

    @Test
    void fromRpcContextForAftersaleWorkbench_emailBlank_throwsBusinessException() {
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL, "");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_MID, "1001");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customeUserContext.fromRpcContextForAftersaleWorkbench());
        assertTrue(ex.getMessage().contains("email") || ex.getMessage().contains("用户"));
    }

    @Test
    void fromRpcContextForAftersaleWorkbench_queryMidByEmailReturnsNull_throwsBusinessException() {
        String email = "aftersale@xiaomi.com";
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL, email);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, "role");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ROLES_LIST, "[]");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_USERNAME, "name");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ACCOUNT, "acc");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_TRACE_ID, "t1");

        when(carEmployeeRemoteGateway.queryMidByEmail(email)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customeUserContext.fromRpcContextForAftersaleWorkbench());
        assertTrue(ex.getMessage().contains("miID") || ex.getMessage().contains("不能为空"));
    }

    @Test
    void fromRpcContextForAftersaleWorkbench_success_returnsUserInfo() {
        String email = "aftersale@xiaomi.com";
        Long mid = 2001L;
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_EMAIL, email);
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_CURR_ROLE, "role");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ROLES_LIST, "[]");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_USERNAME, "售后用户");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_UPC_ACCOUNT, "acc");
        RpcContext.getContext().setAttachment(CommonConst.RPC_CONTEXT_TRACE_ID, "t1");

        when(carEmployeeRemoteGateway.queryMidByEmail(email)).thenReturn(mid);

        UserInfo userInfo = customeUserContext.fromRpcContextForAftersaleWorkbench();

        assertNotNull(userInfo);
        assertEquals(mid, userInfo.getMiID());
        assertEquals(email, userInfo.getEmail());
    }
}
